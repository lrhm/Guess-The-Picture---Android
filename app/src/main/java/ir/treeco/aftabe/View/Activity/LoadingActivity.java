package ir.treeco.aftabe.View.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.pixplicity.easyprefs.library.Prefs;

import ir.treeco.aftabe.Adapter.DBAdapter;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.Tools;

public class LoadingActivity extends Activity implements Runnable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading);

        long startTime = System.currentTimeMillis();

        initUtils();

        long endTime = System.currentTimeMillis();

        if (startTime - endTime > 500) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        new Handler().postDelayed(this, 400);

    }


    @Override
    public void run() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void initUtils() {


        Tools tools = new Tools(this);

        if (Prefs.getBoolean("firstAppRun", true)) {
            Tools.checkKey();
        }

        tools.checkDB();

        DBAdapter db = DBAdapter.getInstance(getApplication());

        tools.parseJson(getApplicationContext().getFilesDir().getPath() + "/head.json");

        if (Prefs.getBoolean("firstAppRun", true)) {

            db.insertCoins(399);
            tools.copyLocalpackages();
            Prefs.putBoolean("firstAppRun", false);
        }

        tools.downloadHead();

    }

}
