package com.store.order.bff.config

import io.grpc.health.v1.HealthCheckResponse.ServingStatus.SERVING
import io.grpc.protobuf.services.HealthStatusManager
import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GrpcHealthConfig {
    @Bean
    fun healthStatusManager() = HealthStatusManager().apply { setStatus("", SERVING) }

    @Bean
    fun healthServerConfigurer(health: HealthStatusManager) =
        GrpcServerConfigurer { serverBuilder -> serverBuilder.addService(health.healthService) }
}
