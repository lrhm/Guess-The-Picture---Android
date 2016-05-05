package ir.treeco.aftabe.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;

import com.pixplicity.easyprefs.library.Prefs;

import ir.treeco.aftabe.API.AftabeAPIAdapter;
import ir.treeco.aftabe.API.UserFoundListener;
import ir.treeco.aftabe.API.Utils.ContactsHolder;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Activity.MainActivity;

/**
 * Created by root on 5/5/16.
 */
public class ContactsAdapter {

    private Context mContext;
    private DBAdapter dbAdapter;
    private OnContactsListener onContactsListener;

    public ContactsAdapter(Context context, OnContactsListener onContactsListener) {
        mContext = context;
        dbAdapter = DBAdapter.getInstance(context);
        this.onContactsListener = onContactsListener;
        getContacts();
    }


    public void getContacts() {

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && !Prefs.getBoolean(MainActivity.CONTACTS_PERMISSION, false)) {
            return;
            // failed to get
        }


        Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {

            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String mail = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            if (mail == null)
                mail = "";

            phoneNumber = phoneNumber.replace(" ", "");

            phoneNumber = phoneNumber.replace("+", "00");
            Log.d("TAG", "phone number " + phoneNumber + " " + Tools.isAPhoneNumber(phoneNumber));
            if (Tools.isAPhoneNumber(phoneNumber)) {

                String id = name + phoneNumber;
                if (!dbAdapter.isContactInDB(id)) {
                    ContactsHolder contactsHolder = new ContactsHolder(name, mail, phoneNumber, id);
                    onContactsListener.onNewContactsFound(contactsHolder);
                }
            }

            phones.close();
        }

    }

    public interface OnContactsListener {
        void onNewContactsFound(ContactsHolder contactsHolder);
    }

}
