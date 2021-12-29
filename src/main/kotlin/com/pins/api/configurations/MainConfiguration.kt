package com.pins.api.configurations

import org.jobrunr.jobs.mappers.JobMapper
import org.jobrunr.storage.InMemoryStorageProvider
import org.jobrunr.storage.StorageProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MainConfiguration {
    @Bean
    fun getBackgroundJobStorageProvider(jobMapper: JobMapper): StorageProvider {
        val storageProvider = InMemoryStorageProvider()
        storageProvider.setJobMapper(jobMapper)
        return storageProvider
    }
}