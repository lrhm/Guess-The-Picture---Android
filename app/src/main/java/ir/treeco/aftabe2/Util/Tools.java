package ir.treeco.aftabe2.Util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import ir.treeco.aftabe2.API.Rest.AftabeAPIAdapter;
import ir.treeco.aftabe2.API.Socket.SocketAdapter;
import ir.treeco.aftabe2.Adapter.DBAdapter;
import ir.treeco.aftabe2.MainApplication;
import ir.treeco.aftabe2.Object.SaveHolder;
import ir.treeco.aftabe2.Object.TokenHolder;
import ir.treeco.aftabe2.Object.User;
import ir.treeco.aftabe2.View.Activity.MainActivity;

public class Tools {
    private Context context;
    private LengthManager lengthManager;
    private ImageManager imageManager;
    public final static String ENCRYPT_KEY = "shared_prefs_last_long";
    public final static String USER_SAVED_DATA = "shared_prefs_user";
    public final static String SHARED_PREFS_TOKEN = "shared_prefs_tk";
    public final static String SHARED_PREFS_SEED = "shared_prefs_seed_key";
    private final static String TAG = "Tools";


    private static boolean test = false;


    private static final String testUrl = "http://server.pakoo.ir:2020/";
    private static final String baseUrl = "https://aftabe2.com:2020/";

    public static String getUrl() {
        if (test)
            return testUrl;
        return baseUrl;
    }


    public Tools(Context context) {
        this.context = context;
        lengthManager = ((MainApplication) context.getApplicationContext()).getLengthManager();
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

    public static int convertDPtoPixel(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
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
        for (int i = views.length - 1; i > 0; i--)
            linearLayout.addView(views[i]);
    }

    public static String numeralStringToPersianDigits(String s) {
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

    public void checkDB(Context context) {
//        Logger.e("db", "check");
        String currentDBPath = context.getFilesDir().getParent() + "/databases/" + "aftabe.db";

        File currentDB = new File(currentDBPath);

        if (!currentDB.exists()) {
            restore();
            restoreDBJournal();
        }
    }

    public void restore() {


        File sd = Environment.getExternalStorageDirectory().getAbsoluteFile();
        String currentDBPath = context.getFilesDir().getParent() + "/databases/" + "aftabe.db";
        String backupDBPath = "Android/.amk";
        File currentDB = new File(currentDBPath);
        File backupDB = new File(sd, backupDBPath);


        try {
            currentDB.getParentFile().mkdirs();
            currentDB.createNewFile();

            byte[] keyBytes = getAESKey();
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            FileOutputStream fos = new FileOutputStream(currentDB);
            FileInputStream fis = new FileInputStream(backupDB);

            CipherInputStream cis = new CipherInputStream(fis, cipher);

            byte[] block = new byte[1024];
            int i;
            while ((i = cis.read(block)) != -1) {
                fos.write(block, 0, i);
            }
            fos.close();
            cis.close();
            fis.close();
//            Logger.e("db", "Restore");
        } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
//            e.printStackTrace();
        }
    }

    public void restoreDBJournal() {

        File sd = Environment.getExternalStorageDirectory().getAbsoluteFile();
        String currentDBPath = context.getFilesDir().getParent() + "/databases/" + "aftabe.db-journal";
        String backupDBPath = "Android/.bmk";
        File currentDB = new File(currentDBPath);
        File backupDB = new File(sd, backupDBPath);


        try {
            currentDB.getParentFile().mkdirs();
            currentDB.createNewFile();

            byte[] keyBytes = getAESKey();
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);


            FileOutputStream fos = new FileOutputStream(currentDB);
            FileInputStream fis = new FileInputStream(backupDB);

            CipherInputStream cis = new CipherInputStream(fis, cipher);

            byte[] block = new byte[1024];
            int i;
            while ((i = cis.read(block)) != -1) {
                fos.write(block, 0, i);
            }
            fos.close();
            cis.close();
            fis.close();

//            Logger.e("db", "Restore blocks " + i);
        } catch (IOException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException e) {
//            e.printStackTrace();
        }
    }

    private static Object lock = new Object();
    private static boolean isBackupInProgress = false;

    public static void backUpDB(final Context context) {

        synchronized (lock) {
            if (isBackupInProgress)
                return;
            isBackupInProgress = true;
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                backUpDBAsync(context);

                synchronized (lock) {
                    isBackupInProgress = false;

                }
            }
        }).start();


    }

    public static void backUpDBAsync(Context context) {

        File sd = Environment.getExternalStorageDirectory().getAbsoluteFile();
        String currentDBPath = context.getFilesDir().getParent() + "/databases/" + "aftabe.db";
        String backupDBPath = "Android/.amk";
        File currentDB = new File(currentDBPath);
        File backupDB = new File(sd, backupDBPath);


        try {
            byte[] keyBytes = getAESKey();
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            backupDB.createNewFile();
            FileOutputStream fos = new FileOutputStream(backupDB);
            FileInputStream fis = new FileInputStream(currentDB);

            CipherOutputStream cos = new CipherOutputStream(fos, cipher);

            byte[] block = new byte[1024];
            int i;
            while ((i = fis.read(block)) != -1) {
                cos.write(block, 0, i);

            }
            cos.close();
            fis.close();
            fos.close();

            backUpDBJournal(context);
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
//            e.printStackTrace();
        }
    }


    public static void backUpDBJournal(final Context context) {


        backUpDBJournalAsync(context);

    }

    public static void backUpDBJournalAsync(Context context) {

        File sd = Environment.getExternalStorageDirectory().getAbsoluteFile();
        String currentDBPath = context.getFilesDir().getAbsolutePath() + "/databases/" + "aftabe.db-journal";
        String backupDBPath = "Android/.bmk";
        File currentDB = new File(currentDBPath);
        File backupDB = new File(sd, backupDBPath);

        try {

            byte[] keyBytes = getAESKey();
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            backupDB.createNewFile();

            FileOutputStream fos = new FileOutputStream(backupDB);
            FileInputStream fis = new FileInputStream(currentDB);

            CipherOutputStream cos = new CipherOutputStream(fos, cipher);

            byte[] block = new byte[1024];
            int i;
            while ((i = fis.read(block)) != -1) {
                cos.write(block, 0, i);
            }
            cos.close();
            fis.close();
            fos.close();

//            Logger.e("db", "backup blocks " + i);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException e) {
//            e.printStackTrace();
        }
    }

    public static byte[] getAESKey() {

        String str = Prefs.getString(ENCRYPT_KEY, null);

        if (str == null)
            str = "1234567812345678"; // for users without deviceID

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

            if (saveHolder.getInstaUsed())
                StoreAdapter.useInsta();
            if (saveHolder.getTelegramUsed())
                StoreAdapter.useTelegram();

        } catch (Exception e) {
            return;
        }


    }

    public static void updateSharedPrefsToken(Context context, User user, TokenHolder tokenHolder) {
        Gson gson = new Gson();
        Prefs.putString(SHARED_PREFS_TOKEN, gson.toJson(tokenHolder));
        String oldKey = Prefs.getString(ENCRYPT_KEY, "");
        User cachedUser = getCachedUser(null);


        Logger.d(TAG, "updateSharedPrefsToken");
        Prefs.putDouble(Tools.SHARED_PREFS_SEED, user.getSeed());
        Prefs.putString(USER_SAVED_DATA, new Gson().toJson(user));

        if (!oldKey.equals(user.getKey()) || cachedUser == null || !cachedUser.getId().equals(user.getId())
                || !cachedUser.getLoginInfo().getAccessToken().equals(tokenHolder.getLoginInfo().accessToken)
                || !cachedUser.getName().equals(user.getName())) { // first login

            Logger.d(TAG, "updateSharedPrefsToken do it");
            Prefs.putString(ENCRYPT_KEY, user.getKey());
            storeKey();
            backUpDB(context);
            DBAdapter dbAdapter = DBAdapter.getInstance(context);

            for (User.PackageInfo info : user.getPackageInfos()) {
                File file = new File(context.getFilesDir().getPath() + "/Packages/package_" + info.getId() + "/");
                if (file.exists()) {
//                    Logger.d(TAG, "package " + info.getId() + " exist");
                    for (int i = 0; i < info.getIndex(); i++) {
                        dbAdapter.resolveLevel(info.getId(), i);
//                        Logger.d(TAG, "resloving level " + i);
                    }
                }

            }

            if (!user.isGuest()) {
                SocketAdapter.reInitiSocket();
                AftabeAPIAdapter.getListOfMyFriends(user, ((MainActivity) context).mainFragment.getFriendListFragment());
            }
        }

    }

    public static TokenHolder getTokenHolder() {
        String tkJson = Prefs.getString(USER_SAVED_DATA, "");
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

        String tkJson = Prefs.getString(USER_SAVED_DATA, "");
        return tkJson.compareTo("") != 0;
    }

    public static boolean isThereOldUserToken() {
        String tkJson = Prefs.getString(SHARED_PREFS_TOKEN, "");
        return tkJson.compareTo("") != 0;

    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static final Pattern VALID_PHONE =
            Pattern.compile("^09[0-9]{9}$");
    public static final Pattern VALID_PHONE_2 =
            Pattern.compile("^9[0-9]{9}$");
    public static final Pattern VALID_PHONE_3 =
            Pattern.compile("^989[0-9]{9}$");
    public static final Pattern VALID_PHONE_4 =
            Pattern.compile("^00989[0-9]{9}$");

    public static final Pattern VALID_NAME_PATTERN_REGEX = Pattern.compile("[a-zA-Z_0-9]+$");

    public static boolean isNameValid(String string) {
        return VALID_NAME_PATTERN_REGEX.matcher(string).find();
    }

    public static boolean isAEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public static boolean isAPhoneNumber(String number) {
        Matcher matcher = VALID_PHONE.matcher(number);
        return matcher.find() || VALID_PHONE_2.matcher(number).find() || VALID_PHONE_3.matcher(number).find()
                || VALID_PHONE_3.matcher(number).find() || VALID_PHONE_4.matcher(number).find();
    }

    public static User getCachedUser(Context context) {

        if (context != null && context instanceof MainActivity) {
            User user = ((MainActivity) context).getMyUser();
            if (user != null)
                return user;
        }

        if (Prefs.contains(Tools.USER_SAVED_DATA)) {
            String jsonString = Prefs.getString(Tools.USER_SAVED_DATA, "");
            Gson gson = new Gson();
            try {
                User user = gson.fromJson(jsonString, User.class);

                return user;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static void cacheUser(User user) {
        Prefs.putString(USER_SAVED_DATA, new Gson().toJson(user));
    }


    public static double getSeed() {

        if (Prefs.contains(SHARED_PREFS_SEED)) {
            return Prefs.getDouble(SHARED_PREFS_SEED, 0.85);
        }

        Random random = new Random(System.currentTimeMillis());
        double seed = random.nextDouble();
        Prefs.putDouble(SHARED_PREFS_SEED, seed);
        return seed;
    }

    public static String mySigCheck(Context context) {
        String sigChk = "Bsaq";

        Signature[] signature = new Signature[1];

        try {
            signature = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures;

//            Log.d("Hash", Integer.toString(signature[0].hashCode())); //<< Prints your signature. Remove once you know this and have changed it below.

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (signature[0].hashCode() == -1769074008 || signature[0].hashCode() == 254943616) {

            sigChk = "TTy";
        }

        return sigChk;
    }

}
