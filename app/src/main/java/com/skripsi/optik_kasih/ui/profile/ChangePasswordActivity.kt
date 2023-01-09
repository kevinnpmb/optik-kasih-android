package com.skripsi.optik_kasih.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.databinding.ActivityChangePasswordBinding
import com.skripsi.optik_kasih.ui.common.BaseActivity
import com.skripsi.optik_kasih.utils.Utilities
import com.skripsi.optik_kasih.utils.Utilities.registerClearText
import com.skripsi.optik_kasih.utils.Utilities.validate
import com.skripsi.optik_kasih.utils.Utilities.validateAll
import com.skripsi.optik_kasih.vo.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private val viewModel: ChangePasswordViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Utilities.initToolbar(this, binding.toolbar.toolbar, getString(R.string.change_password), hideBack = false, hideCart = true)
        initObserver()
        initListener()
        registerClearText()
    }

    private fun initListener() {
        binding.apply {
            btnSave.setOnClickListener {
                val validationList: MutableList<Boolean> = arrayListOf()
                validationList.add(
                    tilOldPassword.validate(
                        Utilities.TextFieldType.OTHER,
                        getString(R.string.invalid_old_password)
                    )
                )
                validationList.add(
                    tilNewPassword.validate(
                        Utilities.TextFieldType.OTHER,
                        getString(R.string.invalid_new_password)
                    )
                )
                validationList.add(
                    validationList.add(
                        when {
                            tilConfirmPassword.editText?.text.toString().isBlank() -> {
                                tilConfirmPassword.error = getString(R.string.invalid_confirm_password)
                                false
                            }
                            tilConfirmPassword.editText?.text.toString() != tilNewPassword.editText?.text.toString() -> {
                                tilConfirmPassword.error = getString(R.string.password_not_same)
                                false
                            }
                            else -> {
                                tilConfirmPassword.error = null
                                true
                            }
                        }
                    )
                )
                validationList.validateAll {
                    //post api
                    btnSave.isEnabled = false
                    viewModel.changePassword(
                        tilOldPassword.editText?.text.toString(),
                        tilNewPassword.editText?.text.toString(),
                    )
                }
            }
        }
    }

    private fun initObserver() {
        viewModel.apply {
            changePasswordMutableLiveData.observe(this@ChangePasswordActivity) {
                binding.btnSave.isEnabled = it.status != Status.LOADING
                when (it.status) {
                    Status.SUCCESS -> {
                        loadingDialog.dismiss()
                        Toast.makeText(
                            this@ChangePasswordActivity,
                            getString(R.string.change_password_success),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                    Status.ERROR -> {
                        loadingDialog.dismiss()
                        Utilities.showToast(
                            this@ChangePasswordActivity,
                            binding.root,
                            it.message,
                            Utilities.ToastType.ERROR
                        )
                    }
                    Status.LOADING -> {
                        loadingDialog.show()
                    }
                    Status.UNAUTHORIZED -> {
                        Utilities.showInvalidApiKeyAlert(this@ChangePasswordActivity)
                    }
                }
            }
        }
    }

    private fun registerClearText() {
        binding.apply {
            tilConfirmPassword.registerClearText()
            tilNewPassword.registerClearText()
            tilOldPassword.registerClearText()
        }
    }
}