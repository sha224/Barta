package app.barta.barta;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

public class BartaApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
    }
}
