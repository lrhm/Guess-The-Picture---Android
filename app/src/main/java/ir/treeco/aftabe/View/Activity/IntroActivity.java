package ir.treeco.aftabe.View.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import ir.treeco.aftabe.R;

/**
 * Created by al on 5/18/16.
 */
public class IntroActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);

        ViewPager pager = (ViewPager) findViewById(R.id.intro_activity_pager);

        PagerAdapter adapter = new

    }



}
