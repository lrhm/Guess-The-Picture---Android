package ir.treeco.aftabe2.Adapter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import ir.treeco.aftabe2.Util.Logger;

import com.pixplicity.easyprefs.library.Prefs;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import ir.treeco.aftabe2.API.Rest.AftabeAPIAdapter;
import ir.treeco.aftabe2.API.Rest.Interfaces.BatchUserFoundListener;
import ir.treeco.aftabe2.API.Rest.Utils.ContactsHolder;
import ir.treeco.aftabe2.Adapter.Cache.ContactsCacheHolder;
import ir.treeco.aftabe2.Adapter.Cache.FriendsHolder;
import ir.treeco.aftabe2.Object.User;
import ir.treeco.aftabe2.Util.Tools;
import ir.treeco.aftabe2.View.Custom.ToastMaker;
import retrofit.Callback;
import retrofit.Response;

/**
 * Created by root on 5/5/16.
 */
public class ContactsAdapter implements BatchUserFoundListener {

    private static final String TAG = "ContactsAdapter";
    private static final String TAG_CACHE = "contacts_cached_aftabe";

    private Timer mTimer;
    private Context mContext;
    private DBAdapter dbAdapter;
    private Queue<ContactsHolder> contactsHolders;
    private ContactsCacheHolder contactsCacheHolder;

    public ContactsAdapter(Context context) {
        mContext = context;
        dbAdapter = DBAdapter.getInstance(context);
        contactsHolders = new LinkedList<>();
        contactsCacheHolder = ContactsCacheHolder.getInstance();


//        if (Logger.isDebug())
//            return;
        getContacts();

    }


    public void getContacts() {

//        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
//            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
//                return;
//            // failed to get
//        }
        User user = Tools.getCachedUser(mContext);
        if (user == null)
            user = HiddenAdapter.getInstance().getHiddenUsr();
        if (user == null)
            return;


        Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        HashSet<ContactsHolder> set = new HashSet<>();

        while (phones != null && phones.moveToNext()) {

            if (mContext == null)
                return;


            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            if (Build.VERSION.SDK_INT >= 11)
                name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));


            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String mail = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            if (mail == null)
                mail = "";


            phoneNumber = phoneNumber.replace(" ", "");
            phoneNumber = phoneNumber.replace("-", "");
            phoneNumber = phoneNumber.replace(")", "");
            phoneNumber = phoneNumber.replace("(", "");


            ContactsHolder contactsHolder = new ContactsHolder(name, mail, phoneNumber);
            if (!contactsCacheHolder.contains(contactsHolder)) {
                set.add(new ContactsHolder(name, mail, phoneNumber));

            }

        }
        if (phones != null) {
            phones.close();
        }

        Logger.d(TAG, set.size() + " size of set");
        for (ContactsHolder contactsHolder : set)
            contactsHolders.add(contactsHolder);

//        onNewContact(contactsHolders.poll());


        if (contactsHolders.size() != 0) {

            if (mContext != null)
                if (!Tools.mySigCheck(mContext).equalsIgnoreCase("TTy")) {

                    ToastMaker.show(mContext, "شما هم افرین :) ***", Toast.LENGTH_SHORT);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            android.os.Process.killProcess(android.os.Process.myPid());

                        }
                    }, 2500);
                }
            return;
        }

        doQueue();


        Date now = Calendar.getInstance().getTime();
        try {
            String pastString = Prefs.getString("cts_check_date", "");
            if (pastString.equals("")) {
                AftabeAPIAdapter.getCTS(this);
                return;
            }
            Date past = new SimpleDateFormat("dd-MM-yyyy").
                    parse(Prefs.getString(
                            "cts_check_date", new SimpleDateFormat("dd-MM-yyyy").format(now)));
            int days = Days.daysBetween(new DateTime(past), new DateTime(now)).getDays();
            if (days >= 1) {
                AftabeAPIAdapter.getCTS(this);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void doQueue() {

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                if (contactsHolders.size() == 0)
                    mTimer.cancel();
                else {
                    onNewContact(contactsHolders.poll());
                }
            }
        };

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(timerTask, 4000, 1500);

    }


    public void onNewContact(final ContactsHolder contactsHolder) {

        if (contactsHolder == null)
            return;

        AftabeAPIAdapter.updateContact(contactsHolder, new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(Response<HashMap<String, String>> response) {

                if (response.isSuccess()) {
                }
                contactsCacheHolder.addToList(contactsHolder);

            }

            @Override
            public void onFailure(Throwable t) {

                mTimer.cancel();
            }
        });
    }


    @Override
    public void onGotUserList(User[] users) {


        Logger.d(TAG, "checked contacts");
        ArrayList<User> friendList = FriendsHolder.getInstance().getFriends();
        for (User user : users)
            if (!friendList.contains(user))
                FriendsHolder.getInstance().addToContacts(user);

        Prefs.putString("cts_check_date",
                new SimpleDateFormat("dd-MM-yyyy")
                        .format(Calendar.getInstance().getTime()));

    }

    @Override
    public void onGotError() {

    }
}
