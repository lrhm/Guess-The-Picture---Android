package ir.treeco.aftabe.View.Activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import ir.treeco.aftabe.R;
import ir.treeco.aftabe.View.Custom.UserLevelMarkView;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.test_activity_main_layout);
        UserLevelMarkView userLevelMark = new UserLevelMarkView(this ,3 ,3);
        relativeLayout.addView(userLevelMark);

    }
}
