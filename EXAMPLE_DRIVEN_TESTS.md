## How do the example driven contract tests run in this project?

### The components involved:

- [Proto file](./spec/order_bff.proto)
- [JSON examples](./spec/order_bff_examples)
- [Specmatic configuration](./specmatic.yaml)
- [Contract test](./src/test/kotlin/com/store/order/bff/ContractTestUsingTestContainer.kt)

### How it all works together?

1. You need to set the `SPECMATIC_GENERATIVE_TESTS` to `false` in contract tests.
2. Specmatic then generates tests which are only based off the examples.

#### If you run the [contract test](./src/test/kotlin/com/store/order/bff/ContractTestUsingTestContainer.kt) you should only see the following 3 tests -
- [+ve] Scenario: com.store.order.bff.OrderService/createOrder with the request from the example 'createOrder.json' where the body contains all the keys
- [+ve] Scenario: com.store.order.bff.OrderService/createProduct with the request from the example 'createProduct.json' where the body contains all the keys
- [+ve] Scenario: com.store.order.bff.OrderService/findAvailableProducts with the request from the example 'findAvailableProducts.json' where the body contains all the keys

