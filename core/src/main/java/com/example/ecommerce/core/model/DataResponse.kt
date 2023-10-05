package com.example.ecommerce.core.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

data class Auth(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("firebaseToken")
    val firebaseToken: String?,
)

data class DataResponse(
    @SerializedName("id")
    var code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: ResultResponse?,
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
    val expiresAt: Int,
)

data class ProfileResponse(
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: ProfileResultResponse?,
)

data class ProfileResultResponse(
    @SerializedName("userName")
    val userName: String,
    @SerializedName("userImage")
    val userImage: String? = null,
)

data class TokenRequest(
    @field:SerializedName("token")
    val token: String?,
)

data class RefreshResponse(
    @field:SerializedName("code")
    val code: Int,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("data")
    val data: RefreshDataResponse?,
)

data class RefreshDataResponse(
    @field:SerializedName("accessToken")
    val accessToken: String,
    @field:SerializedName("refreshToken")
    val refreshToken: String,
    @field:SerializedName("expiresAt")
    val expiresAt: Int,
)

// STORE
data class GetProductResponse(
    val code: Int,
    val message: String,
    val data: GetProductsResultResponse,
)

data class GetProductsResultResponse(
    val itemsPerPage: Int,
    val currentItemCount: Int,
    val pageIndex: Int,
    val totalPages: Int,
    val items: List<GetProductsItemResponse>,
)

data class GetProductsItemResponse(
    val productId: String,
    val productName: String,
    val productPrice: Int,
    val image: String,
    val brand: String,
    val store: String,
    val sale: Int,
    val productRating: Float,
)

data class ProductDetailParam(
    val token: String,
    val productId: String? = null,
)

data class ProductReviewParam(
    val token: String,
    val productId: String? = null,
)

data class ProductParam(
    val token: String,
    val search: String? = null,
    val brand: String? = null,
    val lowest: Int? = null,
    val highest: Int? = null,
    val sort: String? = null,
)

data class SearchResponse(
    val code: Int,
    val message: String,
    val data: List<String>,
)

// DETAIL PRODUCT
data class GetProductDetailResponse(
    val code: Int,
    val message: String,
    val data: GetProductDetailItemResponse,
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
    val productVariant: List<ProductVariant>,
)

data class ProductVariant(
    var variantName: String,
    val variantPrice: Int,
)

// REVIEW PRODUCT
data class GetProductReviewResponse(
    val code: Int,
    val message: String,
    val data: List<GetProductReviewItemResponse>,
)

data class GetProductReviewItemResponse(
    val userName: String,
    val userImage: String,
    val userRating: Int,
    val userReview: String,
)

@Keep
@Entity
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
    var selected: Boolean = false,
) : Parcelable

fun GetProductDetailItemResponse.asProductLocalDb(
    variantName: String?,
    variantPrice: Int?,
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

@Keep
@Entity
@Parcelize
data class WishlistProduct(
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
    var selected: Boolean = false,
) : Parcelable

fun GetProductDetailItemResponse.asWishlistProduct(
    variantName: String?,
    variantPrice: Int?,
): WishlistProduct {
    return WishlistProduct(
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

data class PaymentMethodResponse(
    val code: Int,
    val message: String,
    val data: List<PaymentMethodCategoryResponse>,
)

data class PaymentMethodCategoryResponse(
    val title: String,
    val item: List<PaymentMethodItemResponse>,
)

@Keep
@Parcelize
data class PaymentMethodItemResponse(
    val label: String,
    val image: String,
    val status: Boolean,
) : Parcelable

data class Payment(
    val payment: String,
    val items: @RawValue List<PaymentItem>,
)

@Keep
@Parcelize
data class PaymentItem(
    val productId: String,
    val variantName: String,
    val quantity: Int,
) : Parcelable

@Keep
@Parcelize
data class PaymentResponse(
    val code: Int,
    val message: String,
    val data: @RawValue PaymentDataResponse?,
) : Parcelable

@Keep
@Parcelize
data class PaymentDataResponse(
    val invoiceId: String,
    val status: Boolean,
    val date: String,
    val time: String,
    val payment: String,
    val total: Int,
    val review: String?,
    val rating: Int?,
) : Parcelable

fun TransactionDataResponse.asPaymentDataResponse(
    review: String?,
    rating: Int?,
): PaymentDataResponse {
    return PaymentDataResponse(
        invoiceId,
        status,
        date,
        time,
        payment,
        total,
        review.toString(),
        rating
    )
}

data class Rating(
    val invoiceId: String,
    val rating: Int? = null,
    val review: String? = null,
)

data class RatingResponse(
    val code: String,
    val message: String,
)

data class TransactionResponse(
    val code: String,
    val message: String,
    val data: List<TransactionDataResponse>,
)

@Keep
@Parcelize
data class TransactionDataResponse(
    val invoiceId: String,
    val status: Boolean,
    val date: String,
    val time: String,
    val payment: String,
    val total: Int,
    val items: @RawValue List<PaymentItem>?,
    val rating: Int,
    val review: String?,
    val image: String,
    val name: String,
) : Parcelable

@Keep
@Entity
@Parcelize
data class CheckoutProduct(
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
    var selected: Boolean = false,
) : Parcelable

fun ProductLocalDb.asCheckoutProduct(
    variantName: String?,
    variantPrice: Int?,
): CheckoutProduct {
    return CheckoutProduct(
        productId,
        productName,
        productPrice,
        image,
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
        variantPrice,
        quantity
    )
}

@Keep
@Parcelize
data class ListCheckout(
    val listCheckout: List<CheckoutProduct>? = emptyList(),
) : Parcelable

@Entity(tableName = "notification_table", primaryKeys = ["id"])
@Keep
@Parcelize
data class Notification(
    val id: String,
    val type: String,
    val date: String,
    val title: String,
    val body: String,
    val image: String,
    var isRead: Boolean,
) : Parcelable

data class FilterResults(
    val sort: String?,
    val category: String?,
    val lowest: String?,
    val highest: String?,
)