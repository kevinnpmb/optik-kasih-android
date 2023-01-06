package com.skripsi.optik_kasih.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.adapter.HomeAdapter
import com.skripsi.optik_kasih.adapter.HomeAdapterType
import com.skripsi.optik_kasih.adapter.HomeRow
import com.skripsi.optik_kasih.databinding.FragmentHomeBinding
import com.skripsi.optik_kasih.ui.common.BaseFragment
import com.skripsi.optik_kasih.ui.detail.DetailActivity
import com.skripsi.optik_kasih.utils.GridSpacesItemDecoration
import com.skripsi.optik_kasih.utils.Utilities
import com.skripsi.optik_kasih.utils.Utilities.dpToPx
import com.skripsi.optik_kasih.vo.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.srlHome.setColorSchemeColors(
            ContextCompat.getColor(
                requireContext(),
                R.color.primaryColor
            )
        )
        binding.rvHome.apply {
            adapter = HomeAdapter {
                startActivity(Intent(requireActivity(), DetailActivity::class.java).apply {
                    putExtra(DetailActivity.PRODUCT_ID, it.id)
                })
            }
            layoutManager = GridLayoutManager(context, 2).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (HomeAdapterType.values()[(adapter as HomeAdapter).getItemViewType(position)]) {
                            HomeAdapterType.List -> 1
                            HomeAdapterType.Header -> 2
                        }
                    }
                }
            }
            addItemDecoration(GridSpacesItemDecoration(2.dpToPx.toInt()))
        }
        initObserver()
        initListener()
        viewModel.getProducts()
    }

    private fun initListener() {
        binding.apply {
            srlHome.setOnRefreshListener {
                viewModel.getProducts()
            }
        }
    }

    private fun initObserver() {
        viewModel.apply {
            productsMutableLiveData.observe(
                viewLifecycleOwner
            ) {
                binding.apply {
                    loading.root.isVisible = it.status == Status.LOADING
                    error.root.isVisible = it.status == Status.ERROR
                    when (it.status) {
                        Status.SUCCESS -> {
                            srlHome.isRefreshing = false
                            val products = it.data?.product?.products?.nodes?.filter { it.product.isShown == 1 }.orEmpty()
                            rvHome.isVisible = products.isNotEmpty()
                            empty.root.isVisible = !rvHome.isVisible
                            if (products.isNotEmpty()) {
                                (rvHome.adapter as HomeAdapter).submitList(products.groupBy { it.product.product_type }
                                    .flatMap {
                                        listOf(HomeRow.Header(it.key), *(it.value.map { item ->
                                            (HomeRow.List(
                                                item.product
                                            ))
                                        }).toTypedArray())
                                    })
                            }
                        }
                        Status.ERROR -> {
                            srlHome.isRefreshing = false
                            Utilities.showToast(requireActivity(), binding.root, it.message, Utilities.ToastType.ERROR)
                        }
                        Status.LOADING -> {
                            rvHome.isVisible = false
                        }
                        Status.UNAUTHORIZED -> {
                            Utilities.showInvalidApiKeyAlert(requireActivity())
                        }
                    }
                }
            }
        }
    }
}