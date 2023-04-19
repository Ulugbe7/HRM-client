package uz.ultimatedevs.hrmclient.data

data class User(
    val id: String,
    val name: String,
    val login: String,
    val password: String,
    val profession: String
)