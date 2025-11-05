package com.store.order.bff

import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import io.specmatic.grpc.SPECMATIC_GENERATIVE_TESTS
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest
@EnabledIf(value = "isNonCIOrLinux", disabledReason = "Run only on Linux in CI; all platforms allowed locally")
class ContractTestUsingTestContainer {
    companion object {
        private const val GRPC_STUB_PORT = 9090

        @JvmStatic
        fun isNonCIOrLinux(): Boolean = System.getenv("CI") != "true" || System.getProperty("os.name").lowercase().contains("linux")

        @Container
        private val stubContainer: GenericContainer<*> =
            GenericContainer("specmatic/specmatic-grpc")
                .withCommand(
                    "virtualize",
                    "--port=$GRPC_STUB_PORT",
                    "--protoc-version=3.23.4",
                ).withEnv(SPECMATIC_GENERATIVE_TESTS, "true")
                .withCreateContainerCmdModifier { cmd ->
                    cmd.hostConfig?.withPortBindings(
                        PortBinding(Ports.Binding.bindPort(GRPC_STUB_PORT), ExposedPort(GRPC_STUB_PORT)),
                    )
                }.withExposedPorts(GRPC_STUB_PORT)
                .withFileSystemBind(
                    "./specmatic.yaml",
                    "/usr/src/app/specmatic.yaml",
                    BindMode.READ_ONLY,
                ).withFileSystemBind(
                    "./build/reports/specmatic",
                    "/usr/src/app/build/reports/specmatic",
                    BindMode.READ_WRITE,
                ).waitingFor(Wait.forLogMessage(".*gRPC Stub server is running on.*", 1))
                .withLogConsumer { print(it.utf8String) }
    }

    private val testContainer: GenericContainer<*> =
        GenericContainer("specmatic/specmatic-grpc")
            .withCommand(
                "test",
                "--host=localhost",
                "--port=8085",
                "--protoc-version=3.23.4",
            ).withEnv(SPECMATIC_GENERATIVE_TESTS, "true")
            .withFileSystemBind(
                "./specmatic.yaml",
                "/usr/src/app/specmatic.yaml",
                BindMode.READ_ONLY,
            ).withFileSystemBind(
                "./build/reports/specmatic",
                "/usr/src/app/build/reports/specmatic",
                BindMode.READ_WRITE,
            ).waitingFor(Wait.forLogMessage(".*Failed Tests.*", 1))
            .withNetworkMode("host")
            .withLogConsumer { print(it.utf8String) }

    @Test
    fun specmaticContractTest() {
        testContainer.start()
        val hasSucceeded = testContainer.logs.contains("Result: FAILED").not()
        assertThat(hasSucceeded).isTrue()
    }
}
