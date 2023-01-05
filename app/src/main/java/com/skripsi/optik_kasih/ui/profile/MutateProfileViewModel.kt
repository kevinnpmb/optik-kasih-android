package com.skripsi.optik_kasih.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skripsi.optik_kasih.EditProfileMutation
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
    val birthDateMutableLiveData = MutableLiveData<Date>()
    val birthDate: Date? get() = birthDateMutableLiveData.value
    val mutateProfileData = MutableLiveData<MutateProfileActivity.MutateProfileData>()
    val mutateType: MutateProfileActivity.MutateType? get() = mutateProfileData.value?.mutateType
    val registerMutableLiveData = MutableLiveData<Resource<RegisterMutation.Data>>()
    fun register(
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

    val editProfileMutableLiveData = MutableLiveData<Resource<EditProfileMutation.Data>>()
    fun editProfile(
        id: String,
        name: String,
        birthDate: Date,
        gender: Int,
        phoneNumber: String,
    ) {
        viewModelScope.launch {
            userRepository.editUser(
                id,
                name,
                birthDate,
                gender,
                phoneNumber,
                editProfileMutableLiveData
            )
        }
    }
}