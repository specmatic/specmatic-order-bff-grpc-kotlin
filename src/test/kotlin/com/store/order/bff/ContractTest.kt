package com.store.order.bff

import io.specmatic.grpc.HOST
import io.specmatic.grpc.IMPORT_PATHS
import io.specmatic.grpc.PORT
import io.specmatic.grpc.PROTOC_VERSION
import io.specmatic.grpc.SPECMATIC_GENERATIVE_TESTS
import io.specmatic.grpc.VersionInfo
import io.specmatic.grpc.junit.SpecmaticGrpcContractTest
import io.specmatic.grpc.stub.GrpcStub
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ContractTest : SpecmaticGrpcContractTest {
    companion object {
        private var grpcStub: GrpcStub? = null

        @JvmStatic
        @BeforeAll
        fun setup() {
            println("Using specmatic gRPC version ${VersionInfo.version}")
            System.setProperty(HOST, "localhost")
            System.setProperty(PORT, "8085")
            System.setProperty(SPECMATIC_GENERATIVE_TESTS, "false")
            System.setProperty(PROTOC_VERSION, "3.23.4")
            // This path is relative to the specified proto file in specmatic config file
            System.setProperty(IMPORT_PATHS, "../")
            grpcStub = GrpcStub.createGrpcStub(9090)
            grpcStub?.start()
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            grpcStub?.stop()
        }
    }
}
