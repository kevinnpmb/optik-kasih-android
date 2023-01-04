package com.skripsi.optik_kasih.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import androidx.activity.viewModels
import com.skripsi.optik_kasih.BuildConfig
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.databinding.ActivityLoginBinding
import com.skripsi.optik_kasih.ui.common.BaseActivity
import com.skripsi.optik_kasih.ui.main.MainActivity
import com.skripsi.optik_kasih.utils.PreferencesHelper
import com.skripsi.optik_kasih.utils.Utilities
import com.skripsi.optik_kasih.utils.Utilities.toUser
import com.skripsi.optik_kasih.utils.Utilities.validate
import com.skripsi.optik_kasih.utils.Utilities.validateAll
import com.skripsi.optik_kasih.vo.Status
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
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
                    tilEmail.validate(
                        Utilities.TextFieldType.EMAIL,
                        getString(R.string.email_invalid)
                    )
                )
                validationList.add(
                    tilPassword.validate(
                        Utilities.TextFieldType.OTHER,
                        getString(R.string.password_invalid)
                    )
                )
                validationList.validateAll {
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
                binding.btnLogin.isEnabled = it.status != Status.LOADING
                when (it.status) {
                    Status.SUCCESS -> {
                        loadingDialog.dismiss()
                        it.data?.customer?.login?.let { (token, user) ->
                            preferencesHelper.saveAccount(user.customer.toUser(token))
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    }
                    Status.ERROR -> {
                        loadingDialog.dismiss()
                        Utilities.showToast(
                            this@LoginActivity,
                            binding.root,
                            it.message,
                            Utilities.ToastType.ERROR
                        )
                    }
                    Status.LOADING -> {
                        loadingDialog.show()
                    }
                    Status.UNAUTHORIZED -> {
                        Utilities.showInvalidApiKeyAlert(this@LoginActivity)
                    }
                }
            }
        }
    }
}