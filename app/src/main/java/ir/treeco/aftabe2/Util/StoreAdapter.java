package ir.treeco.aftabe2.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.HashMap;

import ir.tapsell.tapsellvideosdk.developer.CheckCtaAvailabilityResponseHandler;
import ir.tapsell.tapsellvideosdk.developer.DeveloperInterface;
import ir.treeco.aftabe2.Synchronization.Synchronize;
import ir.treeco.aftabe2.View.Custom.ToastMaker;
import ir.treeco.aftabe2.View.Dialog.DialogAdapter;

/**
 * Created by al on 5/13/16.
 */
public class StoreAdapter {

    public static final String SKU_VERY_SMALL_COIN = "very_small_coin";
    public static final String SKU_SMALL_COIN = "small_coin";
    public static final String SKU_MEDIUM_COIN = "medium_coin";
    public static final String SKU_BIG_COIN = "big_coin";

    public static final int AMOUNT_VERY_SMALL_COIN = 500;
    public static final int AMOUNT_SMALL_COIN = 2000;
    public static final int AMOUNT_MEDIUM_COIN = 4000;
    public static final int AMOUNT_BIG_COIN = 12500;
//    public static final int COMMENT_BAZAAR = 50;
    public static final int TELEGRAM = 50;
    public static final int INSTA = 50;
    public static final int TAPSELL_VIDEO = 20;


    static final int[] amounts = new int[]{
            AMOUNT_VERY_SMALL_COIN,
            AMOUNT_SMALL_COIN,
            AMOUNT_MEDIUM_COIN,
            AMOUNT_BIG_COIN
    };

    public final static int[] revenues = new int[]{
            AMOUNT_VERY_SMALL_COIN,
            AMOUNT_SMALL_COIN,
            AMOUNT_MEDIUM_COIN,
            AMOUNT_BIG_COIN,
            TAPSELL_VIDEO,
//            COMMENT_BAZAAR,
            TELEGRAM,
            INSTA
    };


    final static int[] prices = new int[]{1000, 3000, 4000, 10000, -1, -1, -1, -1};

    public static int getTelegramAmount() {
        return TELEGRAM;
    }

    public static int getInstaAmount() {
        return INSTA;
    }

    public static int getTapsellVideoAmount() {
        return TAPSELL_VIDEO;
    }

//    public static int getCommentBazaarAmount() {
////        return COMMENT_BAZAAR;
////    }

    static final String[] SKUs = new String[]{
            SKU_VERY_SMALL_COIN,
            SKU_SMALL_COIN,
            SKU_MEDIUM_COIN,
            SKU_BIG_COIN
    };

    private static HashMap<String, Integer> skuPrice;

    private static HashMap<String, Integer> skuAmount;

    public static int[] getRevenues() {
        return revenues;
    }

    public static int[] getPrices() {
        return prices;
    }

    public static String[] getSKUs() {
        return SKUs;
    }

    public static Integer getSkuAmount(String sku) {

        if (skuAmount == null) {
            skuAmount = new HashMap<>();
            for (int i = 0; i < SKUs.length; i++) {
                skuAmount.put(SKUs[i], amounts[i]);
            }
        }

        return skuAmount.get(sku);

    }

    public static boolean isInstaUsed() {
        return Prefs.getBoolean("com.instagram.android_view", false);
    }


    public static boolean isTelegramUsed() {
        return Prefs.getBoolean("org.telegram.messenger_view", false);
    }

    public static void useInsta() {
        Prefs.putBoolean("com.instagram.android_view", true);
    }

    public static void useTelegram() {
        Prefs.putBoolean("org.telegram.messenger_view", true);
    }

    public static Integer getPrice(String sku) {

        if (skuPrice == null) {
            skuPrice = new HashMap<>();

            for (int i = 0; i < SKUs.length; i++) {
                skuPrice.put(SKUs[i], prices[i]);
            }
        }

        return skuPrice.get(sku);
    }

    private static long lastClickTapsellAvailable = 0;

    public static void checkTapsellAvailable(final Activity activity, final boolean forCoin, final OnTapsellAvailability onTapsell) {
        DeveloperInterface.getInstance(activity)
                .checkCtaAvailability(
                        activity, DeveloperInterface.DEFAULT_MIN_AWARD,
                        DeveloperInterface.VideoPlay_TYPE_NON_SKIPPABLE, new CheckCtaAvailabilityResponseHandler() {
                            @Override
                            public void onResponse(Boolean isConnected, Boolean isAvailable) {


                                if (Synchronize.isOnline(activity)) {

                                    if (!isAvailable || !isConnected) {

                                        Answers.getInstance().logCustom(new CustomEvent("TapsellError"));

                                        long curTime = System.currentTimeMillis();
                                        if (curTime - lastClickTapsellAvailable > 2000) {
                                            ToastMaker.show(activity, "لطفا چند لحظه بعد تلاش کنید", Toast.LENGTH_SHORT);
                                            lastClickTapsellAvailable = curTime;
                                        }
                                    }
                                    Answers.getInstance().logCustom(new CustomEvent("Tapsell")
                                            .putCustomAttribute("isAvailable",
                                                    !isAvailable || !isConnected ? 0 : 100)
                                            .putCustomAttribute("Availibility",
                                                    !isAvailable || !isConnected ? "false" : "true"));

                                    if (onTapsell != null)
                                        onTapsell.onAvailable(isAvailable);

                                    if (isConnected && isAvailable)
                                        DeveloperInterface.getInstance(activity).showNewVideo(activity,
                                                DeveloperInterface.TAPSELL_DIRECT_ADD_REQUEST_CODE + (forCoin ? 0 : 1),
                                                DeveloperInterface.DEFAULT_MIN_AWARD,
                                                DeveloperInterface.VideoPlay_TYPE_NON_SKIPPABLE);

                                } else {
                                    DialogAdapter.checkInternetConnection(activity);
                                }


                            }
                        });
    }


    public static boolean startViewPages(Context context, String appName, String name, String parse) {
        boolean isAppInstalled = isAppAvailable(context, appName);
        if (isAppInstalled) {
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(parse));
            myIntent.setPackage(appName);
            context.startActivity(myIntent);


        } else {

            ToastMaker.show(context, "نصب نیست " + name, Toast.LENGTH_SHORT);
        }
        return isAppInstalled;
    }

    public static boolean startInstaIntent(Context context) {
        String appName = "com.instagram.android";

        String parse = "http://instagram.com/_u/aftabe2";

        String name = "اینستا";

        return startViewPages(context, appName, name, parse);


    }

    public static boolean startTelegramIntent(Context context) {
        String appName = "org.telegram.messenger";

        String parse = "https://telegram.me/aftabe2";

        String name = "تلگرام";

        return startViewPages(context, appName, name, parse);
    }

    public static boolean isAppAvailable(Context context, String appName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(appName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public interface OnTapsellAvailability {
        void onAvailable(boolean avail);
    }
}
