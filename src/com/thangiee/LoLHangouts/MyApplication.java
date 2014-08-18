package com.thangiee.LoLHangouts;

import com.activeandroid.app.Application;
import com.pixplicity.easyprefs.library.Prefs;
import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

@ReportsCrashes(
        formUri = "https://thangiee.cloudant.com/acra-lolhangouts/_design/acra-storage/_update/report",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.POST,
        formUriBasicAuthLogin = "login",
        formUriBasicAuthPassword = "pass",
        formKey = "", // This is required for backward compatibility but not used
        customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PACKAGE_NAME,
                ReportField.REPORT_ID,
                ReportField.BUILD,
                ReportField.STACK_TRACE
        },
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Prefs.initPrefs(getApplicationContext());
//        ACRA.init(this);
    }
}
