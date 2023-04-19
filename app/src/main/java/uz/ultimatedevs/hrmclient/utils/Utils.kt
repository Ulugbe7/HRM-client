package uz.ultimatedevs.hrmclient.utils

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import uz.ultimatedevs.hrmclient.App
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date

@SuppressLint("SimpleDateFormat")
fun getCurrentDate(): String = SimpleDateFormat("dd-MM-yyy").format(Date())

@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentHour(): String = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

fun toast(msg: String) {
    Toast.makeText(
        App.instance,
        msg,
        Toast.LENGTH_SHORT
    ).show()
}