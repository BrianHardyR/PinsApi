package com.pins.api.entities
import com.pins.api.companion.Validatable
import com.pins.api.utils.now
import com.pins.api.utils.toLong
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node

/**
 * This model saves the [UserModel]'s credentials
 * @sample
 * <p>
 * Credential(
 * var provider : DEFAULT,
 * var identifier : user@email.com,
 * var secret : password,
 * var active : true
 * )
 * </p>
 * For [CredentialProvider.API]
 *  @param identifier refers to the application name or id
 *  @param secret refers to the API Key
 * For [CredentialProvider.EMAIL_PASSWORD]
 *  @param identifier refers to the email
 *  @param secret refers to the password or token
 */

@Node("Credential")
data class Credential(
        @Id @GeneratedValue
        var ID : Long? = null,
        var identifier : String,
        var createdAt : Long = now().toLong(),
        var provider : CredentialProvider = CredentialProvider.EMAIL_PASSWORD,
        var secret : String,
        var active : Boolean = true,
        val accessToken : String = "",
        val refreshToken : String = "",
        val providerId : String = "",
        val thirdPartyId : String = ""
):Validatable {
        override fun valid(): Boolean = identifier.isNotBlank() && secret.isNotBlank() && active
}

/**
 * Defined the different types of credentials supported by the app
 */
enum class CredentialProvider{
    EMAIL_PASSWORD,API,GOOGLE
}