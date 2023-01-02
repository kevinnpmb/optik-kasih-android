package com.skripsi.optik_kasih.ui.login

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import com.skripsi.optik_kasih.LoginQuery
//import com.skripsi.optik_kasih.LoginMutation
import com.skripsi.optik_kasih.repository.UserRepository
import com.skripsi.optik_kasih.type.Customer
import com.skripsi.optik_kasih.vo.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    var userRepository: UserRepository,
): ViewModel() {
    val loginMutableLiveData = MutableLiveData<Resource<LoginQuery.Data>>()
    fun login(email: String, password: String) {
        viewModelScope.launch {
            userRepository.loginUser(email, password, loginMutableLiveData)
        }
    }

    fun validate(
        type: TextFieldType, text: String, textInputLayout: TextInputLayout,
        errorText: String
    ): Boolean {
        when (type) {
            TextFieldType.EMAIL -> return if (text.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                textInputLayout.error = null
                true
            } else {
                textInputLayout.error = errorText
                false
            }
            TextFieldType.OTHER -> return if (text.isNotEmpty()) {
                textInputLayout.error = null
                true
            } else {
                textInputLayout.error = errorText
                false
            }
        }
    }

    enum class TextFieldType {
        EMAIL,
        OTHER
    }
}