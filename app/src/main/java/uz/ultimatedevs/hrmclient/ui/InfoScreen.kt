package uz.ultimatedevs.hrmclient.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks
import uz.ultimatedevs.hrmclient.R
import uz.ultimatedevs.hrmclient.data.LocalStorage
import uz.ultimatedevs.hrmclient.data.ResultData
import uz.ultimatedevs.hrmclient.data.WorkHour
import uz.ultimatedevs.hrmclient.databinding.ScreenInfoBinding
import uz.ultimatedevs.hrmclient.domain.Repository
import uz.ultimatedevs.hrmclient.ui.adapter.WorkHistoryAdapter
import java.text.SimpleDateFormat

class InfoScreen : Fragment(R.layout.screen_info) {

    private val repo = Repository()
    private val adapter = WorkHistoryAdapter()
    private val binding by viewBinding(ScreenInfoBinding::bind)
    private val navController by lazy(LazyThreadSafetyMode.NONE) { findNavController() }
    private val works = mutableListOf<WorkHour>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.btnBack.clicks().debounce(100).onEach {
            navController.navigateUp()
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding.rvWorkHistory.adapter = adapter
        binding.txtName.text = LocalStorage.pref.getString("name", "")

        repo.getUserWorkHistory().onEach {
            when (it) {
                is ResultData.Success -> {
                    works.clear()
                    works.addAll(it.data)
                    adapter.submitList(it.data)
                }
                is ResultData.Message -> {

                }
                is ResultData.Error -> {

                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding.btnCalculate.clicks().debounce(100).onEach {
            binding.containerPrice.visibility = View.VISIBLE
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding.btnOK.clicks().debounce(100).onEach {
            val price = binding.inputHoursPrice.text.toString().replaceFirst("^0+", "").toInt()
            var allHours = 0f

            works.forEach {

                val dateFormat = SimpleDateFormat("HH:mm")
                val date1 = dateFormat.parse(it.startHour)
                val date2 = dateFormat.parse(it.endHour)
                val duration = date2!!.time - date1!!.time

//                val startTime = LocalTime.parse(it.startHour)
//                val endTime = LocalTime.parse(it.endHour)
//                val duration = Duration.between(startTime, endTime)

                val oneH = duration / 3600000f

                Log.d("TTT", duration.toString())
                Log.d("TTT", oneH.toString())
                allHours += oneH
            }

            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Xodim maoshi")
            builder.setMessage("Ishlangan vaqt: $allHours\nHisoblangan mablag': ${allHours * price}")

            builder.setPositiveButton("Ok") { dialogInterface, i ->
                binding.containerPrice.visibility = View.GONE
                dialogInterface.dismiss()
            }
            builder.setCancelable(false)
            builder.create()
            builder.show()

        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}
