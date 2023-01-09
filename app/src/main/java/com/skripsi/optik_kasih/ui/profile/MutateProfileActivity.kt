package com.skripsi.optik_kasih.ui.profile

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.databinding.ActivityMutateProfileBinding
import com.skripsi.optik_kasih.ui.common.BaseActivity
import com.skripsi.optik_kasih.ui.login.LoginActivity
import com.skripsi.optik_kasih.utils.Utilities
import com.skripsi.optik_kasih.utils.Utilities.parcelable
import com.skripsi.optik_kasih.utils.Utilities.registerClearText
import com.skripsi.optik_kasih.utils.Utilities.validate
import com.skripsi.optik_kasih.utils.Utilities.validateAll
import com.skripsi.optik_kasih.vo.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import java.util.*

@AndroidEntryPoint
class MutateProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityMutateProfileBinding
    private val viewModel: MutateProfileViewModel by viewModels()
    private val datePicker = MaterialDatePicker.Builder.datePicker()
        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        .setTitleText(R.string.select_dates)
        .setCalendarConstraints(
            CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now())
                .setStart(
                    Calendar.getInstance().apply {
                        set(1980, 0, 1)
                    }.timeInMillis
                )
                .setEnd(
                    Calendar.getInstance().apply {
                        add(Calendar.MONTH, 2)
                        set(Calendar.DAY_OF_MONTH, -1)
                    }.timeInMillis
                ).build()
        )
        .build().apply {
            addOnPositiveButtonClickListener {
                viewModel.birthDateMutableLiveData.postValue(Calendar.getInstance().apply {
                    timeInMillis = selection ?: MaterialDatePicker.todayInUtcMilliseconds()
                }.time)
            }
            addOnNegativeButtonClickListener {
                dismiss()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMutateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerClearText()
        initObserver()
        initListener()
        (intent.parcelable(MUTATE_PROFILE_DATA) as MutateProfileData?)?.let {
            Utilities.initToolbar(
                this@MutateProfileActivity, binding.toolbar.toolbar,
                getString(
                    when (it.mutateType) {
                        MutateType.EDIT -> R.string.edit_profile
                        MutateType.CREATE -> R.string.register
                    }
                ),
                hideBack = false,
                hideCart = true,
            )
            viewModel.mutateProfileData.postValue(it)
        }
    }

    private fun registerClearText() {
        binding.apply {
            tilEmail.registerClearText()
            tilName.registerClearText()
            tilBirthDate.registerClearText()
            tilPhoneNumber.registerClearText()
        }
    }

    private fun initListener() {
        binding.apply {
            tilBirthDate.setOnClickListener {
                datePicker.show(supportFragmentManager, "dp-dob")
            }

            tilBirthDate.editText?.setOnClickListener {
                datePicker.show(supportFragmentManager, "dp-dob")
            }

            tilBirthDate.setEndIconOnClickListener {
                datePicker.show(supportFragmentManager, "dp-dob")
            }

            btnSave.setOnClickListener {
                mutableListOf<Boolean>().apply {
                    add(
                        tilName.validate(
                            Utilities.TextFieldType.OTHER,
                            getString(R.string.invalid_name)
                        )
                    )
                    add(
                        tilBirthDate.validate(
                            Utilities.TextFieldType.OTHER,
                            getString(R.string.invalid_birth_date)
                        )
                    )
                    add(
                        tilPhoneNumber.validate(
                            Utilities.TextFieldType.PHONE,
                            getString(R.string.invalid_phone_number)
                        )
                    )
                }.validateAll {
                    when (viewModel.mutateType) {
                        MutateType.EDIT -> {
                            viewModel.mutateProfileData.value?.let {
                                viewModel.birthDate?.let { date ->
                                    viewModel.editProfile(
                                        preferencesHelper.user?.id!!,
                                        tilName.editText?.text.toString(),
                                        date,
                                        when (rgGender.checkedRadioButtonId) {
                                            R.id.male -> 1
                                            R.id.female -> 2
                                            else -> return@validateAll
                                        },
                                        tilPhoneNumber.editText?.text.toString(),
                                    )
                                }
                            }
                        }
                        MutateType.CREATE -> {
                            viewModel.mutateProfileData.value?.let {
                                viewModel.birthDate?.let { date ->
                                    viewModel.register(
                                        tilName.editText?.text.toString(),
                                        date,
                                        when (rgGender.checkedRadioButtonId) {
                                            R.id.male -> 1
                                            R.id.female -> 2
                                            else -> return@validateAll
                                        },
                                        tilPhoneNumber.editText?.text.toString(),
                                        it.email!!,
                                        it.password!!,
                                    )
                                }
                            }
                        }
                        null -> {}
                    }
                }
            }
        }
    }

    private fun initObserver() {
        viewModel.apply {
            mutateProfileData.observe(this@MutateProfileActivity) { (email, _, name, birthDate, gender, phoneNumber, mutateType) ->
                binding.apply {
                    btnSave.setText(
                        when (mutateType) {
                            MutateType.EDIT -> R.string.save
                            MutateType.CREATE -> R.string.register
                        }
                    )
                    tilEmail.editText?.apply {
                        setText(email)
                        isEnabled = false
                    }
                    tilPhoneNumber.editText?.setText(phoneNumber)
                    tilName.editText?.setText(name)
                    viewModel.birthDateMutableLiveData.postValue(birthDate)
                    tilBirthDate.editText?.setText(
                        Utilities.formatToDateString(
                            birthDate,
                            Utilities.DateType.SIMPLE
                        )
                    )
                    rgGender.check(if (gender == 2) R.id.female else R.id.male)
                }
            }

            registerMutableLiveData.observe(this@MutateProfileActivity) {
                binding.apply {
                    btnSave.isEnabled = it.status != Status.LOADING
                    when (it.status) {
                        Status.SUCCESS -> {
                            loadingDialog.dismiss()
                            Toast.makeText(
                                this@MutateProfileActivity, getString(
                                    R.string.success_add_account
                                ), Toast.LENGTH_LONG
                            ).show()
                            startActivity(
                                Intent(
                                    this@MutateProfileActivity,
                                    LoginActivity::class.java
                                )
                            )
                            finish()
                        }
                        Status.ERROR -> {
                            loadingDialog.dismiss()
                            Utilities.showToast(
                                this@MutateProfileActivity,
                                binding.root,
                                it.message,
                                Utilities.ToastType.ERROR
                            )
                        }
                        Status.LOADING -> {
                            loadingDialog.show()
                        }
                        Status.UNAUTHORIZED -> {
                            Utilities.showInvalidApiKeyAlert(this@MutateProfileActivity)
                        }
                    }
                }
            }

            editProfileMutableLiveData.observe(this@MutateProfileActivity) {
                binding.apply {
                    btnSave.isEnabled = it.status != Status.LOADING
                    when (it.status) {
                        Status.SUCCESS -> {
                            loadingDialog.dismiss()
                            Toast.makeText(
                                this@MutateProfileActivity,
                                getString(R.string.sucess_edit_profile),
                                Toast.LENGTH_SHORT
                            ).show()
                            it.data?.customer?.customer_update?.customer?.let { customer ->
                                preferencesHelper.user?.let { user ->
                                    preferencesHelper.saveAccount(user.copy(
                                        id = customer.id,
                                        name = customer.customer_name,
                                        gender = customer.customer_gender,
                                        birthday = customer.customer_birthday.toString(),
                                        phone_number = customer.phone_number,
                                        email = customer.customer_email,
                                    ))
                                }
                            }
                            finish()
                        }
                        Status.ERROR -> {
                            loadingDialog.dismiss()
                            Utilities.showToast(
                                this@MutateProfileActivity,
                                binding.root,
                                it.message,
                                Utilities.ToastType.ERROR
                            )
                        }
                        Status.LOADING -> {
                            loadingDialog.show()
                        }
                        Status.UNAUTHORIZED -> {
                            Utilities.showInvalidApiKeyAlert(this@MutateProfileActivity)
                        }
                    }
                }
            }

            birthDateMutableLiveData.observe(this@MutateProfileActivity) {
                binding.tilBirthDate.editText?.setText(
                    Utilities.formatToDateString(
                        it,
                        Utilities.DateType.SIMPLE
                    )
                )
            }
        }
    }

    @Parcelize
    data class MutateProfileData(
        val email: String?,
        val password: String?,
        val name: String? = null,
        val birthDate: Date? = null,
        val gender: Int? = null,
        val phoneNumber: String? = null,
        val mutateType: MutateType = MutateType.CREATE,
    ) : Parcelable

    enum class MutateType {
        EDIT, CREATE
    }

    companion object {
        const val MUTATE_PROFILE_DATA = "MUTATE_PROFILE_DATA"
    }
}