package ir.treeco.aftabe.Adapter;

import android.util.Log;

import com.pixplicity.easyprefs.library.Prefs;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Minutes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * Created by al on 5/15/16.
 *
 *
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

    public void setRequestTime(){
        requestTime = System.currentTimeMillis();
    }

    public boolean isRequestForPlay(){
        return !(requestTime == null) && System.currentTimeMillis() - requestTime < 90 * 1000;
    }

    public boolean isThereOfflineOffer() {


        try {
            Date now = Calendar.getInstance().getTime();

            Date past = new SimpleDateFormat("dd-MM-yyyy").
                    parse(Prefs.getString(
                            dateKey, new SimpleDateFormat("dd-MM-yyyy").format(now)));

            int days = Days.daysBetween(new DateTime(past), new DateTime(now)).getDays();


            if (!Prefs.contains(dateKey) || days >= 1) {

                int counter = Prefs.getInt(countKey, 2);
                return counter > 0;
            }

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
        }

    }

    private void saveNow() {

        Prefs.putString(dateKey,
                new SimpleDateFormat("dd-MM-yyyy")
                        .format(Calendar.getInstance().getTime()));

    }
}
