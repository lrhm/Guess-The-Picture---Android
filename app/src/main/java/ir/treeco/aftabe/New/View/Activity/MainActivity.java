package ir.treeco.aftabe.New.View.Activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.DLTaskListener;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.New.Object.HeadObject;
import ir.treeco.aftabe.New.Object.PackageObject;
import ir.treeco.aftabe.New.Util.ImageManager;
import ir.treeco.aftabe.New.Util.Zip;
import ir.treeco.aftabe.New.View.BackgroundDrawable;
import ir.treeco.aftabe.New.View.Fragment.MainFragment;
import ir.treeco.aftabe.R;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private HeadObject headObject;
    //NotificationAdapter notificationAdapter;
    Context context;
//    SharedPreferences preferences;
    // public static HeadObject downlodedObject; //todo in bayad static class she ehtemalan


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_main);


        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if (fragmentManager.getBackStackEntryCount() != 0) throw new IllegalStateException();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MainFragment mainFragment = new MainFragment();
        fragmentTransaction.replace(R.id.fragment_container, mainFragment);
        fragmentTransaction.commit();



        context = this;
        loadDownloadedObject();
        headObject = new HeadObject();
        parseJson();

        setUpCoinBox();
        setUpHeader();
        setOriginalBackgroundColor();


        //region Downloads
        DLManager.getInstance(this).dlStart("http://rsdn.ir/files/aftabe/head.json", this.getFilesDir().getPath(), //todo in hamishe nabayad ejra she
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

    public void downloadPackage(String url, String path, final int id, final String name) {

        //notificationAdapter = new NotificationAdapter(id, this, name);
        DLManager.getInstance(this).dlStart(url, path, new DLTaskListener() {
                    int n = 0;

                    @Override
                    public void onProgress(int progress) {
                        super.onProgress(progress);

                        n++;
                        if (n == 30) {
                            //notificationAdapter.notifyDownload(progress, id, name);
                            n = 0;

                            Log.e("don", "progress" + progress);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        super.onError(error);
                        //notificationAdapter.faildDownload(id, name);

                        Log.e("don", "error");
                    }

                    @Override
                    public void onFinish(File file) {
                        super.onFinish(file); //todo chack md5 & save in json file
                        //notificationAdapter.finalDownload(id, name);
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

        if (MainApplication.downloadedObject.getDownloaded() == null) {
            ArrayList<PackageObject> a = new ArrayList<>();
            MainApplication.downloadedObject.setDownloaded(a);
        }

        MainApplication.downloadedObject.getDownloaded().add(packageObject);
        MainApplication.saveDataAndBackUpData(this);

    }

    public void loadDownloadedObject() {
        try {
            String downloadedPAth = this.getFilesDir().getPath() + "/downloaded.json";
            Log.e("downloadedPAth", downloadedPAth);

            File file = new File(downloadedPAth);
            if (file.exists()) {
                InputStream downloadedPAthinputStream = new FileInputStream(downloadedPAth);
                Reader readerd = new InputStreamReader(downloadedPAthinputStream, "UTF-8");
                Gson gsond = new GsonBuilder().create();
                MainApplication.downloadedObject = gsond.fromJson(readerd, HeadObject.class);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public void saveData(){
        String aa = this.getFilesDir().getPath() + "/downloaded.json";

        Gson gson = new Gson();
        // convert java object to JSON format,
        // and returned as JSON formatted string
        String json = gson.toJson(downlodedObject);

        File file = new File(aa);
        file.delete();

        try {
            String backUpData = "/data/Android System/file.json";
            //write converted json data to a file named "file.json"
            FileWriter writer = new FileWriter(backUpData);
            writer.write(json);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/



    //region SetUpCoinBox
    private void setUpCoinBox() {
        ImageView coinBox = (ImageView) findViewById(R.id.coin_box);

        int coinBoxWidth = MainApplication.lengthManager.getScreenWidth() * 9 / 20;
        int coinBoxHeight = MainApplication.lengthManager.getHeightWithFixedWidth(R.drawable.coin_box, coinBoxWidth);
        coinBox.setImageBitmap(ImageManager.loadImageFromResource(this, R.drawable.coin_box, coinBoxWidth, coinBoxHeight));

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) coinBox.getLayoutParams();
        layoutParams.topMargin = MainApplication.lengthManager.getScreenWidth() / 15;
        layoutParams.leftMargin = MainApplication.lengthManager.getScreenWidth() / 50;

        TextView digits = (TextView) findViewById(R.id.digits);

        RelativeLayout.LayoutParams digitsLayoutParams = (RelativeLayout.LayoutParams) digits.getLayoutParams();
        digitsLayoutParams.topMargin = MainApplication.lengthManager.getScreenWidth() * 34 / 400;
        digitsLayoutParams.leftMargin = MainApplication.lengthManager.getScreenWidth() * 577 / 3600;
        digitsLayoutParams.width = MainApplication.lengthManager.getScreenWidth() / 5;

        digits.setTypeface(Typeface.createFromAsset(getAssets(), "yekan.ttf"));
        String number = "۸۸۸۸۸";
        digits.setText(number);

        coinBox.setOnClickListener(this);
    }

    //region SetUpHeader
    private void setUpHeader() {
        RelativeLayout header = (RelativeLayout) findViewById(R.id.header);
        header.setLayoutParams(new LinearLayout.LayoutParams(MainApplication.lengthManager.getScreenWidth(), MainApplication.lengthManager.getHeaderHeight()));

        ImageView logo = (ImageView) findViewById(R.id.logo);
        logo.setImageBitmap(ImageManager.loadImageFromResource(this, R.drawable.header, MainApplication.lengthManager.getScreenWidth(), MainApplication.lengthManager.getScreenWidth() / 4));

    }


    private void setOriginalBackgroundColor() {
        ImageView background = (ImageView) findViewById(R.id.background);
        background.setImageDrawable(new BackgroundDrawable(this, new int[]{
                Color.parseColor("#29CDB8"),
                Color.parseColor("#1FB8AA"),
                Color.parseColor("#0A8A8C")
        }));
    }

    @Override
    public void onClick(View v) {

    }
}
