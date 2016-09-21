package ir.treeco.aftabe2.Adapter.Cache;


import com.pixplicity.easyprefs.library.Prefs;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ir.treeco.aftabe2.Util.Logger;

/**
 * Created by al on 6/8/16.
 */
public class OnlineOfferAdapter {

    private static final String TAG = "OnlineOfferAdapter";
    private final String dateKey = "OFFER_ONLINE_PAST";
    private final String countKey = "Offer_Online_Counter";

    private static OnlineOfferAdapter instance;
    private Long requestTime;

    private static Object lock = new Object();

    public static OnlineOfferAdapter getInstance() {
        synchronized (lock) {

            if (instance == null)
                instance = new OnlineOfferAdapter();
            return instance;

        }
    }

    public OnlineOfferAdapter() {

    }


    public boolean isThereOfflineOffer() {


        try {
            Date now = Calendar.getInstance().getTime();

            Date past = new SimpleDateFormat("dd-MM-yyyy").
                    parse(Prefs.getString(
                            dateKey, new SimpleDateFormat("dd-MM-yyyy").format(now)));

            int days = Days.daysBetween(new DateTime(past), new DateTime(now)).getDays();


            if (!Prefs.contains(dateKey) || days >= 1) {
                Logger.d(TAG, "there is offer");

                int counter = Prefs.getInt(countKey, 2);
                return counter > 0;
            }
            Logger.d(TAG, "there is not offer");

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;

    }

    public void useOffer() {
        int counter = Prefs.getInt(countKey, 2);
        counter--;
        if (counter == 0) {
            saveNow();
            Prefs.putInt(countKey, 2);
        } else
            Prefs.putInt(countKey, counter);
        Logger.d(TAG , counter + " is the counter");



    }

    private void saveNow() {

        Prefs.putString(dateKey,
                new SimpleDateFormat("dd-MM-yyyy")
                        .format(Calendar.getInstance().getTime()));

    }
}
