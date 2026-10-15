package com.store.order.bff

import io.specmatic.enterprise.SpecmaticContractTest
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ContractTest : SpecmaticContractTest {
    @BeforeEach
    fun awaitApplicationHealth() = waitForGrpcHealth(8085)
}
