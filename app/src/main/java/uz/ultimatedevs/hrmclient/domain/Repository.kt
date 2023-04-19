package uz.ultimatedevs.hrmclient.domain

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import uz.ultimatedevs.hrmclient.data.LocalStorage
import uz.ultimatedevs.hrmclient.data.ResultData
import uz.ultimatedevs.hrmclient.data.WorkHour
import uz.ultimatedevs.hrmclient.utils.getCurrentDate
import uz.ultimatedevs.hrmclient.utils.getCurrentHour
import javax.inject.Inject

class Repository {

    private val db = Firebase.firestore

    fun checkUser(login: String, password: String) = flow<ResultData<Unit>> {
        db.collection("users").get().await().forEach {
            if (it.getString("login") == login && it.getString("password") == password) {
                LocalStorage.pref.edit().putString("user_id", it.id).apply()
                LocalStorage.pref.edit().putString("name", it.getString("name") ?: "").apply()
                emit(ResultData.Success(Unit))
                return@flow
            }
        }
        emit(ResultData.Message("Bunday foydalanuvchi mavjud emas."))
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    fun getUserWorkHistory() = flow<ResultData<List<WorkHour>>> {

        val workHours = mutableListOf<WorkHour>()

        db.collection("users").document(LocalStorage.pref.getString("user_id", "") ?: "")
            .collection("work_history").get()
            .await().forEach {
                workHours.add(
                    WorkHour(
                        it.id,
                        it.getString("date") ?: "",
                        it.getString("start_hour") ?: "",
                        it.getString("end_hour") ?: "",
                    )
                )
            }
        emit(ResultData.Success(workHours))
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    @RequiresApi(Build.VERSION_CODES.O)
    fun createStartHour() = flow<ResultData<Unit>> {
        if (LocalStorage.pref.getString("currentDate", "")!! != getCurrentDate()) {
            val map = hashMapOf(
                "date" to getCurrentDate(),
                "start_hour" to getCurrentHour(),
                "end_hour" to "00-00"
            )
            db.collection("users").document(LocalStorage.pref.getString("user_id", "") ?: "")
                .collection("work_history")
                .document().set(map).await()
            LocalStorage.pref.edit().putString("currentDate", getCurrentDate()).apply()
            emit(ResultData.Success(Unit))
        } else {
            emit(ResultData.Message("Ushbu kun uchun kelgan vaqtingiz belgilangan!"))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

    @RequiresApi(Build.VERSION_CODES.O)
    fun createEndHour() = flow<ResultData<Unit>> {
        Log.d("TTT", LocalStorage.pref.getString("currentDate", "")!!)
        Log.d("TTT", getCurrentDate())
        if ((LocalStorage.pref.getString("currentDate", "")!!) == getCurrentDate()) {
            var date: WorkHour? = null
            db.collection("users").document(LocalStorage.pref.getString("user_id", "") ?: "")
                .collection("work_history")
                .whereEqualTo("date", getCurrentDate()).get().await().forEach {
                    date = WorkHour(
                        it.id,
                        it.getString("date") ?: "",
                        it.getString("start_hour") ?: "",
                        it.getString("end_hour") ?: "",
                    )
                }
            Log.d("TTT", date.toString())
            if (date != null) {
                val map = hashMapOf(
                    "date" to date?.date,
                    "start_hour" to date?.startHour,
                    "end_hour" to getCurrentHour()
                )
                db.collection("users").document(LocalStorage.pref.getString("user_id", "") ?: "")
                    .collection("work_history")
                    .document(date!!.id).set(map).await()
                emit(ResultData.Message("Ketish vaqti saqlandi!"))
                emit(ResultData.Success(Unit))
            } else {
                emit(ResultData.Message("1 Ushbu kun uchun kelgan vaqtingiz belgilanmagan!"))
            }
        } else {
            emit(ResultData.Message("2 Ushbu kun uchun kelgan vaqtingiz belgilanmagan!"))
        }
    }.catch {
        emit(ResultData.Error(it))
    }.flowOn(Dispatchers.IO)

}