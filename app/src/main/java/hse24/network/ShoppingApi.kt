package hse24.network

import hse24.network.model.CategoryItemModel
import hse24.network.model.CategoryModel
import hse24.network.model.ProductModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ShoppingApi {

    @GET("/1/category/tree")
    fun fetchCategories(): Single<CategoryModel>

    @GET("/1/c/**/*-{categoryId}/%3Fpage%3D{page}")
    fun fetchCategoriesPagedNoFilters(
        @Path("categoryId") categoryId: Int,
        @Path("pageNumber") pageNumber: Int
    ): Single<List<CategoryItemModel>>

    @GET("/1/c/**/*-{categoryId}/%3Fpage%3D{pageNumber}%26filterAttribute_motive%3D{filter}")
    fun fetchCategoriesPagedByFilter(
        @Path("categoryId") categoryId: Int,
        @Path("pageNumber") pageNumber: Int,
        @Path("filter") filter: String
    ): Single<List<CategoryItemModel>>

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

    @GET("/1/product/tree/{productSKU}")
    fun fetchProductDetailsBySku(
        @Path("productSKU") id: Int
    ): Single<ProductModel>

}
