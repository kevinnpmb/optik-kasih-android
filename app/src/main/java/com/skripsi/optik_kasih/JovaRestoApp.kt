package com.skripsi.optik_kasih

//import com.pmberjaya.jovaresto.repository.UserRepository
import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class OptikKasihApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
//        initializeApplication()
    }

//    fun initializeApplication() {
//        //load the current user into the system
//        val user =
//            userRepository.getLoggedInUser().subscribeOn(SchedulerProvider.instance.computation())
//                .blockingGet()
//        loggedUser.loggedInUser = user
//
//        //load the current access token into all requests
//        requestHeaders.accesstoken.accessToken = user.access_token ?: ""
//    }
}
