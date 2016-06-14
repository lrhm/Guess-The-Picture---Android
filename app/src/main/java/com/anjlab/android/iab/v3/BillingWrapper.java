package com.anjlab.android.iab.v3;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.android.vending.billing.IInAppBillingService;
//import ir.tgbs.iranapps.billing.IranAppsIabService;

public class BillingWrapper {
     public static String KEY_CAFE_BAZAAR = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwDMSVdcFg3nWIA42O4vJGpAAT55RwQht5RsjhguiIuS/yt9kfsZ2+fUbYyQT47VBxDaSWOZ/X7c1w0N+pwCaDp/TdtHnAN/dw7vJF0W8EhfNXV74mMMXKXzeA24v0jH/JOJ33LpDn6J7tIUmFTobBgFkx6asIEhWL+V6yeoyGHrDB2LCZMQSh6iXIyxEcBDzORCizBk10Ubw6K3d3W30vIx37s3x52tk9bgY1yExcMCAwEAAQ==";
    final private static String KEY_IRAN_APPS = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCXRdP4anjGTnagxoN5ttZ/94H8fI+QArRXtO3ZLJ2MgedS6pVZfgEvt6+HHVIJlBtowqVNcd3rw87PZZ86zSDj+XWteonRGwYULdlJkuRpAqbu74M4CkKf2w2CWcTzuPXW/NpUAG7WJ6v0JFGkthJOQVbdfxtE+Dh2KmgjAZMR0QIDAQAB";

    private final Context context;
    private String TAG = "BillingWrapper";

    public void release() {
        try {
            if (isAvailable(Service.CAFE_BAZAAR))
                context.unbindService(cafeBazaarServiceConn);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            cafeBazaarIabService = null;
        }

//        try {
////            if (isAvailable(Service.IRAN_APPS))
////                context.unbindService(iranAppsServiceConn);
//        } catch (Exception e) {
//            Log.e(TAG, e.toString());
//        } finally {
////            iranAppsIabService = null;
//        }
    }

    public enum Service {
        CAFE_BAZAAR,
        IRAN_APPS,
        BOTH
    }

    Service currentService = Service.CAFE_BAZAAR;
    //    IranAppsIabService iranAppsIabService = null;
    IInAppBillingService cafeBazaarIabService = null;

//    ServiceConnection iranAppsServiceConn = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            iranAppsIabService = IranAppsIabService.Stub.asInterface(service);
//            serviceConnected(Service.IRAN_APPS);
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            iranAppsIabService = null;
//            serviceDisconnected(Service.IRAN_APPS);
//        }
//    };

    private void serviceDisconnected(Service service) {
        Log.i(TAG, "Service " + service + " disconnected!");
        if (mServiceConnection != null)
            mServiceConnection.onServiceConnected(null, null);
    }

    private void serviceConnected(Service service) {
        Log.i(TAG, "Service " + service + " connected!");
        if (mServiceConnection != null)
            mServiceConnection.onServiceDisconnected(null);
    }

    ServiceConnection cafeBazaarServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            cafeBazaarIabService = IInAppBillingService.Stub.asInterface(iBinder);
            serviceConnected(Service.CAFE_BAZAAR);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            cafeBazaarIabService = null;
            serviceDisconnected(Service.CAFE_BAZAAR);
        }
    };

    boolean isAvailable(Service service) {
        if (isPreferred(service)) {
            switch (service) {
//                case IRAN_APPS:
//                    return iranAppsIabService != null;
                case CAFE_BAZAAR:
                    return cafeBazaarIabService != null;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    public BillingWrapper(Context context, Service currentService) {
        this.currentService = currentService;
        this.context = context;


        bindToServices();
    }

    private void bindToServices() {
//        if (isPreferred(Service.IRAN_APPS)) {
////            Intent serviceIntent = new Intent(IranAppsIabService.class.getName());
//            // TODO Armin: sometimes want to bind service that is already binded!!!(double back on ehem loading)
//
//            try {
//                context.bindService(serviceIntent, iranAppsServiceConn, Context.BIND_AUTO_CREATE);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

//        if (isPreferred(Service.CAFE_BAZAAR)) {
            Intent serviceIntent = new Intent("ir.cafebazaar.pardakht.InAppBillingService.BIND");
            serviceIntent.setPackage("com.farsitel.bazaar");
            context.bindService(serviceIntent, cafeBazaarServiceConn, Context.BIND_AUTO_CREATE);
//        }
    }

    private boolean isPreferred(Service service) {
        return currentService == Service.BOTH || currentService == service;
    }

    Bundle getSkuDetails(int apiVersion, String packageName, String type, Bundle skuBundle) {
        try {
//            if (isAvailable(Service.IRAN_APPS)) {
//                return iranAppsIabService.getSkuDetails(apiVersion, packageName, type, skuBundle);
//            } else if (isAvailable(Service.CAFE_BAZAAR)) {
            return cafeBazaarIabService.getSkuDetails(apiVersion, packageName, type, skuBundle);
//            }
        } catch (RemoteException e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    Bundle getBuyIntent(int apiVersion, String packageName, String sku, String type, String developerPayload) {
        try {
//            if (isAvailable(Service.IRAN_APPS)) {
//                return iranAppsIabService.getBuyIntent(apiVersion, packageName, sku, type, developerPayload);
//            } else if (isAvailable(Service.CAFE_BAZAAR)) {
                return cafeBazaarIabService.getBuyIntent(apiVersion, packageName, sku, type, developerPayload);
//            }
        } catch (RemoteException e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    Bundle getPurchases(int apiVersion, String packageName, String type, String continuationToken) {
        try {
//            if (isAvailable(Service.IRAN_APPS)) {
//                return iranAppsIabService.getPurchases(apiVersion, packageName, type, continuationToken);
//            } else if (isAvailable(Service.CAFE_BAZAAR)) {
                return cafeBazaarIabService.getPurchases(apiVersion, packageName, type, continuationToken);
//            }
        } catch (RemoteException e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    Integer consumePurchase(int apiVersion, String packageName, String purchaseToken) {
        try {
//            if (isAvailable(Service.IRAN_APPS)) {
//                return iranAppsIabService.consumePurchase(apiVersion, packageName, purchaseToken);
//            } else if (isAvailable(Service.CAFE_BAZAAR)) {
                return cafeBazaarIabService.consumePurchase(apiVersion, packageName, purchaseToken);
//            }
        } catch (RemoteException e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    String getKey() {
        if (isAvailable(Service.IRAN_APPS)) {
            return KEY_IRAN_APPS;
        } else if (isAvailable(Service.CAFE_BAZAAR)) {
            return KEY_CAFE_BAZAAR;
        }
        return null;
    }

    boolean isBillingInitialized() {
        return isAvailable(Service.IRAN_APPS) || isAvailable(Service.CAFE_BAZAAR);
    }

    ServiceConnection mServiceConnection = null;

    void setServiceConnection(ServiceConnection serviceConnection) {
        this.mServiceConnection = serviceConnection;
    }
}
