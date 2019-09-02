package hse24.network

import hse24.network.model.CatalogPageApiModel
import hse24.network.model.CategoryApiModel
import hse24.network.model.ProductApiModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ShoppingApi {

    @GET("1/category/tree")
    fun fetchCategories(): Single<CategoryApiModel>

    @GET("1/c/**/*-{categoryId}/%3Fpage%3D{pageNumber}")
    fun fetchCategoryCatalog(
        @Path("categoryId") categoryId: Int,
        @Path("pageNumber") pageNumber: Int
    ): Single<CatalogPageApiModel>

    @GET("1/c/**/*-{categoryId}/%3Fpage%3D2{pageNumber}%26filterAttribute_motive%3D{filter}")
    fun fetchCategoriesPagedByFilter(
        @Path("categoryId") categoryId: Int,
        @Path("pageNumber") pageNumber: Int,
        @Path("filter") filter: String
    ): Single<CatalogPageApiModel>

    /*
      ?                          =
    [%3F filterAttribute_motive %3D {filter}] - ?filerAttribute_motive={filter}
      ?            =
    [%3F pageSize %3D 20] - ?pageSize=20
      ?        =
    [%3F page %3D 2] - ?page=2

    & = %26

    /1/c/**/*-{categoryId}/?filterAttribute_motive={filer}&pageSize=20&page=2
                            ?        =          &                          =
    /1/c/**/*-{categoryId}/%3F page %3D {page} %26 filterAttribute_motive %3D {filter}
     */

    @GET("1/product/tree/{productSKU}")
    fun fetchProductDetailsBySku(
        @Path("productSKU") id: Int
    ): Single<ProductApiModel>

}
