package com.thangiee.lolhangouts;

import android.content.ContextWrapper;
import com.activeandroid.app.Application;
import com.parse.Parse;
import com.pixplicity.easyprefs.library.Prefs;
import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import java.util.HashSet;
import java.util.Set;

@ReportsCrashes(
        formUri = "https://thangiee.cloudant.com/acra-lolhangouts/_design/acra-storage/_update/report",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.POST,
        formUriBasicAuthLogin = "login",
        formUriBasicAuthPassword = "pass",
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
    public static String PLAY_SERVICE_KEY = "";
    public boolean isFriendListOpen = false;
    public boolean isChatOpen = false;
    public Set<String> FriendsToNotifyOnAvailable = new HashSet<>();

    @Override
    public void onCreate() {
        super.onCreate();

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
//        ACRA.init(this);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "LOIXdGNLWVUnWqF4vuKjTdolMR23FpYTiNgArrib", "VDdyddCDyZW0yLS5jQoUf4lnxWPMCCsSCRnCvbJc");
    }

}
