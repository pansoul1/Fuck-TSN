package com.pansoul.fucktsn.hook

import android.os.Handler
import android.os.Looper
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * 负责Hook体适能应用的版本更新模块
 */
object VersionUpdateHook {

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        // 由于应用加固，版本更新相关类可能延迟加载，使用Handler延迟执行Hook
        Handler(Looper.getMainLooper()).postDelayed({
            try {


                
                // 方法2: 直接Hook弹窗显示方法
                hookUpdateDialog(lpparam)
                
                XposedBridge.log("TSNHook: 版本更新Hook成功")
            } catch (e: Throwable) {
                XposedBridge.log("TSNHook: 版本更新Hook失败 - ${e.message}")
                
                // 如果第一次尝试失败，再次延迟尝试
                retryHookVersionUpdate(lpparam)
            }
        }, 1000) // 延迟1秒执行，与安全检测Hook保持一致
    }
    
    /**
     * 如果第一次Hook失败，再次尝试
     */
    private fun retryHookVersionUpdate(lpparam: XC_LoadPackage.LoadPackageParam) {
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                hookUpdateDialog(lpparam)
                XposedBridge.log("TSNHook: 第二次尝试版本更新Hook成功")
            } catch (e: Throwable) {
                XposedBridge.log("TSNHook: 第二次尝试版本更新Hook也失败 - ${e.message}")
            }
        }, 5000) // 再延迟5秒尝试
    }
    
    
    /**
     * 直接Hook弹窗显示方法，阻止弹窗显示
     */
    private fun hookUpdateDialog(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedHelpers.findAndHookMethod(
            "com.bxkj.student.common.versionupdate.a",
            lpparam.classLoader,
            "d",
            "java.lang.String",
            "java.lang.String", 
            "java.lang.String",
            XC_MethodReplacement.returnConstant(null)
        )
        
        XposedBridge.log("TSNHook: 已Hook更新弹窗方法")
    }
}