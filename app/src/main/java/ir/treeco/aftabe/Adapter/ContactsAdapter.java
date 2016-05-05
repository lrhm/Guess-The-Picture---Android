package ir.treeco.aftabe.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.HashMap;

import ir.treeco.aftabe.API.AftabeAPIAdapter;
import ir.treeco.aftabe.API.UserFoundListener;
import ir.treeco.aftabe.API.Utils.ContactsHolder;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.Util.RandomString;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Activity.MainActivity;
import retrofit.Callback;
import retrofit.Response;

/**
 * Created by root on 5/5/16.
 */
public class ContactsAdapter {

    private Context mContext;
    private DBAdapter dbAdapter;

    public ContactsAdapter(Context context) {
        mContext = context;
        dbAdapter = DBAdapter.getInstance(context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getContacts();

            }
        }).start();
    }


    public void getContacts() {

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && !Prefs.getBoolean(MainActivity.CONTACTS_PERMISSION, false)) {
            return;
            // failed to get
        }
        if (!Tools.isUserRegistered()) {
            return;
        }


        int counter = 25;
        Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        while (counter != 0 && phones.moveToNext()) {

            if (mContext == null)
                return;


            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            if (Build.VERSION.SDK_INT >= 11)
                name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));


            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String mail = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            if (mail == null)
                mail = "";

            String id = name + phoneNumber;
            if (!Prefs.contains(id.hashCode() + "")) {

                counter--;

                ContactsHolder contactsHolder = new ContactsHolder(name, mail, phoneNumber, id);
                onNewContact(contactsHolder);
            }
        }
        phones.close();

    }

    public void onNewContact(final ContactsHolder contactsHolder) {



        AftabeAPIAdapter.updateContact(contactsHolder, new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(Response<HashMap<String, String>> response) {

                if (response.isSuccess()) {
                    Prefs.putString(contactsHolder.getId().hashCode() + "", RandomString.nextString());
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }


}
