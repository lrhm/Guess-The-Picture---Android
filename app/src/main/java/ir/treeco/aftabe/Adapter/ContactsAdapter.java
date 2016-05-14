package ir.treeco.aftabe.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import ir.treeco.aftabe.API.AftabeAPIAdapter;
import ir.treeco.aftabe.API.BatchUserFoundListener;
import ir.treeco.aftabe.API.UserFoundListener;
import ir.treeco.aftabe.API.Utils.ContactsHolder;
import ir.treeco.aftabe.Adapter.Cache.ContactsCacheHolder;
import ir.treeco.aftabe.Adapter.Cache.FriendsHolder;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.Util.RandomString;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Activity.MainActivity;
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

        getContacts();

    }


    public void getContacts() {

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && !Prefs.getBoolean(MainActivity.CONTACTS_PERMISSION, false)) {
            return;
            // failed to get
        }
        if (!Tools.isUserRegistered() || Tools.getCachedUser() == null) {
            return;
        }


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
        phones.close();

        Log.d(TAG, set.size() + " size of set");
        for (ContactsHolder contactsHolder : set)
            contactsHolders.add(contactsHolder);

//        onNewContact(contactsHolders.poll());

        doQueue();

        if (contactsHolders.size() == 0 && !Prefs.getBoolean("cts_checked_aftabe", false)) {

            AftabeAPIAdapter.getCTS(this);
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

        Log.d(TAG, "updating a new contact");
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


        Prefs.putBoolean("cts_checked_aftabe", true);
        ArrayList<User> friendList = FriendsHolder.getInstance().getFriends();
        for (User user : users)
            if (!friendList.contains(user))
                FriendsHolder.getInstance().addToContacts(user);

    }

    @Override
    public void onGotError() {

    }
}
