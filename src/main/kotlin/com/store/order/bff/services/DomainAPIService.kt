package com.store.order.bff.services

import com.store.order.bff.proto.*
import com.store.order.proto.OrderServiceGrpc
import com.store.product.proto.ProductServiceGrpc
import net.devh.boot.grpc.client.inject.GrpcClient
import org.springframework.stereotype.Service
import com.store.order.proto.NewOrder as ServiceNewOrder
import com.store.order.proto.OrderStatus as ServiceOrderStatus
import com.store.product.proto.NewProduct as ServiceNewProduct
import com.store.product.proto.ProductSearchRequest as ServiceProductSearchRequest
import com.store.product.proto.ProductType as ServiceProductType

@Service
class DomainAPIService {
    @GrpcClient("domain-api-service")
    lateinit var orderServiceGrpc: OrderServiceGrpc.OrderServiceBlockingStub

    @GrpcClient("domain-api-service")
    lateinit var productServiceGrpc: ProductServiceGrpc.ProductServiceBlockingStub

    fun createOrder(newOrder: NewOrder): OrderId {
        val orderId = orderServiceGrpc.addOrder(
            ServiceNewOrder.newBuilder().setProductId(newOrder.productId).setCount(newOrder.count)
                .setStatus(ServiceOrderStatus.PENDING).build()
        )
        return OrderId.newBuilder().setId(orderId.id).build()
    }

    fun findProducts(findAvailableProductsRequest: findAvailableProductsRequest): ProductListResponse {
        val searchProductRequest =
            ServiceProductSearchRequest.newBuilder().setType(
                ServiceProductType.forNumber(findAvailableProductsRequest.typeValue)
            ).build()
        val products = productServiceGrpc.searchProducts(searchProductRequest)
        val response = products.productsList.map {
            Product.newBuilder().setId(it.id).setName(it.name).setInventory(it.inventory).setType(
                ProductType.forNumber(it.typeValue)
            ).build()
        }
        println("sending request to findProducts $findAvailableProductsRequest")
        return ProductListResponse.newBuilder().addAllProducts(response).build()
    }

    fun createProduct(newProduct: NewProduct): ProductId {
        val productId = productServiceGrpc.addProduct(
            ServiceNewProduct.newBuilder().setName(newProduct.name).setType(
                ServiceProductType.forNumber(newProduct.typeValue)
            ).setInventory(newProduct.inventory).build()
        )
        return ProductId.newBuilder().setId(productId.id).build()
    }
}