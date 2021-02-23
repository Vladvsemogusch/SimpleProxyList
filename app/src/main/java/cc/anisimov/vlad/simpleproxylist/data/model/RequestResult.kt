package cc.anisimov.vlad.simpleproxylist.data.model

sealed class RequestResult<out R> {

    data class Success<out T>(val data: T) : RequestResult<T>()
    data class Error(val exception: Throwable?) : RequestResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[errorData=${exception?.message ?: "empty" }]"
        }
    }
}