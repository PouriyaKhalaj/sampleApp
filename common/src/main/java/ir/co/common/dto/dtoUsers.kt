package ir.co.common.dto

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "users")
@Parcelize
@Keep
data class User(
    @PrimaryKey
    @Keep val id: Long,
    @Keep val email: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @Keep val avatar: String
) : Parcelable {
    class DiffUtils : DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(
            p0: User,
            p1: User
        ): Boolean = p0.id == p1.id

        override fun areContentsTheSame(
            p0: User,
            p1: User
        ): Boolean = false
    }
}

@Keep
data class UsersResponse(
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @Keep val data: MutableList<User>,
    @Keep val total: Int,
    @Keep val page: Int,
)

data class UsersResponseLive(
    val data: LiveData<PagedList<User>>,
    val onEmptyList: LiveData<EmptyMessage>,
    val networkState: LiveData<NetworkState>,
    val endListMessage: LiveData<EmptyMessage>
)
