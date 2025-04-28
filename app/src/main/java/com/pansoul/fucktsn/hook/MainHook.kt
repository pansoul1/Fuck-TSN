package com.pansoul.fucktsn.hook

import android.widget.Toast
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import android.os.Handler
import android.os.Looper

class MainHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.bxkj.student") {
            XposedBridge.log("TSNHook: 检测到体适能应用")
            HookBXStudent.hook(lpparam)
        }
    }
}

object HookBXStudent {
    private var hasShownToast = false

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedBridge.log("VIPHook: 检测到体适能")

        // 显示 Toast - 使用Application类
        try {
            XposedHelpers.findAndHookMethod(
                "android.app.Application",
                lpparam.classLoader,
                "onCreate",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (!hasShownToast) {
                            val application = param.thisObject as android.app.Application
                            android.widget.Toast.makeText(
                                application,
                                "体适能 Hook成功 By Pansoul",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                            hasShownToast = true
                            XposedBridge.log("体适能 Hook成功 By Pansoul")
                            
                            // 在Application的onCreate方法中，延迟Hook加固部分
                            // 使用Handler延迟执行i0包下类的Hook
                            Handler(Looper.getMainLooper()).postDelayed({
                                hookSecurityChecks(lpparam)
                            }, 3000) // 延迟3秒执行
                        }
                    }
                }
            )
            XposedBridge.log("VIPHook: 体适能 Toast Hook成功")
        } catch (e: Throwable) {
            XposedBridge.log("VIPHook: 体适能 Toast Hook失败 - ${e.message}")
        }

        // Hook启动页广告
        try {
            XposedHelpers.findAndHookMethod(
                "android.app.Activity",
                lpparam.classLoader,
                "onCreate",
                "android.os.Bundle",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        try {
                            val activity = param.thisObject
                            val activityClass = activity.javaClass
                            val className = activityClass.name
                            
                            XposedBridge.log("TSNHook: 检测到Activity: $className")
                            
                            when {
                                // 处理广告启动页
                                className == "com.bxkj.student.splash.SplashActivity" -> {
                                    XposedBridge.log("TSNHook: 找到启动页广告: $className")
                                    try {
                                        // 直接调用 U() 方法
                                        XposedHelpers.callMethod(activity, "U")
                                        XposedBridge.log("TSNHook: 成功跳过启动页广告")
                                    } catch (e: Throwable) {
                                        XposedBridge.log("TSNHook: 跳转失败 - ${e.message}")
                                        try {
                                            // 尝试直接设置标志位并调用跳转
                                            XposedHelpers.setBooleanField(activity, "f21290l", true)
                                            XposedHelpers.callMethod(activity, "U")
                                        } catch (e: Throwable) {
                                            XposedBridge.log("TSNHook: 设置标志位失败 - ${e.message}")
                                        }
                                    }
                                }
                                
                                // 处理隐私协议页面
                                className == "com.bxkj.student.splash.OpenPrivacyAgreementActivity" -> {
                                    XposedBridge.log("TSNHook: 找到隐私协议页面: $className")
                                    try {
                                        // 尝试自动同意隐私协议
                                        val checkBox = XposedHelpers.findField(activityClass, "checkBox")
                                        val agreeBtn = XposedHelpers.findField(activityClass, "agreeBtn")
                                        
                                        checkBox.set(activity, true)
                                        XposedHelpers.callMethod(agreeBtn.get(activity), "performClick")
                                        XposedBridge.log("TSNHook: 已自动同意隐私协议")
                                    } catch (e: Throwable) {
                                        XposedBridge.log("TSNHook: 自动同意隐私协议失败 - ${e.message}")
                                    }
                                }
                            }
                        } catch (e: Throwable) {
                            XposedBridge.log("TSNHook: 处理启动页时出错 - ${e.message}")
                        }
                    }
                }
            )
            XposedBridge.log("TSNHook: 体适能 启动页 Hook成功")
        } catch (e: Throwable) {
            XposedBridge.log("TSNHook: 体适能 启动页 Hook失败 - ${e.message}")
        }

        // Hook开发者选项检查
        try {
            XposedHelpers.findAndHookMethod(
                "android.provider.Settings\$Secure",
                lpparam.classLoader,
                "getInt",
                "android.content.ContentResolver",
                "java.lang.String",
                "int",
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        // 如果是检查开发者选项的调用
                        if (param.args[1] == "development_settings_enabled") {
                            XposedBridge.log("TSNHook: 已拦截开发者选项检查")
                            param.result = 0  // 直接返回0，表示开发者选项未开启
                        }
                    }
                }
            )
            XposedBridge.log("TSNHook: 体适能 开发者选项检查 Hook成功")
        } catch (e: Throwable) {
            XposedBridge.log("TSNHook: 体适能 开发者选项检查 Hook失败 - ${e.message}")
        }

        // 1. 立即Hook Debug.isDebuggerConnected()方法
        try {
            XposedHelpers.findAndHookMethod(
                "android.os.Debug", 
                lpparam.classLoader,
                "isDebuggerConnected", 
                XC_MethodReplacement.returnConstant(false)
            )
            XposedBridge.log("TSNHook: Debug.isDebuggerConnected Hook成功")
        } catch (e: Throwable) {
            XposedBridge.log("TSNHook: Debug.isDebuggerConnected Hook失败 - ${e.message}")
        }
        
        // 7. 立即拦截System.exit调用
        try {
            XposedHelpers.findAndHookMethod(
                "java.lang.System", 
                lpparam.classLoader,
                "exit", 
                "int",
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        // 获取调用堆栈
                        val stackTrace = Thread.currentThread().stackTrace
                        // 检查是否来自安全检测类
                        for (element in stackTrace) {
                            if (element.className.startsWith("i0.") || 
                                element.className.startsWith("x.")) {
                                XposedBridge.log("TSNHook: 拦截到安全检测的System.exit调用: ${element.className}.${element.methodName}")
                                // 拦截调用
                                param.result = null
                                break
                            }
                        }
                    }
                }
            )
            XposedBridge.log("TSNHook: System.exit Hook成功")
        } catch (e: Throwable) {
            XposedBridge.log("TSNHook: System.exit Hook失败 - ${e.message}")
        }

        // 添加版本更新Hook
        VersionUpdateHook.hook(lpparam)
    }
    
    // 延迟Hook加固的i0包方法
    private fun hookSecurityChecks(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedBridge.log("TSNHook: 开始延迟Hook加固部分...")
        
        // 延迟Hook i0包下的安全检测类
        try {
            // 2. Hook i0.b类的方法 (AppSecretUtils)
            XposedHelpers.findAndHookMethod(
                "i0.b", 
                lpparam.classLoader,
                "b", 
                XC_MethodReplacement.returnConstant(null)
            )
            XposedBridge.log("TSNHook: i0.b.b() Hook成功")
            
            // 3. Hook i0.e类的c方法 (Hook检测)
            XposedHelpers.findAndHookMethod(
                "i0.e", 
                lpparam.classLoader,
                "c", 
                XC_MethodReplacement.returnConstant(false)
            )
            XposedBridge.log("TSNHook: i0.e.c() Hook成功")
            
            // 4. Hook i0.g类的a方法 (调试标志检测)
            XposedHelpers.findAndHookMethod(
                "i0.g", 
                lpparam.classLoader,
                "a", 
                XC_MethodReplacement.returnConstant(false)
            )
            XposedBridge.log("TSNHook: i0.g.a() Hook成功")
            
            // 5. Hook i0.f类的d方法 (Root设备检测)
            XposedHelpers.findAndHookMethod(
                "i0.f", 
                lpparam.classLoader,
                "d", 
                XC_MethodReplacement.returnConstant(false)
            )
            XposedBridge.log("TSNHook: i0.f.d() Hook成功")
            
            // 6. Hook x.b类的c方法 (应用完整性检测)
            XposedHelpers.findAndHookMethod(
                "x.b", 
                lpparam.classLoader,
                "c", 
                "android.content.Context",
                XC_MethodReplacement.returnConstant(true)
            )
            XposedBridge.log("TSNHook: x.b.c() Hook成功")
            
            XposedBridge.log("TSNHook: 体适能 延迟安全检测 Hook全部成功")
        } catch (e: Throwable) {
            XposedBridge.log("TSNHook: 体适能 延迟安全检测 Hook失败 - ${e.message}")
            
            // 如果第一次尝试失败，再次尝试延迟Hook
            Handler(Looper.getMainLooper()).postDelayed({
                XposedBridge.log("TSNHook: 再次尝试延迟Hook...")
                try {
                    // 尝试逐个Hook i0包下的类，以便确定具体哪个类出现问题
                    try {
                        XposedHelpers.findAndHookMethod(
                            "i0.b", 
                            lpparam.classLoader,
                            "b", 
                            XC_MethodReplacement.returnConstant(null)
                        )
                        XposedBridge.log("TSNHook: 第二次尝试 i0.b.b() Hook成功")
                    } catch (e1: Throwable) {
                        XposedBridge.log("TSNHook: 第二次尝试 i0.b.b() Hook失败 - ${e1.message}")
                    }
                    
                    // 尝试hook其他i0包下的类...
                    // ...
                    
                } catch (e2: Throwable) {
                    XposedBridge.log("TSNHook: 第二次尝试延迟Hook失败 - ${e2.message}")
                }
            }, 5000) // 再延迟5秒尝试
        }
    }
} 