package com.ma.aigou.view

import android.content.Context
import android.os.Build
import android.util.Log
import com.ma.aigou.BuildConfig
import java.io.PrintWriter
import java.io.StringWriter


// 单例（懒实例化，没吊用就不会创建）
object MyCrashHandler : Thread.UncaughtExceptionHandler {
    private var context: Context ?= null

    fun init(context: Context){
        this.context = context
        // 设置该CrashHandler为程序默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(arg0: Thread?, arg1: Throwable?) {
        // 1.获取当前程序的版本号. 版本的id
        val versioninfo = getVersionInfo()
        // 2.获取手机的硬件信息.
        val mobileInfo = getMobileInfo()
        // 3.把错误的堆栈信息 获取出来
        val errorinfo = getErrorInfo(arg1)

        Log.e("error", errorinfo)
        if (!BuildConfig.DEBUG) {
            // 干掉当前的程序
            android.os.Process.killProcess(android.os.Process.myPid())
        }
    }

    /**
     * 获取错误的信息

     * @param arg1
     * *
     * @return
     */
    private fun getErrorInfo(arg1: Throwable?): String {
        val writer = StringWriter()
        val pw = PrintWriter(writer)
        arg1?.printStackTrace(pw)
        pw.close()
        val error = writer.toString()
        return error
    }

    /**
     * 获取手机的硬件信息

     * @return
     */
    private fun getMobileInfo(): String {
        val sb = StringBuffer()
        // 通过反射获取系统的硬件信息
        try {
            val fields = Build::class.java.declaredFields
            for (field in fields) {
                // 暴力反射 ,获取私有的信息
                field.isAccessible = true
                val name = field.name
                val value = field.get(null).toString()
                sb.append(name + "=" + value)
                sb.append("\n")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return sb.toString()
    }

    /**
     * 获取手机的版本信息

     * @return
     */
    private fun getVersionInfo(): String {
        try {
            val pm = this.context?.packageManager
            val info = pm?.getPackageInfo(this.context?.packageName, 0)
            return info?.versionName ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            return "版本号未知"
        }

    }

}