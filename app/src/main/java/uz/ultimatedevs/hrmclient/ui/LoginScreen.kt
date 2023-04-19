package uz.ultimatedevs.hrmclient.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.ultimatedevs.hrmclient.R
import uz.ultimatedevs.hrmclient.data.ResultData
import uz.ultimatedevs.hrmclient.databinding.ScreenLoginBinding
import uz.ultimatedevs.hrmclient.domain.Repository
import uz.ultimatedevs.hrmclient.utils.toast

class LoginScreen : Fragment(R.layout.screen_login) {

    private val repo = Repository()
    private val binding by viewBinding(ScreenLoginBinding::bind)
    private val navController by lazy(LazyThreadSafetyMode.NONE) { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.btnLogin.setOnClickListener {
            if (binding.inputLogin.text.isNotEmpty() && binding.inputPassword.text.isNotEmpty()) {
                repo.checkUser(
                    binding.inputLogin.text.toString(),
                    binding.inputPassword.text.toString()
                ).onEach {
                    when (it) {
                        is ResultData.Success -> {
                            navController.navigate(LoginScreenDirections.actionLoginScreenToHomeScreen())
                        }
                        is ResultData.Message -> {
                            toast(it.message)
                        }
                        is ResultData.Error -> {

                        }
                    }
                }.launchIn(viewLifecycleOwner.lifecycleScope)
            }
        }
    }
}