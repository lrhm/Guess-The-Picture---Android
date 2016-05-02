package ir.treeco.aftabe.View.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;

import ir.treeco.aftabe.Adapter.DBAdapter;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Service.NotifObjects.NotifHolder;
import ir.treeco.aftabe.Service.ServiceConstants;
import ir.treeco.aftabe.Util.Tools;

public class LoadingActivity extends Activity implements Runnable {

    long startTime;
    private boolean mIsThereFriendReq;
    private boolean mIsThereMatchReq;
    private NotifHolder mNotifHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading);

        startTime = System.currentTimeMillis();


        new Handler().postDelayed(this, 300);

    }

    @Override
    protected void onDestroy() {
        Log.d(this.getClass().getName(), "on destory");
        super.onDestroy();
    }

    @Override
    public void run() {

        initUtils();
        long diff = System.currentTimeMillis() - startTime;

        if (diff < 1000)
            try {
                Thread.sleep(1000 - diff);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        Intent intent = new Intent(this, MainActivity.class);
        if (getIntent() != null && getIntent().getExtras() != null)
            intent.putExtras(getIntent().getExtras());
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
