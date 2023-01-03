package com.skripsi.optik_kasih.ui.common

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skripsi.optik_kasih.ui.common.BaseActivity

open class BaseFragment : Fragment() {
    lateinit var baseActivity: BaseActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseActivity = requireActivity() as BaseActivity
    }
}