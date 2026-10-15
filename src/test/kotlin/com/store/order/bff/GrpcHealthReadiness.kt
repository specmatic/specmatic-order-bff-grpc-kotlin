package com.store.order.bff

import io.grpc.ManagedChannelBuilder
import io.grpc.health.v1.HealthCheckRequest
import io.grpc.health.v1.HealthCheckResponse.ServingStatus.SERVING
import io.grpc.health.v1.HealthGrpc
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import java.time.Duration
import java.util.concurrent.TimeUnit

internal fun waitForGrpcHealth(port: Int) {
    val channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build()
    try {
        await().atMost(Duration.ofSeconds(30)).pollInterval(Duration.ofMillis(250)).untilAsserted {
            val response = HealthGrpc.newBlockingStub(channel)
                .withDeadlineAfter(2, TimeUnit.SECONDS)
                .check(HealthCheckRequest.getDefaultInstance())
            assertThat(response.status).isEqualTo(SERVING)
        }
    } finally {
        channel.shutdownNow()
    }
}
