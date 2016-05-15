package ir.treeco.aftabe.Object;

import java.util.HashMap;

/**
 * Created by al on 5/13/16.
 */
public class StoreItemHolder {

    public static final String SKU_VERY_SMALL_COIN = "very_small_coin";
    public static final String SKU_SMALL_COIN = "small_coin";
    public static final String SKU_MEDIUM_COIN = "medium_coin";
    public static final String SKU_BIG_COIN = "big_coin";
    static final String[] SKUs = new String[]{
            SKU_VERY_SMALL_COIN,
            SKU_SMALL_COIN,
            SKU_MEDIUM_COIN,
            SKU_BIG_COIN
    };

    private static HashMap<String, Integer> skuPrice;
    private static StoreItemHolder instace;
    private static Object lock = new Object();
    public static StoreItemHolder getInstnce(){
        synchronized (lock){
            if(instace != null)
                return instace;

            instace = new StoreItemHolder();
            instace.skuPrice = new HashMap<>();
            final int[] prices = new int[]{450, 800, 1500, 5000, -1, -1};

            for(int i = 0 ; i < SKUs.length ; i++){
                skuPrice.put(SKUs[i], prices[i]);
            }
            return instace;
        }

    }

    public Integer getPrice(String sku){
        return skuPrice.get(sku);
    }

}
