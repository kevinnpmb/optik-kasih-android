package com.skripsi.optik_kasih.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.adapter.MainViewPagerAdapter
import com.skripsi.optik_kasih.databinding.ActivityMainBinding
import com.skripsi.optik_kasih.ui.common.BaseActivity
import com.skripsi.optik_kasih.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.vpMain.apply {
            adapter = MainViewPagerAdapter(this@MainActivity)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when (position) {
                        MainViewPagerAdapter.MainBottomMenu.HOME.ordinal -> {
                            binding.bottomNavigationView.menu.findItem(R.id.home).isChecked = true
                        }
                        MainViewPagerAdapter.MainBottomMenu.HISTORY.ordinal -> {
                            binding.bottomNavigationView.menu.findItem(R.id.history).isChecked = true
                        }
                        MainViewPagerAdapter.MainBottomMenu.PROFILE.ordinal -> {
                            binding.bottomNavigationView.menu.findItem(R.id.profile).isChecked = true
                        }
                    }
                }
            })
        }
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    binding.vpMain.currentItem = 0
                    true
                }
                R.id.history -> {
                    binding.vpMain.currentItem = 1
                    invalidateOptionsMenu()
                    true
                }
                R.id.profile -> {
                    if (preferencesHelper.isLogin) {
                        binding.vpMain.currentItem = 2
                        true
                    } else {
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        false
                    }
                }
                else -> false
            }
        }
    }

    override fun onStop() {
        super.onStop()
        binding.vpMain.unregisterOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {})
    }
}