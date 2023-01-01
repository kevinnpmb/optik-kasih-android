package com.skripsi.optik_kasih.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import androidx.activity.viewModels
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.databinding.ActivityLoginBinding
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
                viewModel.login("erdio@gmail.com", "test12345")
//                if (BuildConfig.DEBUG && tilDomain.editText?.text!!.isEmpty()) {
//                    tilDomain.editText?.setText("mantap")
//                    tilEmail.editText?.setText("satria.sanjaya@pmberjaya.com")
//                    tilPassword.editText?.setText("test12345")
//                }
//                val type: LoginType
//                val validationList: MutableList<Boolean> = arrayListOf()
//                validationList.add(
//                    loginViewModel.validate(
//                        Constants.TYPE_OTHER,
//                        tilDomain.editText?.text.toString(),
//                        tilDomain,
//                        getString(R.string.domain_invalid)
//                    )
//                )
//                if (tilEmail.editText?.text!!.isNotBlank()) {
//                    when {
//                        Patterns.EMAIL_ADDRESS.matcher(tilEmail.editText?.text.toString())
//                            .matches() -> {
//                            //reset error message
//                            loginViewModel.validate(
//                                Constants.TYPE_OTHER,
//                                tilEmail.editText?.text.toString(),
//                                tilEmail,
//                                getString(R.string.email_invalid)
//                            )
//                            type = LoginType.Email
//                        }
//                        Patterns.PHONE.matcher(tilEmail.editText?.text.toString())
//                            .matches() -> {
//                            loginViewModel.validate(
//                                Constants.TYPE_OTHER,
//                                tilEmail.editText?.text.toString(),
//                                tilEmail,
//                                getString(R.string.phone_invalid)
//                            )
//
//                            type = LoginType.Phone
//                        }
//                        else -> {
//                            loginViewModel.validate(
//                                Constants.TYPE_OTHER,
//                                "",
//                                tilEmail,
//                                getString(R.string.email_or_phone_invalid)
//                            )
//                            return@setOnClickListener
//                        }
//                    }
//                } else {
//                    loginViewModel.validate(
//                        Constants.TYPE_OTHER,
//                        "",
//                        tilEmail,
//                        getString(R.string.email_or_phone_invalid)
//                    )
//                    return@setOnClickListener
//                }
//
//                validationList.add(
//                    loginViewModel.validate(
//                        Constants.TYPE_OTHER,
//                        tilPassword.editText?.text.toString(),
//                        tilPassword,
//                        getString(R.string.password_invalid)
//                    )
//                )
//                var isAllValid = true
//                for (i in 0 until validationList.size) {
//                    if (!validationList[i]) {
//                        isAllValid = false
//                        break
//                    }
//                }
//                if (isAllValid) {
//                    //post api
//                    btnLogin.isEnabled = false
//                    loginViewModel.postLogin(
//                        type,
//                        tilDomain.editText?.text.toString().lowercase()
//                            .removeWhitespace() + DOMAIN_SUFFIX,
//                        tilEmail.editText?.text.toString(),
//                        tilPassword.editText?.text.toString()
//                    )
//                }
            }
        }
    }

    private fun initObserver() {
        viewModel.apply {
            loginMutableLiveData.observe(this@LoginActivity) {
                when (it.status) {
                    Status.SUCCESS -> {
//                        it.data?.user?.login?.let { (token, user) ->
//                            preferencesHelper.saveAccount(user.user.toUser(token))
//                        }
                        Timber.d("${it.data?.product?.products?.nodes}")
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