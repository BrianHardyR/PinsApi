package com.pins.api.utils


object GoogleAuthUtil {


//    private const val client_secret_path = "google_client_secret.json"
//
//    fun googleAuth(authCode: String, identifier: String, passwordEncoder: PasswordEncoder): UserModel {
//        try {
//            println("Google user\n")
//            val inputStream = ClassPathResource(client_secret_path).inputStream
//            val reader = InputStreamReader(inputStream)
//
//            val clientSecret = GoogleClientSecrets.load(
//                JacksonFactory.getDefaultInstance(),
//                reader
//            )
//
//
//            val tokenResponse = GoogleAuthorizationCodeTokenRequest(
//                NetHttpTransport(),
//                JacksonFactory.getDefaultInstance(),
//                clientSecret.details.tokenUri,
//                clientSecret.details.clientId,
//                clientSecret.details.clientSecret,
//                authCode,
//                ""
//            ).execute()
//
//
//            val googleToken = tokenResponse.parseIdToken()
//            val payload = googleToken.payload
//
//            println("\n==========================\n")
//            println(payload)
//            println("\n==========================\n")
//
//            val credential = Credential(
//                provider = CredentialProvider.GOOGLE,
//                identifier = payload.email,
//                secret = passwordEncoder.encode(tokenResponse.accessToken),
//                accessToken = tokenResponse.accessToken,
//                refreshToken = tokenResponse.refreshToken,
//                providerId = identifier,
//                thirdPartyId = payload["sub"].toString()
//            )
//
//            return UserModel(
//                userName = payload.email.split("@")[0],
//                name = payload["given_name"].toString(),
//                otherNames = listOf(payload["family_name"].toString()),
//                profileImage = payload["picture"].toString(),
//                credentials = ArrayList(listOf(credential))
//            )
//        } catch (e: Exception) {
//            throw CredentialsException("Google authentication error")
//        }
//
//
//    }
//
//    fun refreshToken(refresh: String): GoogleTokenResponse {
//
//        val inputStream = ClassPathResource(client_secret_path).inputStream
//        val reader = InputStreamReader(inputStream)
//
//        val clientSecret = GoogleClientSecrets.load(
//            JacksonFactory.getDefaultInstance(),
//            reader
//        )
//
//        return GoogleRefreshTokenRequest(
//            NetHttpTransport(),
//            JacksonFactory.getDefaultInstance(),
//            refresh,
//            clientSecret.details.clientId,
//            clientSecret.details.clientSecret
//        ).execute()
//
//    }


}
