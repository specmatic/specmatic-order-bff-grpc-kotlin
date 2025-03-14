# Specmatic Order BFF gRPC Sample

This sample project demonstrates [Specmatic](https://specmatic.io/) **gRPC support** which allows you to use your **proto files for Contract Testing and Intelligent Service Virtualisation (stubbing/mocking)**.
[Specmatic](https://specmatic.io/) **gRPC support** can also help you use your proto files for #nocode backward compatibility testing and more.

![Specmatic gRPC Sample Project Architecture](assets/SpecmaticGRPCSupport.gif)

The **specmatic-order-bff-grpc-kotlin** is a gRPC server application developed according to the following proto file, which can be found in the central contract repository:
* [bff.proto](https://github.com/znsio/specmatic-order-contracts/blob/main/io/specmatic/examples/store/grpc/order_bff.proto)

This BFF project relies on the [OrderAPI domain service](https://github.com/znsio/specmatic-order-api-grpc-kotlin) which implements the following proto files:
* [order.proto](https://github.com/znsio/specmatic-order-contracts/blob/main/io/specmatic/examples/store/grpc/order_api/order.proto)
* [product.proto](https://github.com/znsio/specmatic-order-contracts/blob/main/io/specmatic/examples/store/grpc/order_api/product.proto)

The `ContractTest` class demonstrates how to use Specmatic to test **specmatic-order-bff-grpc-kotlin** gRPC server app using the above proto files.

## Requirements

- Java 17 or later

## Project Setup

1. Clone the repository
   ```shell
   git clone https://github.com/znsio/specmatic-order-bff-grpc-kotlin
   ```
   
2. Initialize and update the `specmatic-order-contracts` submodule

   ```shell
   git submodule update --init --recursive --remote
   ```

3. Enable automatic submodule updating when executing `git pull`

   ```shell
   git config submodule.recurse true
   ```

## Running Contract Tests

### Using Gradle

   ```shell
   ./gradlew clean test   
   ```

### Using Docker

1. Start the Specmatic gRPC stub server to emulate domain service:

   ```shell
   docker run -p 9090:9090 -v "$(pwd)/specmatic.yaml:/usr/src/app/specmatic.yaml" znsio/specmatic-grpc virtualize --port=9090
   ```

2. Build and run the BFF service (System Under Test) in a Docker container:

   ```shell
   docker build --no-cache -t specmatic-order-bff-grpc .
   ```

   ```shell
   docker run --network host -p 8085:8085 specmatic-order-bff-grpc
   ```

   Or run it using `./gradlew bootRun`

3. Finally, run Specmatic Contract on the BFF service (System Under Test):

   ```shell
   docker run --network host -v "$(pwd)/specmatic.yaml:/usr/src/app/specmatic.yaml" -v "$(pwd)/build/reports/specmatic:/usr/src/app/build/reports/specmatic" -e SPECMATIC_GENERATIVE_TESTS=true znsio/specmatic-grpc test --port=8085 --host=host.docker.internal
   ```

## Developer notes

The BFF service can be independently started with below command.

   ```shell
   ./gradlew bootRun
   ```

And now you can debug / test by using [grpcurl](https://github.com/fullstorydev/grpcurl) to verify the setup.

   ```shell
   grpcurl -plaintext -d '{"type": "OTHER", "pageSize": 10}' localhost:8085 com.store.order.bff.OrderService/findAvailableProducts
   ```

Which should give you results as shown below.
   ```
   %  grpcurl -plaintext -d '{"type": "OTHER", "pageSize": 10}' localhost:8085 com.store.order.bff.OrderService/findAvailableProducts
   {
     "products": [
       {
         "id": 608,
         "name": "PXDIO",
         "type": "GADGET",
         "inventory": 148
       }
     ]
   }
   ```

Alternatively you can also use [grpcui](https://github.com/fullstorydev/grpcui) to debug the service.

   ```shell
   grpcui -plaintext localhost:8085
   ```

Also observe corresponding logs in the Specmatic Stub Server which is emulating domain service to understand the interactions between BFF and Domain and Service.
