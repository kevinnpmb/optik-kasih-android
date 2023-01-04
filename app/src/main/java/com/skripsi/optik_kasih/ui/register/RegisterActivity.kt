package com.skripsi.optik_kasih.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.databinding.ActivityRegisterBinding
import com.skripsi.optik_kasih.ui.login.LoginActivity
import com.skripsi.optik_kasih.ui.profile.MutateProfileActivity
import com.skripsi.optik_kasih.utils.Utilities
import com.skripsi.optik_kasih.utils.Utilities.registerClearText
import com.skripsi.optik_kasih.utils.Utilities.validate
import com.skripsi.optik_kasih.utils.Utilities.validateAll

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Utilities.initToolbar(
            this, binding.toolbar.toolbar, "",
            hideBack = false,
            hideCart = true,
        )
        registerClearText()
        initListener()
    }

    private fun registerClearText() {
        binding.apply {
            tilEmail.registerClearText()
            tilPassword.registerClearText()
            tilConfirmPassword.registerClearText()
        }
    }

    private fun initListener() {
        binding.apply {
            btnNext.setOnClickListener {
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
                validationList.add(
                    when {
                        tilConfirmPassword.editText?.text.toString().isBlank() -> {
                            tilConfirmPassword.error = getString(R.string.confirm_password_invalid)
                            false
                        }
                        tilConfirmPassword.editText?.text.toString() != tilPassword.editText?.text.toString() -> {
                            tilConfirmPassword.error = getString(R.string.password_not_same)
                            false
                        }
                        else -> {
                            tilConfirmPassword.error = null
                            true
                        }
                    }
                )
                validationList.validateAll {
                    startActivity(
                        Intent(
                            this@RegisterActivity,
                            MutateProfileActivity::class.java
                        ).apply {
                            putExtra(
                                MutateProfileActivity.MUTATE_PROFILE_DATA,
                                MutateProfileActivity.MutateProfileData(
                                    tilEmail.editText?.text.toString(),
                                    tilPassword.editText?.text.toString(),
                                )
                            )
                        })
                }
            }

            login.setOnClickListener {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }
        }
    }
}