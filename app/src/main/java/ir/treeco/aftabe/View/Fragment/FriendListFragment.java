package ir.treeco.aftabe.View.Fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;

import ir.treeco.aftabe.API.AftabeAPIAdapter;
import ir.treeco.aftabe.API.BatchUserFoundListener;
import ir.treeco.aftabe.API.UserFoundListener;
import ir.treeco.aftabe.Adapter.FriendsAdapter;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.FontsHolder;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.SizeConverter;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Custom.MyAutoCompleteTextView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendListFragment extends Fragment implements TextWatcher, View.OnClickListener,
        MyAutoCompleteTextView.OnKeyboardDismiss, TextView.OnEditorActionListener, UserFoundListener {

    ArrayAdapter<String> searchBarAdapter;
    ImageManager imageManager;
    RecyclerView mFriendsRecyclerView;
    FriendsAdapter mFriendsAdapter;
    MyAutoCompleteTextView mAutoCompleteTextView;
    View clearButton;
    View mainLayout;

    Boolean mAdaptersSet = false;


    public FriendListFragment() {


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        ((MainActivity) getActivity()).addUserFoundListener(new UserFoundListener() {
            @Override
            public void onGetUser(User user) {

            }

            @Override
            public void onGetError() {

            }

            @Override
            public void onGetMyUser(User myUser) {

                setUpAdapters();

            }
        });


        imageManager = ((MainApplication) getActivity().getApplication()).getImageManager();

        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);

        mainLayout = view.findViewById(R.id.friend_list_main_layout);
        mFriendsRecyclerView = (RecyclerView) view.findViewById(R.id.friends_recyler_view);


        clearButton = view.findViewById(R.id.clear_button);
        clearButton.setOnClickListener(this);
        clearButton.setVisibility(View.GONE);

        ((ImageView) clearButton).setImageBitmap(imageManager.loadImageFromResource(R.drawable.clear_button,
                (int) (SizeManager.getScreenWidth() * 0.15), (int) (SizeManager.getScreenWidth() * 0.15)));

        setUpAdapters();
        setUpRecylerViews();

//        String[] objct = new String[6];
//        objct[0] = "asghar";
//        objct[1] = "ahmad";
//        objct[5] = "ahmagh";
//        objct[2] = "sahar";
//        objct[3] = "golpar";
//        objct[4] = "saghi";


        TextInputLayout textInputLayout = (TextInputLayout) view.findViewById(R.id.search_text_input_layout);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) (SizeManager.getScreenWidth() * 0.5), (int) (SizeManager.getScreenHeight() * 0.1));
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        textInputLayout.setLayoutParams(layoutParams);

        mAutoCompleteTextView = (MyAutoCompleteTextView) view.findViewById(R.id.search_text_view);


        searchBarAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line);


        mAutoCompleteTextView.setAdapter(searchBarAdapter);
        mAutoCompleteTextView.setOnKeyboardDismiss(this);
        mAutoCompleteTextView.addTextChangedListener(this);
        mAutoCompleteTextView.setOnEditorActionListener(this);

        mAutoCompleteTextView.setTypeface(FontsHolder.getSansRegular(getContext()));
//
        ImageView searchFriendImageView = (ImageView) view.findViewById(R.id.search_friend_image);
        SizeConverter searchFriendConverter = SizeConverter.SizeConvertorFromWidth(SizeManager.getScreenWidth() * 0.8f, 1373, 227);
        searchFriendImageView.setImageBitmap
                (imageManager.loadImageFromResource(R.drawable.searchbar, searchFriendConverter.mWidth
                        , searchFriendConverter.mHeight, ImageManager.ScalingLogic.FIT));

        return view;
    }


    public void setUpAdapters() {

        if (mFriendsAdapter == null) mFriendsAdapter = new FriendsAdapter(null, null, null, null);

        if (getActivity() == null)
            return;

        User myUser = ((MainActivity) getActivity()).getMyUser();

        if (mAdaptersSet)
            return;

        if (myUser == null) {
            mAdaptersSet = false;
            return;

        }
        mAdaptersSet = true;

        getContacts();


        AftabeAPIAdapter.getListOfMyFriends(myUser, new BatchUserFoundListener() {
            @Override
            public void onGotUserList(final User[] users) {
                FriendListFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (User user : users)
                            mFriendsAdapter.addUser(user, FriendsAdapter.TYPE_FRIEND);
                    }
                });
            }

            @Override
            public void onGotError() {

            }
        });

        AftabeAPIAdapter.getListOfFriendRequestsToMe(myUser, new BatchUserFoundListener() {
            @Override
            public void onGotUserList(final User[] users) {
                FriendListFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (User user : users)
                            mFriendsAdapter.addUser(user, FriendsAdapter.TYPE_REQUEST);
                    }
                });
            }

            @Override
            public void onGotError() {

            }
        });


    }

    public void setUpRecylerViews() {
        mFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFriendsRecyclerView.setAdapter(mFriendsAdapter);


    }


    @Override
    public void onClick(View v) {
        clear();
    }

    public void clear() {
        mAutoCompleteTextView.setText("");
        while (!mFriendsAdapter.mSearched.isEmpty()) {
            User user = mFriendsAdapter.mSearched.get(0);
            mFriendsAdapter.removeUser(user, FriendsAdapter.TYPE_SEARCHED);

        }
        clearButton.setVisibility(View.GONE);
        hideKeyboard();
        mainLayout.requestFocus();

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {


    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    @Override
    public void onKeyboardDismiss() {
        clear();
    }

    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE
                ) {
            submitSearch();
            handled = true;
        }
        return handled;
    }


    public void submitSearch() {
        hideKeyboard();

        User myUser = ((MainActivity) getActivity()).getMyUser();

        if (myUser == null)
            return;

        AftabeAPIAdapter.searchForUser(myUser, mAutoCompleteTextView.getText().toString(), this);

        Log.d("TAG", "TODO submit search");

    }


    public void getContacts() {

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && !Prefs.getBoolean(MainActivity.CONTACTS_PERMISSION, false)) {
            return;
            // failed to get
        }

        User myUser = ((MainActivity) getActivity()).getMyUser();
        if (myUser == null) {
            Log.d("TAG", "user is null");
            return;

        }

        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {

            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumber = phoneNumber.replace(" ", "");

            phoneNumber = phoneNumber.replace("+", "00");
            Log.d("TAG", "phone number " + phoneNumber + " " + Tools.isAPhoneNumber(phoneNumber));


            AftabeAPIAdapter.searchForUser(myUser, phoneNumber, new UserFoundListener() {
                @Override
                public void onGetUser(User user) {
                    mFriendsAdapter.addUser(user, FriendsAdapter.TYPE_CONTACT);
                }

                @Override
                public void onGetError() {

                }

                @Override
                public void onGetMyUser(User myUser) {

                }
            });

        }
        phones.close();
    }

    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
    }

    @Override
    public void onGetUser(User user) {

        mFriendsAdapter.addUser(user, FriendsAdapter.TYPE_SEARCHED);

    }

    @Override
    public void onGetError() {
        Toast.makeText(getContext(), "user not found", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetMyUser(User myUser) {
        Log.d("TAG", "on get my user friendlist");
        setUpAdapters();


    }
}
