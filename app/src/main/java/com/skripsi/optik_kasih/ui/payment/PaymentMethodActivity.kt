package com.skripsi.optik_kasih.ui.payment

import android.app.Activity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.databinding.ActivityPaymentMethodBinding
import com.skripsi.optik_kasih.databinding.FragmentHomeBinding
import com.skripsi.optik_kasih.ui.common.BaseActivity
import com.skripsi.optik_kasih.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentMethodActivity : BaseActivity() {
    private lateinit var binding: ActivityPaymentMethodBinding
    private val viewModel:  by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}