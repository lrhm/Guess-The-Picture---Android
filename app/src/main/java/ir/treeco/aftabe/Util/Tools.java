package ir.treeco.aftabe.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.DLTaskListener;
import ir.treeco.aftabe.Adapter.CoinAdapter;
import ir.treeco.aftabe.Adapter.DBAdapter;
import ir.treeco.aftabe.Adapter.NotificationAdapter;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.HeadObject;
import ir.treeco.aftabe.Object.PackageObject;
import ir.treeco.aftabe.Object.SaveHolder;
import ir.treeco.aftabe.Object.TokenHolder;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.View.Custom.ToastMaker;

public class Tools {
    private Context context;
    private LengthManager lengthManager;
    private HeadObject headObject;
    private ImageManager imageManager;
    public final static String ENCRYPT_KEY = "shared_prefs_last_long";
    public final static String USER_SAVED_DATA = "shared_prefs_user";
    public final static String SHARED_PREFS_TOKEN = "shared_prefs_tk";
    private final static String TAG = "Tools";


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
//
//    public Bitmap updateHSV(Bitmap src, float settingHue, float settingSat, float settingVal) {
//        int w = src.getWidth();
//        int h = src.getHeight();
//        int[] mapSrcColor = new int[w * h];
//        int[] mapDestColor = new int[w * h];
//
//        float[] pixelHSV = new float[3];
//
//        src.getPixels(mapSrcColor, 0, w, 0, 0, w, h);
//
//        int index = 0;
//        for (int y = 0; y < h; ++y) {
//            for (int x = 0; x < w; ++x) {
//
//                // Convert from Color to HSV
//                Color.colorToHSV(mapSrcColor[index], pixelHSV);
//                int alpha = Color.alpha(mapSrcColor[index]);
//
//                // Adjust HSV
//                pixelHSV[0] = pixelHSV[0] + settingHue;
//                if (pixelHSV[0] < 0.0f) {
//                    pixelHSV[0] += 360;
//                } else if (pixelHSV[0] > 360.0f) {
//                    pixelHSV[0] -= 360.0f;
//                }
//
//                pixelHSV[1] = pixelHSV[1] + settingSat;
//                if (pixelHSV[1] < 0.0f) {
//                    pixelHSV[1] = 0.0f;
//                } else if (pixelHSV[1] > 1.0f) {
//                    pixelHSV[1] = 1.0f;
//                }
//
//                pixelHSV[2] = pixelHSV[2] + settingVal;
//                if (pixelHSV[2] < 0.0f) {
//                    pixelHSV[2] = 0.0f;
//                } else if (pixelHSV[2] > 1.0f) {
//                    pixelHSV[2] = 1.0f;
//                }
//
//                // Convert back from HSV to Color
//                mapDestColor[index] = Color.HSVToColor(alpha, pixelHSV);
//
//                index++;
//            }
//        }
//
//        return Bitmap.createBitmap(mapDestColor, w, h, Bitmap.Config.ARGB_8888);
//
//    }

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
            ((MainApplication) context.getApplicationContext()).setHeadObject(gson.fromJson(reader, HeadObject.class));

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
//            makeFirstHSV(id);
        }
    }

//    public void makeFirstHSV(int id) {
//        float[] thumbnailHSV = {130, 0, 0};  //todo load for json
//        Bitmap levelLocked = updateHSV(
//                imageManager.loadImageFromResource(
//                        R.drawable.level_locked,
//                        lengthManager.getLevelFrameWidth(),
//                        lengthManager.getLevelFrameHeight()),
//                thumbnailHSV[0], thumbnailHSV[1], thumbnailHSV[2]);
//
//        saveBitmap(levelLocked, id + "_levelLocked.png");
//
//        Bitmap levelUnlocked = updateHSV(
//                imageManager.loadImageFromResource(
//                        R.drawable.level_unlocked,
//                        lengthManager.getLevelFrameWidth(),
//                        lengthManager.getLevelFrameHeight()),
//                thumbnailHSV[0], thumbnailHSV[1], thumbnailHSV[2]);
//
//        saveBitmap(levelUnlocked, id + "_levelUnlocked.png");
//
//
//        float[] cheatButtonHSV = {130, 0, 0};//mLevel.getWrapperPackage().meta.getCheatButtonHSV();
//
//        Bitmap cheatBitmap = updateHSV(
//                imageManager.loadImageFromResource(
//                        R.drawable.cheat_button,
//                        lengthManager.getCheatButtonSize(),
//                        lengthManager.getCheatButtonSize()),
//                cheatButtonHSV[0], cheatButtonHSV[1], cheatButtonHSV[2]);
//
//        saveBitmap(cheatBitmap, id + "_cheatBitmap.png");
//
//
//        Bitmap backBitmap = updateHSV(
//                imageManager.loadImageFromResource(
//                        R.drawable.back_button,
//                        lengthManager.getCheatButtonSize(),
//                        lengthManager.getCheatButtonSize()),
//                cheatButtonHSV[0], cheatButtonHSV[1], cheatButtonHSV[2]);
//
//        saveBitmap(backBitmap, id + "_backBitmap.png");
//
//    }

    public void saveBitmap(Bitmap bitmap, String name) {

        File file = new File(context.getFilesDir().getPath() + "/Downloaded/", name);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkDB() {
        Log.e("db", "check");
        String currentDBPath = "/data/" + "ir.treeco.aftabe" + "/databases/" + "aftabe.db";
        File data = Environment.getDataDirectory();
        File currentDB = new File(data, currentDBPath);

        if (!currentDB.exists()) {
            restore();
            restoreDBJournal();
        }
    }

    public void restore() {
        Log.e("db", "Restore1");
        File sd = Environment.getExternalStorageDirectory().getAbsoluteFile();
        File data = Environment.getDataDirectory();
        FileChannel source;
        FileChannel destination;
        String currentDBPath = "/data/" + "ir.treeco.aftabe" + "/databases/" + "aftabe.db";
        String backupDBPath = "Android/a.mk";
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        Log.e("aa", currentDB.getPath());
        Log.e("bb", backupDB.getPath());
        try {
            currentDB.getParentFile().mkdirs();
            currentDB.createNewFile();
            byte[] keyBytes = getAESKey();
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            backupDB.deleteOnExit();
            backupDB.createNewFile();
            FileOutputStream fos = new FileOutputStream(currentDB);
            FileInputStream fis = new FileInputStream(backupDB);

            CipherInputStream cis = new CipherInputStream(fis, cipher);

            byte[] block = new byte[8];
            int i;
            while ((i = cis.read(block)) != -1) {
                fos.write(block, 0, i);
            }
            fos.close();
            cis.close();
            fis.close();
            Log.e("db", "Restore");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public void restoreDBJournal() {
        Log.e("db", "Restore1");
        File sd = Environment.getExternalStorageDirectory().getAbsoluteFile();
        File data = Environment.getDataDirectory();
        FileChannel source;
        FileChannel destination;
        String currentDBPath = "/data/" + "ir.treeco.aftabe" + "/databases/" + "aftabe.db-journal";
        String backupDBPath = "Android/b.mk";
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        Log.e("aa", currentDB.getPath());
        Log.e("bb", backupDB.getPath());
        try {
            currentDB.getParentFile().mkdirs();
            currentDB.createNewFile();

            byte[] keyBytes = getAESKey();
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            backupDB.deleteOnExit();
            backupDB.createNewFile();
            FileOutputStream fos = new FileOutputStream(currentDB);
            FileInputStream fis = new FileInputStream(backupDB);

            CipherInputStream cis = new CipherInputStream(fis, cipher);

            byte[] block = new byte[8];
            int i;
            while ((i = cis.read(block)) != -1) {
                fos.write(block, 0, i);
            }
            fos.close();
            cis.close();
            fis.close();

            Log.e("db", "Restore blocks " + i);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public static void backUpDB() {
        File sd = Environment.getExternalStorageDirectory().getAbsoluteFile();
        File data = Environment.getDataDirectory();
        FileChannel source;
        FileChannel destination;
        String currentDBPath = "/data/" + "ir.treeco.aftabe" + "/databases/" + "aftabe.db";
        String backupDBPath = "Android/a.mk";
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        Log.e("cc", currentDB.getPath());
        Log.e("dd", backupDB.getPath());
        try {
            byte[] keyBytes = getAESKey();
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            backupDB.deleteOnExit();
            backupDB.createNewFile();
            FileOutputStream fos = new FileOutputStream(backupDB);
            FileInputStream fis = new FileInputStream(currentDB);

            CipherOutputStream cos = new CipherOutputStream(fos, cipher);

            byte[] block = new byte[8];
            int i;
            while ((i = fis.read(block)) != -1) {
                cos.write(block, 0, i);
            }
            cos.close();
            fis.close();
            fos.close();

            Log.e("db", "backup ");
            backUpDBJournal();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public static void backUpDBJournal() {
        File sd = Environment.getExternalStorageDirectory().getAbsoluteFile();
        File data = Environment.getDataDirectory();
        FileChannel source;
        FileChannel destination;
        String currentDBPath = "/data/" + "ir.treeco.aftabe" + "/databases/" + "aftabe.db-journal";
        String backupDBPath = "Android/b.mk";
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);


        Log.e("cc", currentDB.getPath());
        Log.e("dd", backupDB.getPath());
        try {

            byte[] keyBytes = getAESKey();
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            backupDB.deleteOnExit();
            backupDB.createNewFile();
            FileOutputStream fos = new FileOutputStream(backupDB);
            FileInputStream fis = new FileInputStream(currentDB);

            CipherOutputStream cos = new CipherOutputStream(fos, cipher);

            byte[] block = new byte[8];
            int i;
            while ((i = fis.read(block)) != -1) {
                cos.write(block, 0, i);
            }
            cos.close();
            fis.close();
            fos.close();

            Log.e("db", "backup blocks " + i);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
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
        if (db.getLevels(packageObject.getId()) == null) {
            db.insertPackage(packageObject);
        }
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
//                        makeFirstHSV(id);
                    }
                }
        );
    }

    public static byte[] getAESKey() {

        String str = Prefs.getString(ENCRYPT_KEY, null);


        if (str == null)
            str = "1234567812345678"; // for users without deviceID

        Log.d(TAG, "key is " + str);
        for (int i = 0; i < 15; i++) {
            try {
                byte[] bytesOfMessage = str.getBytes("UTF-8");
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] thedigest = md.digest(bytesOfMessage);
                str = new String(thedigest);
            } catch (Exception e) {
            }
        }

        try {
            byte[] key = new byte[16];
            byte[] strBytes;
            strBytes = str.getBytes("UTF-8");
            int i = 0;
            while (i < 16 && i < strBytes.length)
                key[i] = strBytes[i++];
            while (i < 16)
                key[i++] = 100;
            Log.d(TAG, "new key is " + new String(key));
            return key;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "1234567812345678".getBytes();

        }

    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static void storeKey() {


        if (!isExternalStorageWritable())
            return;
        File rootFolder = new File(
                Environment.getExternalStorageDirectory(),
                "Android");
        rootFolder.mkdir();
        JSONObject jsonObject = new JSONObject();

        if (!Prefs.contains(SHARED_PREFS_TOKEN) || !Prefs.contains(ENCRYPT_KEY))
            return;
        try {

            Gson gson = new Gson();
            TokenHolder tokenHolder = gson.fromJson(Prefs.getString(SHARED_PREFS_TOKEN, ""), TokenHolder.class);
            SaveHolder saveHolder = new SaveHolder(tokenHolder, Prefs.getString(ENCRYPT_KEY, ""));

            FileOutputStream fileOutputStream = new FileOutputStream(
                    new File(rootFolder, ".system64a"));
            fileOutputStream.write(gson.toJson(saveHolder).getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void checkKey() {
        if (!isExternalStorageWritable())
            return;
        File rootFolder = new File(Environment.getExternalStorageDirectory(),
                "Android");
        if (!rootFolder.exists())
            return;
        File file = new File(rootFolder, ".system64a");
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (scanner == null)
            return;

        String temp = "";
        while (scanner.hasNextLine()) {
            temp += scanner.nextLine();
        }

        if (temp.equals(""))
            return;
        Gson gson = new Gson();
        try {
            SaveHolder saveHolder = gson.fromJson(temp, SaveHolder.class);
            Prefs.putString(SHARED_PREFS_TOKEN, gson.toJson(saveHolder.getTokenHolder()));
            Prefs.putString(ENCRYPT_KEY, saveHolder.getKey());

        } catch (Exception e) {
            return;
        }


    }

    public static void updateSharedPrefsToken(User user, TokenHolder tokenHolder) {
        Gson gson = new Gson();
        Prefs.putString(SHARED_PREFS_TOKEN, gson.toJson(tokenHolder));
        Prefs.putString(ENCRYPT_KEY, user.getKey());
        storeKey();
        backUpDB();
        backUpDBJournal();

    }

    public static TokenHolder getTokenHolder() {
        String tkJson = Prefs.getString(SHARED_PREFS_TOKEN, "");
        if (tkJson.compareTo("") == 0) {
            return null;
        }
        try {
            Gson gson = new Gson();
            return gson.fromJson(tkJson, TokenHolder.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isUserRegistered() {

        String tkJson = Prefs.getString(SHARED_PREFS_TOKEN, "");
        if (tkJson.compareTo("") == 0) {
            return false;
        }
        return true;
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static final Pattern VALID_PHONE =
            Pattern.compile("^09[0-9]{9}$");
    public static final Pattern VALID_PHONE_2 =
            Pattern.compile("^9[0-9]{9}$");

    public static boolean isAEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public static boolean isAPhoneNumber(String number) {
        Matcher matcher = VALID_PHONE.matcher(number);
        return matcher.find() || VALID_PHONE_2.matcher(number).find();
    }



}
