package com.pins.api.repo

import com.pins.api.entities.Credential
import com.pins.api.entities.CredentialProvider
import org.springframework.data.neo4j.repository.Neo4jRepository
import java.util.*


interface CredentialsRepo : Neo4jRepository<Credential, Long> {
   fun findOneByIdentifierAndSecretAndActiveAndProvider(identifier : String, secret : String, active : Boolean = true , provider: CredentialProvider = CredentialProvider.EMAIL_PASSWORD ):Credential
   fun findByIdentifier(identifier: String) : Optional<Credential>
}