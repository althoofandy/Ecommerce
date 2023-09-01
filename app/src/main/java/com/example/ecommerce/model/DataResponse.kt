package com.example.ecommerce.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class Auth(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("firebaseToken")
    val firebaseToken: String
)

data class DataResponse(
    @SerializedName("id")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: ResultResponse?
)

data class ResultResponse(
    @SerializedName("userName")
    val userName: String,
    @SerializedName("userImage")
    val userImage: String,
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("expiresAt")
    val expiresAt: Int
)

data class ProfileResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: ProfileResultResponse?
)

data class ProfileResultResponse(
    @SerializedName("userName")
    val userName: String,
    @SerializedName("userImage")
    val userImage: String? = null
)

data class TokenRequest(
    @field:SerializedName("token")
    val token: String?
)

data class RefreshResponse(
    @field:SerializedName("code")
    val code: Int,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("data")
    val data: RefreshDataResponse?
)

data class RefreshDataResponse(
    @field:SerializedName("accessToken")
    val accessToken: String,
    @field:SerializedName("refreshToken")
    val refreshToken: String,
    @field:SerializedName("expiresAt")
    val expiresAt: Int
)
//STORE
data class GetProductResponse(
    val code: Int,
    val message: String,
    val data: GetProductsResultResponse
)

data class GetProductsResultResponse(
    val itemsPerPage: Int,
    val currentItemCount: Int,
    val pageIndex: Int,
    val totalPages: Int,
    val items: List<GetProductsItemResponse>
)

data class GetProductsItemResponse(
    val productId: String,
    val productName: String,
    val productPrice: Int,
    val image: String,
    val brand: String,
    val store: String,
    val sale: Int,
    val productRating: Float
)

data class ProductParam(
    val token: String,
    val search: String? = null,
    val brand: String? = null,
    val lowest: Int? = null,
    val highest: Int? = null,
    val sort: String? = null
)

data class SearchResponse(
    val code: Int,
    val message: String,
    val data: List<String>
)
//DETAIL PRODUCT
data class GetProductDetailResponse(
    val code: Int,
    val message: String,
    val data: GetProductDetailItemResponse
)

data class GetProductDetailItemResponse(
    val productId: String,
    val productName: String,
    val productPrice: Int,
    val image: List<String>,
    val brand: String,
    val description: String,
    val store: String,
    val sale: Int,
    val stock: Int,
    val totalRating: Int,
    val totalReview: Int,
    val totalSatisfaction: Int,
    val productRating: Float,
    val productVariant: List<ProductVariant>
)

data class ProductVariant(
    val variantName: String,
    val variantPrice: Int
)

//REVIEW PRODUCT
data class GetProductReviewResponse(
    val code: Int,
    val message: String,
    val data: List<GetProductReviewItemResponse>
)

data class GetProductReviewItemResponse(
    val userName : String,
    val userImage : String,
    val userRating : Int,
    val userReview : String
)

@Entity(tableName = "product_database")
@Parcelize
data class ProductLocalDb(
    @PrimaryKey
    val productId: String,
    val productName: String,
    val productPrice: Int,
    val image: String,
    val brand: String,
    val description: String,
    val store: String,
    val sale: Int,
    val stock: Int?,
    val totalRating: Int,
    val totalReview: Int,
    val totalSatisfaction: Int,
    val productRating: Float,
    var variantName: String,
    var variantPrice: Int?,
    var quantity: Int = 1,
    var selected: Boolean = false
) : Parcelable

fun GetProductDetailItemResponse.asProductLocalDb(
    variantName: String?,
    variantPrice: Int?
): ProductLocalDb {
    return ProductLocalDb(
        productId,
        productName,
        productPrice,
        image.firstOrNull().toString(),
        brand,
        description,
        store,
        sale,
        stock,
        totalRating,
        totalReview,
        totalSatisfaction,
        productRating,
        variantName.toString(),
        variantPrice
    )
}










