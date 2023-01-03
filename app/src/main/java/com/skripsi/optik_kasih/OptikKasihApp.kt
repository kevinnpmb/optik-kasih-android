package com.skripsi.optik_kasih

//import com.pmberjaya.jovaresto.repository.UserRepository
import android.app.Application
import com.skripsi.optik_kasih.utils.PreferencesHelper
import com.skripsi.optik_kasih.vo.RequestHeaders
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class OptikKasihApp : Application() {
    @Inject
    lateinit var requestHeaders: RequestHeaders
    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        initializeApplication()
    }

    fun initializeApplication() {
        //load the current access token into all requests
        requestHeaders.accessToken = preferencesHelper.user?.accessToken ?: ""
    }
}
