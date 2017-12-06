package com.ma.aigou

import android.app.Application
import com.ma.aigou.view.MyCrashHandler
import com.ma.lib.utils.ImageLoad

class MyApplication : Application() {
    //定义静态变量
    companion object {
        //定义set方法为private
        var myApplication: MyApplication? = null
            private set

        fun staticFun(){

        }
    }

    override fun onCreate() {
        super.onCreate()
        myApplication = this
        MyCrashHandler.init(this)
        ImageLoad.initImageLoader(this)
    }
}