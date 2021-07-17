package com.pins.api.services

import com.pins.api.entities.*
import com.pins.api.exceptions.*
import com.pins.api.repo.AccountsRepo
import com.pins.api.repo.CredentialsRepo
import com.pins.api.repo.UserRepo
import org.springframework.beans.factory.annotation.Autowired
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
                    secret = request.credential.password
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
        if (credentialRef.isPresent) {
            throw UserExistsException("email ${request.credential.email} is already taken")
        }
        // user does not exist proceed to create account
        val accountModel = Account(
            type = type,
            owner = user
        )
        return accountsRepo.save(accountModel).also {
            println(it.toString())
        }
    }

    /**
     * Assing user to account
     */
    fun assignUserToAccount(request: AssignAccountRequest): Account {

        val userRef = userRepo.findById(request.userId)
        if (!userRef.isPresent) throw UserNotFound("user with id ${request.userId} not found")
        val user = userRef.get()
        val accountRef = accountsRepo.findByTypeAndOwnerId(request.type, user.ID!!)
        if (accountRef.isPresent) throw AccountExisits("account of type ${request.type} for user ${request.userId} already exisits")
        val accountModel = Account(
            type = request.type,
            owner = userRef.get()
        )
        return accountsRepo.save(accountModel)

    }

    /**
     * Link user to another user's account
     */

    fun linkUserToAccount(request: LinkAccountRequest): Account {
        val accountRef = accountsRepo.findById(request.accountID)
        if (!accountRef.isPresent) throw AccountNotFound("account of id ${request.accountID} not found")
        val userRef = userRepo.findById(request.userIdToLink)
        if (!userRef.isPresent) throw UserNotFound("user with id ${request.userIdToLink} not found")
        val account = accountRef.get()
        val user = userRef.get()
        val accountRoleExists =
            account.accountUsers.firstOrNull { userAccountRoles -> userAccountRoles.userModel.ID == user.ID && userAccountRoles.role == request.role }
        if (accountRoleExists != null) throw AccountRoleExists("user already has a role")

        val userAccountRoles = UserAccountRoles(
            role = request.role,
            userModel = userRef.get()
        )

        return accountsRepo.save(account.apply {
            accountUsers.add(userAccountRoles)
        })

    }


    /**
     * Create account or sign in user
     * check if email exists
     * if not create
     * else sign in
     */
    fun loginAndAssignToken(request: EmailPasswordAuthRequest) {
        val credential = credentialsRepo.findOneByIdentifierAndSecretAndActiveAndProvider(
            identifier = request.email,
            secret = request.password,
            active = true,
            provider = CredentialProvider.EMAIL_PASSWORD
        )
        val user = userRepo.findUserByCredentialId(credential.ID!!)

    }

}

/**
 * Models used by the service
 */


data class EmailPasswordAuthRequest(
    var email: String,
    var password: String,
)

data class APIAuthRequest(
    var apiKey: String,
    var appName: String
)

data class AccountCreationRequest(
    var userName: String,
    var name: String,
    var otherNames: List<String>,
    var credential: EmailPasswordAuthRequest,
)

data class AssignAccountRequest(
    val userId: Long,
    val type: AccountType
)

data class LinkAccountRequest(
    val roleOrdinal: Int,
    val userIdToLink: Long,
    val accountID: Long
) {
    val role: Roles get() = Roles.values()[roleOrdinal]
}