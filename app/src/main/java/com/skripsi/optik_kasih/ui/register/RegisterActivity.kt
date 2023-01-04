package com.skripsi.optik_kasih.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.databinding.ActivityRegisterBinding
import com.skripsi.optik_kasih.ui.profile.MutateProfileActivity
import com.skripsi.optik_kasih.utils.Utilities
import com.skripsi.optik_kasih.utils.Utilities.validate
import com.skripsi.optik_kasih.utils.Utilities.validateAll

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListener()
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
                    if (tilConfirmPassword.editText?.text != tilPassword.editText?.text) {
                        tilConfirmPassword.error = getString(R.string.password_not_same)
                        false
                    } else {
                        tilConfirmPassword.error = null
                        true
                    }
                )
                validationList.validateAll {
                    startActivity(Intent(this@RegisterActivity, MutateProfileActivity::class.java).apply {

                    })
                }
            }
        }
    }
}