package com.skripsi.optik_kasih.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.databinding.FragmentProfileBinding
import com.skripsi.optik_kasih.ui.main.MainActivity
import com.skripsi.optik_kasih.utils.PreferencesHelper
import com.skripsi.optik_kasih.utils.Utilities
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesHelper.user?.let {
            binding.apply {
                profileUserName.text = it.name
                profileEmail.text = it.email
            }
        }
        initListener()
    }

    private fun initListener() {
        binding.apply {
            btnLogout.setOnClickListener {
                preferencesHelper.signOut()
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            }

            editProfile.setOnClickListener {
                startActivity(Intent(requireContext(), MutateProfileActivity::class.java).apply {
                    preferencesHelper.user?.apply {
                        putExtra(MutateProfileActivity.MUTATE_PROFILE_DATA, MutateProfileActivity.MutateProfileData(
                            email,
                            null,
                            name,
                            Utilities.formatToDateISO8601(birthday),
                            gender,
                            phone_number,
                            MutateProfileActivity.MutateType.EDIT
                        ))
                    }
                })
            }
        }
    }
}