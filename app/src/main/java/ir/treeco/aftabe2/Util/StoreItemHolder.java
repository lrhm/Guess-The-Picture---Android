package ir.treeco.aftabe2.Util;

import java.util.HashMap;

/**
 * Created by al on 5/13/16.
 */
public class StoreItemHolder {

    public static final String SKU_VERY_SMALL_COIN = "very_small_coin";
    public static final String SKU_SMALL_COIN = "small_coin";
    public static final String SKU_MEDIUM_COIN = "medium_coin";
    public static final String SKU_BIG_COIN = "big_coin";

    public static final int AMOUNT_VERY_SMALL_COIN = 500;
    public static final int AMOUNT_SMALL_COIN = 2000;
    public static final int AMOUNT_MEDIUM_COIN = 4000;
    public static final int AMOUNT_BIG_COIN = 12500;
    public static final int COMMENT_BAZAAR = 300;
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
            COMMENT_BAZAAR,
            TAPSELL_VIDEO
    };

    final static int[] prices = new int[]{1000, 3000, 4000, 10000, -1, -1};

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

    public static Integer getPrice(String sku) {

        if (skuPrice == null) {
            skuPrice = new HashMap<>();

            for (int i = 0; i < SKUs.length; i++) {
                skuPrice.put(SKUs[i], prices[i]);
            }
        }

        return skuPrice.get(sku);
    }

}
