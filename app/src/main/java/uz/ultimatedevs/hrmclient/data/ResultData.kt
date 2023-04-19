package uz.ultimatedevs.hrmclient.data

sealed class ResultData<out T> {
    data class Message<T>(val message: String) : ResultData<T>()
    data class Success<T>(val data: T) : ResultData<T>()
    data class Error<T>(val error: Throwable) : ResultData<T>()

}