package ir.treeco.aftabe.New.View.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.DLTaskListener;
import ir.treeco.aftabe.BackgroundDrawable;
import ir.treeco.aftabe.CoinManager;
import ir.treeco.aftabe.New.Adapter.NotificationAdapter;
import ir.treeco.aftabe.New.Object.HeadObject;
import ir.treeco.aftabe.New.Object.PackageObject;
import ir.treeco.aftabe.New.Util.Zip;
import ir.treeco.aftabe.New.View.Fragment.PackageFragmentNew;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.View.Fragment.StoreFragment;
import ir.treeco.aftabe.utils.ImageManager;
import ir.treeco.aftabe.utils.LengthManager;
import ir.treeco.aftabe.utils.LoadingManager;
import ir.treeco.aftabe.utils.TaskStartedListener;
import ir.treeco.aftabe.utils.Utils;

public class MainActivity extends FragmentActivity {
    public final static String FRAGMENT_TYPE = "fragment_type";
    private FragmentPagerItemAdapter fragmentPagerItemAdapter;
    private ViewPager viewPager;
    private HeadObject headObject;
    NotificationAdapter notificationAdapter;
    Context context;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_main);


        preferences = getSharedPreferences(Utils.SHARED_PREFRENCES_TAG, Context.MODE_PRIVATE);

        // Initialize Height Manager
        LengthManager.initialize(this);

        // setup header
        setUpHeader();
        // setup coins
        setUpCoinBox();
        // set main activity background
        setOriginalBackgroundColor();

        context = this;

        headObject = new HeadObject();
        parseJson();

        //region Setup fragments in ViewPager
        fragmentPagerItemAdapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("تازه‌ها", PackageFragmentNew.class, new Bundler().putInt(FRAGMENT_TYPE, 0).get())
                .add("دانلود شده‌ها", PackageFragmentNew.class, new Bundler().putInt(FRAGMENT_TYPE, 1).get())
                .add("محبوب‌ترین‌ها", PackageFragmentNew.class, new Bundler().putInt(FRAGMENT_TYPE, 2).get())
                .create()
        );

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(fragmentPagerItemAdapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
        //endregion

        //region Downloads
        DLManager.getInstance(this).dlStart("http://rsdn.ir/files/aftabe/head.json", this.getFilesDir().getPath(),
                new DLTaskListener() {
                    @Override
                    public void onFinish(File file) {
                        super.onFinish(file);
                        parseJson();
                        downloadTask();
                    }
                }
        );
        //endregion
    }

    public void parseJson() {
        try {
            String a = this.getFilesDir().getPath() + "/head.json";
            Log.e("path", a);
            InputStream inputStream = new FileInputStream(a);
            Reader reader = new InputStreamReader(inputStream, "UTF-8");
            Gson gson = new GsonBuilder().create();
            headObject = gson.fromJson(reader, HeadObject.class);

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void downloadTask() {
        for (int i = 0; i < headObject.getDownloadtask().size(); i++) {
            File file = new File(this.getFilesDir().getPath() + "/" + headObject.getDownloadtask().get(i).getName());
            if (!file.exists()) {
                DLManager.getInstance(this).dlStart(headObject.getDownloadtask().get(i).getUrl(), this.getFilesDir().getPath(),
                        new DLTaskListener() {

                            @Override
                            public void onFinish(File file) {
                                super.onFinish(file);
                                Log.e("don", file.getPath());
                            }
                        }
                );
            }
        }
    }

    public HeadObject getHeadObject() {
        return headObject;
    }

    public void setHeadObject(HeadObject headObject) {
        this.headObject = headObject;
    }

    public void downloadPackage(String url, String path, final int id, final String name){

        notificationAdapter = new NotificationAdapter(id, this, name);
        DLManager.getInstance(this).dlStart(url, path, new DLTaskListener() {
                    int n = 0;
                    @Override
                    public void onProgress(int progress) {
                        super.onProgress(progress);

                        n++;
                        if (n == 30) {
                            notificationAdapter.notifyDownload(progress, id, name);
                            n = 0;

                            Log.e("don", "progress" + progress);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        super.onError(error);
                        notificationAdapter.faildDownload(id, name);

                        Log.e("don", "error");
                    }

                    @Override
                    public void onFinish(File file) {
                        super.onFinish(file); //todo chack md5 & save in json file
                        notificationAdapter.finalDownload(id, name);
                        Log.e("don", "finish " + file.getPath());
                        Zip zip = new Zip();
                        zip.unpackZip(file.getPath(), id, context);
                        saveToDownloads(id);
                    }
                }
        );
    }

    public void saveToDownloads(int id) {
        PackageObject packageObject = null;
        try {
            String a = this.getFilesDir().getPath() + "/Downloaded/" + id + "_level_list.json";
            Log.e("path", a);
            InputStream inputStream = new FileInputStream(a);
            Reader reader = new InputStreamReader(inputStream, "UTF-8");
            Gson gson = new GsonBuilder().create();
             packageObject = gson.fromJson(reader, PackageObject.class);


        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HeadObject downlodedObject = null;


        try {


            String downloadedPAth = this.getFilesDir().getPath() + "/downloaded.json";
            Log.e("downloadedPAth", downloadedPAth);

            File file = new File(downloadedPAth);
            if(file.exists()) {
                InputStream downloadedPAthinputStream = new FileInputStream(downloadedPAth);
                Reader readerd = new InputStreamReader(downloadedPAthinputStream, "UTF-8");
                Gson gsond = new GsonBuilder().create();
                downlodedObject = gsond.fromJson(readerd, HeadObject.class);

                boolean deleted = file.delete();
            }

        } catch ( Exception e) {
            e.printStackTrace();
        }


        if (downlodedObject.getDownloaded()==null){
            ArrayList<PackageObject> a = new ArrayList<>();
            downlodedObject.setDownloaded(a);
        }

        downlodedObject.getDownloaded().add(packageObject);




            String aa = this.getFilesDir().getPath() + "/downloaded.json";
            Log.e("path", aa);


        Gson gson = new Gson();

        // convert java object to JSON format,
        // and returned as JSON formatted string
        String json = gson.toJson(downlodedObject);

        try {
            //write converted json data to a file named "file.json"
            FileWriter writer = new FileWriter(aa);
            writer.write(json);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }



//            InputStream inputStream = new FileInputStream(a);
//            Reader reader = new InputStreamReader(inputStream, "UTF-8");
//            Gson gson = new GsonBuilder().create();
//            headObject = gson.fromJson(reader, HeadObject.class);



    }

    //region SetBackGroundDrawable
    private void setOriginalBackgroundColor() {
        ImageView background = (ImageView) findViewById(R.id.background);
        background.setImageDrawable(new BackgroundDrawable(this, new int[]{
                Color.parseColor("#F3C81D"),
                Color.parseColor("#F3C01E"),
                Color.parseColor("#F49C14")
        }));
    }
    //endregion

    //region SetUpCoinBox
    private void setUpCoinBox() {
        ImageView coinBox = (ImageView) findViewById(R.id.coin_box);
        int coinBoxWidth = LengthManager.getScreenWidth() * 9 / 20;
        int coinBoxHeight = LengthManager.getHeightWithFixedWidth(R.drawable.coin_box, coinBoxWidth);
        coinBox.setImageBitmap(ImageManager.loadImageFromResource(MainActivity.this, R.drawable.coin_box, coinBoxWidth, coinBoxHeight));

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) coinBox.getLayoutParams();
        layoutParams.topMargin = LengthManager.getScreenWidth() / 15;
        layoutParams.leftMargin = LengthManager.getScreenWidth() / 50;

        LinearLayout digits = (LinearLayout) findViewById(R.id.digits);
        RelativeLayout.LayoutParams digitsLayoutParams = (RelativeLayout.LayoutParams) digits.getLayoutParams();
        digitsLayoutParams.topMargin = LengthManager.getScreenWidth() * 40 / 360;
        digitsLayoutParams.leftMargin = LengthManager.getScreenWidth() * 575 / 3600;
        digitsLayoutParams.width = LengthManager.getScreenWidth() / 5;

        coinBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StoreFragment.getIsUsed())
                    return;

                LoadingManager.startTask(new TaskStartedListener() {
                    @Override
                    public void taskStarted() {
                        StoreFragment fragment = StoreFragment.getInstance();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.fragment_container, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });
            }
        });


        CoinManager.setCoinsChangedListener(new CoinManager.CoinsChangedListener() {
            @Override
            public void changed(int newAmount) {
                LinearLayout digits = (LinearLayout) findViewById(R.id.digits);
                digits.removeAllViews();

                String number = "" + CoinManager.getCoinsCount(preferences);

                int[] digitResource = new int[]{
                        R.drawable.digit_0,
                        R.drawable.digit_1,
                        R.drawable.digit_2,
                        R.drawable.digit_3,
                        R.drawable.digit_4,
                        R.drawable.digit_5,
                        R.drawable.digit_6,
                        R.drawable.digit_7,
                        R.drawable.digit_8,
                        R.drawable.digit_9,
                };

                digits.addView(Utils.makeNewSpace(MainActivity.this));
                for (int i = 0; i < number.length(); i++) {
                    int d = number.charAt(i) - '0';
                    ImageView digit = new ImageView(MainActivity.this);
                    int digitHeight = LengthManager.getScreenWidth() / 21;
                    digit.setImageBitmap(ImageManager.loadImageFromResource(MainActivity.this, digitResource[d], LengthManager.getWidthWithFixedHeight(digitResource[d], digitHeight), digitHeight));
                    digits.addView(digit);
                }
                digits.addView(Utils.makeNewSpace(MainActivity.this));
            }
        }, preferences);

    }
    //endregion

    //region SetUpHeader
    private void setUpHeader() {
        RelativeLayout header = (RelativeLayout) findViewById(R.id.header);
        header.setLayoutParams(new RelativeLayout.LayoutParams(LengthManager.getScreenWidth(), LengthManager.getHeaderHeight()));

        ImageView logo = (ImageView) findViewById(R.id.logo);
        logo.setImageBitmap(ImageManager.loadImageFromResource(MainActivity.this, R.drawable.header, LengthManager.getScreenWidth(), LengthManager.getScreenWidth() / 4));

    }
    //endregion
}
