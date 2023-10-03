package com.example.ecommerce.ui.main.store.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.ecommerce.api.ApiService
import com.example.ecommerce.model.GetProductsItemResponse

class ProductPagingSource(
    private val apiService: ApiService,
    private val token: String,
    private val search: String?,
    private val brand: String?,
    private val lowest: Int?,
    private val highest: Int?,
    private val sort: String?,

) : PagingSource<Int, GetProductsItemResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GetProductsItemResponse> {
        val currentPage = params.key ?: 1

        return try {
            val response = apiService.getProducts(
                token,
                search,
                brand,
                lowest,
                highest,
                sort,
                null,
                currentPage
            )
            if (response.code == 200) {
                val productsResponse = response.data
                val prevPage = if (currentPage == 1) null else currentPage - 1
                val nextPage =
                    if (currentPage < productsResponse.totalPages) currentPage + 1 else null

                LoadResult.Page(
                    data = productsResponse.items,
                    prevKey = prevPage,
                    nextKey = nextPage
                )
            } else {
                LoadResult.Error(Exception("Failed to load data"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GetProductsItemResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
