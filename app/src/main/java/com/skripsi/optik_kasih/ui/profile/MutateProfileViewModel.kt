package com.skripsi.optik_kasih.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skripsi.optik_kasih.RegisterMutation
import com.skripsi.optik_kasih.repository.UserRepository
import com.skripsi.optik_kasih.vo.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MutateProfileViewModel @Inject constructor(
    var userRepository: UserRepository,
) : ViewModel() {
    val registerMutableLiveData = MutableLiveData<Resource<RegisterMutation.Data>>()
    fun login(
        name: String,
        birthDate: Date,
        gender: Int,
        phoneNumber: String,
        email: String,
        password: String,
    ) {
        viewModelScope.launch {
            userRepository.registerUser(
                name,
                birthDate,
                gender,
                phoneNumber,
                email,
                password,
                registerMutableLiveData
            )
        }
    }
}