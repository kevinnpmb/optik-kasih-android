package com.skripsi.optik_kasih.ui.login

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import com.skripsi.optik_kasih.LoginMutation
import com.skripsi.optik_kasih.repository.UserRepository
import com.skripsi.optik_kasih.vo.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    var userRepository: UserRepository,
): ViewModel() {
    val loginMutableLiveData = MutableLiveData<Resource<LoginMutation.Data>>()
    fun login(email: String, password: String) {
        viewModelScope.launch {
            userRepository.loginUser(email, password, loginMutableLiveData)
        }
    }
}