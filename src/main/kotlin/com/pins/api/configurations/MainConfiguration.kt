package com.pins.api.configurations

import org.jobrunr.jobs.mappers.JobMapper
import org.jobrunr.storage.StorageProvider
import org.jobrunr.storage.sql.common.SqlStorageProviderFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.jdbc.DatabaseDriver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MainConfiguration {

    @Value("\${job.db.url}") lateinit var dbUrl : String

    @Value("\${job.db.username}") lateinit var dbUser : String

    @Value("\${job.db.password}") lateinit var dbPassword : String


    @Bean
    fun getBackgroundJobStorageProvider(jobMapper: JobMapper): StorageProvider {


        print("Data source config $dbUrl\n$dbUser\n$dbPassword")

        val datasource = DataSourceBuilder.create()
            .url(dbUrl)
            .driverClassName(DatabaseDriver.MYSQL.driverClassName)
            .username(dbUser)
            .password(dbPassword)
            .build()

        val storageProvider = SqlStorageProviderFactory.using(datasource)
        storageProvider.setJobMapper(jobMapper)
        return storageProvider
    }
}