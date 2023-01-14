package com.skripsi.optik_kasih.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.adapter.HistoryAdapter
import com.skripsi.optik_kasih.databinding.FragmentHistoryBinding
import com.skripsi.optik_kasih.ui.common.BaseFragment
import com.skripsi.optik_kasih.ui.home.HomeViewModel
import com.skripsi.optik_kasih.utils.Utilities
import com.skripsi.optik_kasih.vo.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : BaseFragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val viewModel: HistoryViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utilities.initToolbar(
            requireActivity() as AppCompatActivity,
            binding.toolbar.toolbar,
            getString(R.string.history),
            hideBack = true,
            hideCart = true
        )
        binding.srlHistory.setColorSchemeColors(
            ContextCompat.getColor(
                requireContext(),
                R.color.primaryColor
            )
        )
        binding.rvHistory.apply {
            adapter = HistoryAdapter {
                startActivity(Intent(requireContext(), HistoryDetailActivity::class.java).apply {
                    putExtra(HistoryDetailActivity.HISTORY_DETAIL_ID, it.id)
                })
            }
        }
        initObserver()
        initListener()
        viewModel.getMyOrders()
    }

    private fun initListener() {
        binding.apply {
            srlHistory.setOnRefreshListener {
                viewModel.getMyOrders()
            }
        }
    }

    private fun initObserver() {
        viewModel.apply {
            ordersMutableLiveData.observe(
                viewLifecycleOwner
            ) {
                binding.apply {
                    loading.root.isVisible = it.status == Status.LOADING
                    error.root.isVisible = it.status == Status.ERROR
                    when (it.status) {
                        Status.SUCCESS -> {
                            srlHistory.isRefreshing = false
                            val orders =
                                it.data?.order?.orders_me?.mapNotNull { it?.order }.orEmpty()
                            rvHistory.isVisible = orders.isNotEmpty()
                            empty.root.isVisible = !rvHistory.isVisible
                            (rvHistory.adapter as HistoryAdapter).submitList(
                                orders
                            )
                        }
                        Status.ERROR -> {
                            srlHistory.isRefreshing = false
                            Utilities.showToast(
                                requireActivity(),
                                binding.root,
                                it.message,
                                Utilities.ToastType.ERROR
                            )
                        }
                        Status.LOADING -> {
                            rvHistory.isVisible = false
                        }
                        Status.UNAUTHORIZED -> {
                            Utilities.showInvalidApiKeyAlert(requireActivity())
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMyOrders()
    }
}