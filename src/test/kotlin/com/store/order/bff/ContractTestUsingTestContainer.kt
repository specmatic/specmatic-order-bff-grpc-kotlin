package com.store.order.bff

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.images.PullPolicy.alwaysPull
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest
@EnabledIf(value = "isNonCIOrLinux", disabledReason = "Run only on Linux in CI; all platforms allowed locally")
class ContractTestUsingTestContainer {
    companion object {

        @JvmStatic
        fun isNonCIOrLinux(): Boolean = System.getenv("CI") != "true" || System.getProperty("os.name").lowercase().contains("linux")

        @Container
        private val stubContainer: GenericContainer<*> =
            GenericContainer("specmatic/enterprise")
                .withImagePullPolicy(alwaysPull())
                .withCommand("mock")
                .withFileSystemBind("./src", "/usr/src/app/src", BindMode.READ_ONLY)
                .withFileSystemBind("./specmatic.yaml", "/usr/src/app/specmatic.yaml", BindMode.READ_ONLY)
                .withNetworkMode("host")
                .waitingFor(Wait.forLogMessage(".*\\[Specmatic gRPC\\] Server started, listening on.*", 1))
                .withLogConsumer { print(it.utf8String) }
    }

    private val testContainer: GenericContainer<*> =
        GenericContainer("specmatic/enterprise")
            .withImagePullPolicy(alwaysPull())
            .withCommand("test")
            .withFileSystemBind("./src", "/usr/src/app/src", BindMode.READ_ONLY)
            .withFileSystemBind("./specmatic.yaml", "/usr/src/app/specmatic.yaml", BindMode.READ_ONLY)
            .withNetworkMode("host")
            .waitingFor(Wait.forLogMessage(".*Tests run:.*", 1))
            .withLogConsumer { print(it.utf8String) }

    @Test
    fun specmaticContractTest() {
        testContainer.start()
        val hasSucceeded = testContainer.logs.contains("Result: FAILED").not()
        assertThat(hasSucceeded).isTrue()
    }
}
