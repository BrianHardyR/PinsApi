package com.pins.api.services


import com.pins.api.entities.*
import com.pins.api.exceptions.*
import com.pins.api.repo.AccountsRepo
import com.pins.api.repo.CredentialsRepo
import com.pins.api.repo.UserRepo
import com.pins.api.security.AppUserDetails
import com.pins.api.utils.GoogleAuthUtil
import com.pins.api.utils.JwtTokenUtil
import com.pins.api.utils.getLoggedInUserDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * This service handles all authentication related functions
 * From sign up to account deactivation
 */

@Service
class AuthenticationService {

    @Autowired
    lateinit var userRepo: UserRepo

    @Autowired
    lateinit var credentialsRepo: CredentialsRepo

    @Autowired
    lateinit var accountsRepo: AccountsRepo

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    lateinit var jwtTokenUtil: JwtTokenUtil

    /**
     * Create [UserModel] Object for saving to database
     */
    fun createUser() {}

    /**
     * Create [UserAcount] Object for saving to database
     * @param type [AccountType]
     * @return [Account]
     */
    fun createAccount(request: AccountCreationRequest, type: AccountType): Account {
        val user = UserModel(
            userName = request.userName,
            name = request.name,
            otherNames = request.otherNames,
            credentials = arrayListOf(
                Credential(
                    provider = CredentialProvider.EMAIL_PASSWORD,
                    identifier = request.credential.email,
                    secret = passwordEncoder.encode(request.credential.password)
                )
            )
        )

        // account username and credentials are unique
        // validate

        val userRef = userRepo.findByUserName(user.userName)
        if (userRef.isPresent) {
            throw UserExistsException("username ${user.userName} is already taken")

        }
        val credentialRef = credentialsRepo.findByIdentifier(request.credential.email)
        if (credentialRef.isNotEmpty()) {
            throw UserExistsException("email ${request.credential.email} is already taken")
        }
        // user does not exist proceed to create account
        val accountModel = Account(
            type = type,
            owner = user
        ).apply {
            accountUsers.add(
                UserAccountRoles(
                    role = Roles.OWNER,
                    userModel = user,
                )
            )
        }
        return accountsRepo.save(accountModel).also {
            println(it.toString())
        }
    }

    /**
     * Assing user to account
     */
    fun assignUserToAccount(request: AssignAccountRequest): Account {

        val user = getUserById(request.userId)
        val accountRef = accountsRepo.findByTypeAndOwnerId(request.type, user.ID!!)
        if (accountRef.isPresent) throw AccountExisits("account of type ${request.type} for user ${request.userId} already exisits")
        val accountModel = Account(
            type = request.type,
            owner = user
        )
        return accountsRepo.save(accountModel)

    }

    /**
     * Link user to another user's account
     */

    fun linkUserToAccount(request: LinkAccountRequest): Account {

        val account = getLoggedInUserDetails().account
        val user = getUserById(request.userIdToLink)
        val accountRoleExists =
            account.accountUsers.firstOrNull { userAccountRoles -> userAccountRoles.userModel.ID == user.ID && userAccountRoles.role == request.role }
        if (accountRoleExists != null) throw AccountRoleExists("user already has a role")



        return accountsRepo.save(account.apply {
            accountUsers.add(
                UserAccountRoles(
                    role = request.role,
                    userModel = user
                )
            )
        })

    }


    fun unlinkUserFromAccount(request: LinkAccountRequest): Boolean {
        val account = getLoggedInUserDetails().account
        val user = getUserById(request.userIdToLink)
        val accountRole =
            account.accountUsers.firstOrNull { userAccountRoles -> userAccountRoles.userModel.ID == user.ID && userAccountRoles.role != Roles.OWNER }
                ?: throw AccountRoleNotFound("user already has a role")

        return account.accountUsers.remove(accountRole)

    }


    /**
     * Create account or sign in user
     * check if email exists
     * if not create
     * else sign in
     */
    fun loginAndAssignToken(request: EmailPasswordAuthRequest): AppUserDetails {

//        val user = userRepo.findUserByCredentialId(credential.ID!!)

        val authenticate = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                "${request.email}:${CredentialProvider.EMAIL_PASSWORD}",
                request.password
            )
        )


        return authenticate.principal as AppUserDetails
    }


    fun switchAccount(request: SwitchAccountRequest): AppUserDetails {

        val currentUserDetails = SecurityContextHolder.getContext().authentication.principal.let {
            print("switch request")
            print(it)
            it
        } as AppUserDetails
        val user = currentUserDetails.user
        val account = getAccountById(request.accountID)
        val accountsRoles = accountsRepo.findAccountRoleByUserIdAndAccountId(user.ID!!, account.ID!!)
        val linkedAccounts = accountsRepo.findAccountsByUserId(user.ID!!)
        val userDetails = AppUserDetails(user, user.credentials.first(), account, listOf(accountsRoles), linkedAccounts)

        SecurityContextHolder.getContext()
            .authentication = UsernamePasswordAuthenticationToken(
            SecurityContextHolder.getContext().authentication.principal,
            SecurityContextHolder.getContext().authentication.credentials,
            userDetails.authorities
        )

        return userDetails
    }

    fun getDataFromAccount(): AppUserDetails {

        return getLoggedInUserDetails()

    }

    fun getToken(appUser: AppUserDetails) = jwtTokenUtil.generateToken(appUser)

    fun getUserById(userId: Long): UserModel {
        val userRef = userRepo.findById(userId)
        if (!userRef.isPresent) throw UserNotFound("user not found")
        return userRef.get()
    }

    fun getUserByUserName(username: String): UserModel {
        val userRef = userRepo.findByUserName(username)
        if (!userRef.isPresent) throw UserNotFound("user not found")
        return userRef.get()
    }

    fun getAccountById(accountID: Long): Account {
        val accountRef = accountsRepo.findById(accountID)
        if (!accountRef.isPresent) throw AccountNotFound("account not found")
        return accountRef.get()
    }

    /**
     * Use Google Outh to create user account and login
     * if user exists with email and password log then in
     */

    fun signInWithGoogle(googleToken: String, providerId: String = ""): AppUserDetails {
        println("google login")
        if (!providerId.isEmpty()) {
            println("provider id available")
            val credentialRef = credentialsRepo.findByProviderIdAndActiveAndProvider(providerId)
            if (credentialRef.isPresent) {
                val credential = credentialRef.get()
                println(credential)
                val authenticate = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(
                        "${credential.identifier}:${credential.provider}",
                        credential.accessToken
                    )
                )
                return authenticate.principal as AppUserDetails

            }
        }


        var userModel = GoogleAuthUtil.googleAuth(googleToken, providerId, passwordEncoder)
        val credentials = credentialsRepo.findByIdentifier(userModel.credentials.first().identifier)

        // user exists
        // ask user to link by entering their password
        if (credentials.isNotEmpty()) {

            val googleCredentials = credentials.filter { it.provider == CredentialProvider.GOOGLE }
            if (googleCredentials.isNotEmpty()) {
                // account exisits return account
                userModel = userRepo.findUserByCredentialId(googleCredentials.first().ID!!)
                val accountRef = accountsRepo.findByTypeAndOwnerId(AccountType.DEFAULT, userModel.ID!!)
                if (!accountRef.isPresent) throw AccountNotFound("account not found")

                val authenticate = authenticationManager.authenticate(
                    with(googleCredentials[0]) {
                        UsernamePasswordAuthenticationToken(
                            "$identifier:${CredentialProvider.GOOGLE}",
                            accessToken
                        )
                    }
                )

                return authenticate.principal as AppUserDetails


            }

            val emailCredentials = credentials.filter { it.provider == CredentialProvider.EMAIL_PASSWORD }
            if (emailCredentials.isNotEmpty()) with(credentials.first()) {
                throw UserExistsException(
                    "user with email : $identifier",
                    accessToken
                )
            }
        }

        // user does not exist
        var account = Account(
            owner = userModel
        )

        account = accountsRepo.save(account)
        val role = UserAccountRoles(
            role = Roles.OWNER,
            userModel = account.owner!!
        )
        account.accountUsers.add(role)
        account = accountsRepo.save(account)
        val authenticate = authenticationManager.authenticate(
            with(userModel.credentials.first()) {
                UsernamePasswordAuthenticationToken(
                    "$identifier:${CredentialProvider.GOOGLE}",
                    accessToken
                )
            }
        )

        return authenticate.principal as AppUserDetails
    }

    fun linkWithGoogle(googleToken: String, providerId: String = ""): UserModel {
        val credentialRef = credentialsRepo.findByProviderIdAndActiveAndProvider(providerId)
        if (credentialRef.isPresent) throw CredentialsException("already linked with a google account")
        val userModel = GoogleAuthUtil.googleAuth(googleToken, providerId, passwordEncoder)
        val user = getLoggedInUserDetails().user

        val credential = credentialsRepo.save(userModel.credentials[0])
        user.credentials.add(credential)
        return userRepo.save(user)
    }

    fun unlinkCredential(credentialId: Long): AppUserDetails {

        val userDetails = getLoggedInUserDetails()

        if (userDetails.credential.ID == credentialId) throw CredentialsException("You cannot unlink this credential please log out and use another credential")
        val credentialToDelete = userDetails.user.credentials.firstOrNull { it.ID == credentialId }

        credentialsRepo.delete(
            credentialToDelete
                ?: throw CredentialsException("You cannot unlink this credential please log out and use another credential")
        )

        val authenticate = authenticationManager.authenticate(
            with(userDetails.credential) {
                UsernamePasswordAuthenticationToken(
                    "$identifier:${provider}",
                    when (provider) {
                        CredentialProvider.EMAIL_PASSWORD -> secret
                        else -> accessToken
                    }
                )
            }

        )

        return authenticate.principal as AppUserDetails

    }


    fun refreshToken() {

    }

    fun logout() {

    }


}

/**
 * Models used by the service
 */


data class EmailPasswordAuthRequest(
    var email: String,
    var password: String
)

data class GoogleAuthRequest(
    val serverCode: String,
    val googleId: String,
    val authKey: String
)

data class APIAuthRequest(
    var apiKey: String,
    var appName: String
)

data class AccountCreationRequest(
    var userName: String,
    var name: String,
    var otherNames: List<String>,
    var credential: EmailPasswordAuthRequest
)

data class AssignAccountRequest(
    val userId: Long,
    val type: AccountType
)

data class LinkAccountRequest(
    val roleOrdinal: Int = 0,
    val userIdToLink: Long,
    val accountID: Long? = null
) {
    val role: Roles get() = Roles.values()[roleOrdinal]
}

data class SwitchAccountRequest(
    val accountID: Long
)

data class UnlinkCredentialRequest(
    val credentialId: Long
)

data class UnlinkAccountRequest(
    val accountID: Long
)