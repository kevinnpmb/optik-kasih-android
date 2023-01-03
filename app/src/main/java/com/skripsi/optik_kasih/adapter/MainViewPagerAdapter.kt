package com.skripsi.optik_kasih.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.skripsi.optik_kasih.ui.history.HistoryFragment
import com.skripsi.optik_kasih.ui.home.HomeFragment
import com.skripsi.optik_kasih.ui.profile.ProfileFragment

class MainViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return MainBottomMenu.values().size
    }
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            MainBottomMenu.HOME.ordinal -> HomeFragment()
            MainBottomMenu.HISTORY.ordinal -> HistoryFragment()
            MainBottomMenu.PROFILE.ordinal -> ProfileFragment()
            else -> Fragment()
        }
    }

    enum class MainBottomMenu {
        HOME, HISTORY, PROFILE
    }
}