package com.skripsi.optik_kasih.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skripsi.optik_kasih.ChangePasswordMutation
import com.skripsi.optik_kasih.EditProfileMutation
import com.skripsi.optik_kasih.RegisterMutation
import com.skripsi.optik_kasih.repository.UserRepository
import com.skripsi.optik_kasih.vo.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    var userRepository: UserRepository,
) : ViewModel() {
    val changePasswordMutableLiveData = MutableLiveData<Resource<ChangePasswordMutation.Data>>()
    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            userRepository.changePassword(oldPassword, newPassword, changePasswordMutableLiveData)
        }
    }
}