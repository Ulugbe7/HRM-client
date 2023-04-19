package uz.ultimatedevs.hrmclient.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
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
import uz.ultimatedevs.hrmclient.databinding.ScreenHomeBinding
import uz.ultimatedevs.hrmclient.domain.Repository
import uz.ultimatedevs.hrmclient.utils.toast
import javax.inject.Inject

class HomeScreen : Fragment(R.layout.screen_home) {

    private val repo = Repository()
    private val binding by viewBinding(ScreenHomeBinding::bind)
    private val navController by lazy(LazyThreadSafetyMode.NONE) { findNavController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher
            .addCallback(this) {
                requireActivity().finish()
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.txtName.text = LocalStorage.pref.getString("name", "")

        binding.apply {
            btnEnterStartHour.clicks().debounce(100).onEach {
                repo.createStartHour().onEach {
                    when (it) {
                        is ResultData.Success -> {
                            toast(
                                "Kelgan vaqtingiz saqlandi!",
                            )
                        }
                        is ResultData.Message -> {
                            toast(it.message)
                        }
                        is ResultData.Error -> {

                        }
                    }
                }.launchIn(viewLifecycleOwner.lifecycleScope)
            }.launchIn(viewLifecycleOwner.lifecycleScope)

            btnEnterEndHour.clicks().debounce(100).onEach {
                repo.createEndHour().onEach {
                    when (it) {
                        is ResultData.Success -> {
                            toast(
                                "Ketgan vaqtingiz saqlandi!",
                            )
                        }
                        is ResultData.Message -> {
                            toast(it.message)
                        }
                        is ResultData.Error -> {

                        }
                    }
                }.launchIn(viewLifecycleOwner.lifecycleScope)
            }.launchIn(viewLifecycleOwner.lifecycleScope)

            btnShowHistory.clicks().debounce(100).onEach {
                navController.navigate(HomeScreenDirections.actionHomeScreenToInfoScreen())
            }.launchIn(viewLifecycleOwner.lifecycleScope)

            btnLogOut.clicks().debounce(100).onEach {
                navController.navigate(HomeScreenDirections.actionHomeScreenToLoginScreen())
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        }

    }
}