package shadowws.in.gored.other;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

import com.pixplicity.easyprefs.library.Prefs;

import org.acra.ACRA;
import org.acra.annotation.AcraMailSender;

@AcraMailSender(mailTo = "shadowwsvinothkumar@gmail.com")
public class GoRed extends Application {

    private static GoRed goRed;

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
        goRed = this;
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }

    public static void freeMemory(){
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    public static synchronized GoRed getInstance() {
        return goRed;
    }

    public void setListener(InternetListener.InternetReceiverListener listener) {
        InternetListener.listener = listener;
    }
}
