package com.blanke.mdwechat

import android.content.Context
import com.blanke.mdwechat.config.WxObjects
import com.blanke.mdwechat.util.LogUtil.log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.ref.WeakReference

class WechatHook : IXposedHookLoadPackage {
    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != Common.WECHAT_PACKAGENAME) {
            return
        }
        WeChatHelper.initPrefs()
        val context = XposedHelpers.callMethod(
                XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null),
                        "currentActivityThread"), "getSystemContext") as Context
        val wechatPackageInfo = context.packageManager.getPackageInfo(Common.WECHAT_PACKAGENAME, 0)
        WxObjects.Application = WeakReference(context)
        val versionName = wechatPackageInfo.versionName
        log("wechat version=" + versionName
                + ",processName=" + lpparam.processName
                + ",MDWechat version=" + BuildConfig.VERSION_NAME)
        try {
            WeChatHelper.init(versionName, lpparam)
        } catch (e: Throwable) {
            log(e)
        }
    }
}
