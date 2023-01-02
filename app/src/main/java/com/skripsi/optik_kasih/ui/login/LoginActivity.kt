package com.skripsi.optik_kasih.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import androidx.activity.viewModels
import com.skripsi.optik_kasih.BuildConfig
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.databinding.ActivityLoginBinding
import com.skripsi.optik_kasih.ui.main.MainActivity
import com.skripsi.optik_kasih.utils.PreferencesHelper
import com.skripsi.optik_kasih.utils.Utilities.toUser
import com.skripsi.optik_kasih.vo.Status
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initObserver()
        initListener()
    }

    private fun initListener() {
        binding.apply {
            btnLogin.setOnClickListener {
                if (BuildConfig.DEBUG && tilEmail.editText?.text!!.isEmpty()) {
                    tilEmail.editText?.setText("erdio@gmail.com")
                    tilPassword.editText?.setText("test12345")
                }
                val validationList: MutableList<Boolean> = arrayListOf()
                validationList.add(
                    viewModel.validate(
                        LoginViewModel.TextFieldType.EMAIL,
                        tilPassword.editText?.text.toString(),
                        tilPassword,
                        getString(R.string.email_invalid)
                    )
                )

                validationList.add(
                    viewModel.validate(
                        LoginViewModel.TextFieldType.OTHER,
                        tilPassword.editText?.text.toString(),
                        tilPassword,
                        getString(R.string.password_invalid)
                    )
                )

                var isAllValid = true
                for (i in 0 until validationList.size) {
                    if (!validationList[i]) {
                        isAllValid = false
                        break
                    }
                }
                if (isAllValid) {
                    //post api
                    btnLogin.isEnabled = false
                    viewModel.login(
                        tilEmail.editText?.text.toString(),
                        tilPassword.editText?.text.toString()
                    )
                }
            }
        }
    }

    private fun initObserver() {
        viewModel.apply {
            loginMutableLiveData.observe(this@LoginActivity) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.user?.login?.let { (token, user) ->
                            preferencesHelper.saveAccount(user.user.toUser(token))
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    }
                    Status.ERROR -> {

                    }
                    Status.LOADING -> {

                    }
                    Status.UNAUTHORIZED -> {

                    }
                }
            }
        }
    }
}