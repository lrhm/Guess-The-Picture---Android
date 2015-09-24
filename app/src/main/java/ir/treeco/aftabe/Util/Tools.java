package ir.treeco.aftabe.Util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.DLTaskListener;
import ir.treeco.aftabe.Adapter.DBAdapter;
import ir.treeco.aftabe.Adapter.NotificationAdapter;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.HeadObject;
import ir.treeco.aftabe.Object.PackageObject;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.View.Custom.ToastMaker;

public class Tools {
    private Context context;
    private LengthManager lengthManager;
    private HeadObject headObject;
    private ImageManager imageManager;

    public Tools(Context context) {
        this.context = context;
        lengthManager = ((MainApplication) context.getApplicationContext()).getLengthManager();
        headObject = ((MainApplication) context.getApplicationContext()).getHeadObject();
        imageManager = ((MainApplication) context.getApplicationContext()).getImageManager();
    }

    public String decodeBase64(String string) {
        byte[] data = Base64.decode(string, Base64.DEFAULT);
        String solution = "";

        try {
            solution = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return solution;

    }

    public float convertPixelsToDp(float px) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);
    }

    public Bitmap updateHSV(Bitmap src, float settingHue, float settingSat, float settingVal) {
        int w = src.getWidth();
        int h = src.getHeight();
        int[] mapSrcColor = new int[w * h];
        int[] mapDestColor = new int[w * h];

        float[] pixelHSV = new float[3];

        src.getPixels(mapSrcColor, 0, w, 0, 0, w, h);

        int index = 0;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {

                // Convert from Color to HSV
                Color.colorToHSV(mapSrcColor[index], pixelHSV);
                int alpha = Color.alpha(mapSrcColor[index]);

                // Adjust HSV
                pixelHSV[0] = pixelHSV[0] + settingHue;
                if (pixelHSV[0] < 0.0f) {
                    pixelHSV[0] += 360;
                } else if (pixelHSV[0] > 360.0f) {
                    pixelHSV[0] -= 360.0f;
                }

                pixelHSV[1] = pixelHSV[1] + settingSat;
                if (pixelHSV[1] < 0.0f) {
                    pixelHSV[1] = 0.0f;
                } else if (pixelHSV[1] > 1.0f) {
                    pixelHSV[1] = 1.0f;
                }

                pixelHSV[2] = pixelHSV[2] + settingVal;
                if (pixelHSV[2] < 0.0f) {
                    pixelHSV[2] = 0.0f;
                } else if (pixelHSV[2] > 1.0f) {
                    pixelHSV[2] = 1.0f;
                }

                // Convert back from HSV to Color
                mapDestColor[index] = Color.HSVToColor(alpha, pixelHSV);

                index++;
            }
        }

        return Bitmap.createBitmap(mapDestColor, w, h, Bitmap.Config.ARGB_8888);

    }

    public void resizeView(View view, int width, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (width != layoutParams.width || height != layoutParams.height) {
            layoutParams.width = width;
            layoutParams.height = height;
        }
    }

    public void reverseLinearLayout(LinearLayout linearLayout) {
        View views[] = new View[linearLayout.getChildCount()];
        for (int i = 0; i < views.length; i++)
            views[i] = linearLayout.getChildAt(i);
        linearLayout.removeAllViews();
        for (int i = views.length - 1; i >= 0; i--)
            linearLayout.addView(views[i]);
    }

    public String numeralStringToPersianDigits(String s) {
        String persianDigits = "۰۱۲۳۴۵۶۷۸۹";
        char[] result = new char[s.length()];
        for (int i = 0; i < s.length(); i++)
            result[i] = Character.isDigit(s.charAt(i)) ? persianDigits.charAt(s.charAt(i) - '0') : s.charAt(i);
        return new String(result);
    }

    public void setViewBackground(final View view, Drawable dialogDrawable) {
        if (Build.VERSION.SDK_INT >= 16)
            view.setBackground(dialogDrawable);
        else {
            view.setBackgroundDrawable(dialogDrawable);
        }
    }

    public void downloadHead() {
        DLManager.getInstance(context)
                .dlStart("http://pfont.ir/files/aftabe/head.json", context.getFilesDir().getPath(),
                        new DLTaskListener() {
                            @Override
                            public void onFinish(File file) {
                                super.onFinish(file);

                                Prefs.putString(
                                        context.getResources()
                                                .getString(R.string.updated_time_shared_preference),
                                        new SimpleDateFormat("dd-MM-yyyy")
                                                .format(Calendar.getInstance().getTime()));

                                parseJson(context.getFilesDir().getPath() + "/head.json");
                                downloadTask();
                            }
                        }
                );
    }

    public void downloadTask() {
        for (int i = 0; i < headObject.getDownloadtask().length; i++) {
            File file = new File(context.getFilesDir().getPath() + "/" + headObject.getDownloadtask()[i].getName());

            if (!file.exists()) {
                DLManager.getInstance(context).dlStart(headObject.getDownloadtask()[i].getUrl(), context.getFilesDir().getPath(),
                        new DLTaskListener() {

                            @Override
                            public void onFinish(File file) {
                                super.onFinish(file);
                            }
                        }
                );
            }
        }
    }

    public void parseJson(String path) {
        try {
            InputStream inputStream = new FileInputStream(path);
            Reader reader = new InputStreamReader(inputStream, "UTF-8");
            Gson gson = new GsonBuilder().create();
            ((MainApplication)context.getApplicationContext()).setHeadObject(gson.fromJson(reader, HeadObject.class));

            headObject = ((MainApplication) context.getApplicationContext()).getHeadObject(); // TODO: 9/24/15 check for Reference
            // TODO Armin: Here we should save the number of ads in DB or SP

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void copyLocalpackages() {
        HeadObject localObject = new Gson().fromJson(new InputStreamReader(
                context.getResources().openRawResource(R.raw.local)), HeadObject.class);

        for (int i = 0; i < localObject.getLocal().length; i++) {
            int id = localObject.getLocal()[i].getId();
            String backImage = "p_" + id + "_back";
            String frontImage = "p_" + id + "_front";
            String zipFile = "p_" + id;

            writeRawFiles(backImage, "png", id);
            writeRawFiles(frontImage, "png", id);
            writeRawFiles(zipFile, "zip", id);
        }
    }

    public void writeRawFiles(String name, String type, int id) {
        FileOutputStream fileOutputStream;
        InputStream inputStream = context.getResources().openRawResource(
                context.getResources().getIdentifier("raw/" + name, type, context.getPackageName()));
        String path = context.getFilesDir().getPath() + File.separator + name + "." + type;

        try {
            fileOutputStream = new FileOutputStream(path);
            byte[] bytes = new byte[1024];
            int read;

            while ((read = inputStream.read(bytes)) > 0) {
                fileOutputStream.write(bytes, 0, read);
            }

            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (type.equals("zip")) {
            Zip zip = new Zip();
            zip.unpackZip(path, id, context);
            saveToDownloadsJson(id);
            makeFirstHSV(id);
        }
    }

    public void makeFirstHSV(int id) {
        float[] thumbnailHSV = {130, 0, 0};  //todo load for json
        Bitmap levelLocked = updateHSV(
                imageManager.loadImageFromResource(
                        R.drawable.level_locked,
                        lengthManager.getLevelFrameWidth(),
                        lengthManager.getLevelFrameHeight()),
                thumbnailHSV[0], thumbnailHSV[1], thumbnailHSV[2]);

        saveBitmap(levelLocked, id + "_levelLocked.png");

        Bitmap levelUnlocked = updateHSV(
                imageManager.loadImageFromResource(
                        R.drawable.level_unlocked,
                        lengthManager.getLevelFrameWidth(),
                        lengthManager.getLevelFrameHeight()),
                thumbnailHSV[0], thumbnailHSV[1], thumbnailHSV[2]);

        saveBitmap(levelUnlocked, id + "_levelUnlocked.png");


        float[] cheatButtonHSV = {130, 0, 0};//mLevel.getWrapperPackage().meta.getCheatButtonHSV();

        Bitmap cheatBitmap = updateHSV(
                imageManager.loadImageFromResource(
                        R.drawable.cheat_button,
                        lengthManager.getCheatButtonSize(),
                        lengthManager.getCheatButtonSize()),
                cheatButtonHSV[0], cheatButtonHSV[1], cheatButtonHSV[2]);

        saveBitmap(cheatBitmap, id + "_cheatBitmap.png");


        Bitmap backBitmap = updateHSV(
                imageManager.loadImageFromResource(
                        R.drawable.back_button,
                        lengthManager.getCheatButtonSize(),
                        lengthManager.getCheatButtonSize()),
                cheatButtonHSV[0], cheatButtonHSV[1], cheatButtonHSV[2]);

        saveBitmap(backBitmap, id + "_backBitmap.png");

    }

    public void saveBitmap (Bitmap bitmap, String name) {

        File file = new File (context.getFilesDir().getPath() + "/Downloaded/", name);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveDataAndBackUpData(Context context) {
        String aa = context.getFilesDir().getPath() + "/downloaded.json";
        File parentDir = new File(Environment.getExternalStorageDirectory() + "/Android");
        parentDir.mkdir();
        String backUpDataPath = parentDir.getPath() + "/file.json";
        Gson backupGson = new Gson();
        String backUpJson = null;// = backupGson.toJson(downloadedObject);

        File file = new File(aa);
        file.delete();

        File backUpFile = new File(parentDir, backUpDataPath);
        backUpFile.delete();

        try {
            //write converted json data to a file named "file.json"
            FileWriter writer = new FileWriter(aa);
            writer.write(backUpJson);
            writer.close();

            FileWriter writerBackUp = new FileWriter(backUpDataPath);
            writerBackUp.write(backUpJson);
            writer.close();
            writerBackUp.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveToDownloadsJson(int id) {
        PackageObject packageObject = null;

        try {
            String a = context.getFilesDir().getPath() + "/Downloaded/" + id + "_level_list.json";
            InputStream inputStream = new FileInputStream(a);
            Reader reader = new InputStreamReader(inputStream, "UTF-8");
            Gson gson = new GsonBuilder().create();
            packageObject = gson.fromJson(reader, PackageObject.class);

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        DBAdapter db = DBAdapter.getInstance(context);
        db.insertPackage(packageObject);
    }

    public void downloadPackage(String url, String path, final int id, final String name) {
        final NotificationAdapter notificationAdapter = new NotificationAdapter(id, context, name);
        DLManager.getInstance(context).dlStart(url, path, new DLTaskListener() {
                    int n = 0;

                    @Override
                    public void onProgress(int progress) {
                        super.onProgress(progress);
                        if (progress % 10 == 0) {
                            notificationAdapter.notifyDownload(progress, id, name);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        super.onError(error);
                        notificationAdapter.faildDownload(id, name);
                        ToastMaker.show(context, "دانلود بسته با مشکل روبرو شد :(", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onFinish(File file) {
                        super.onFinish(file); //todo chack md5 & save in json file
                        notificationAdapter.finalDownload(id, name);
                        Zip zip = new Zip();
                        zip.unpackZip(file.getPath(), id, context);
                        saveToDownloadsJson(id);
                        makeFirstHSV(id);
                    }
                }
        );
    }


}
