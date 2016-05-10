package ir.treeco.aftabe.View.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;

import java.net.Socket;
import java.util.ArrayList;

import ir.treeco.aftabe.API.AftabeAPIAdapter;
import ir.treeco.aftabe.API.BatchUserFoundListener;
import ir.treeco.aftabe.API.Socket.Objects.Friends.MatchRequestHolder;
import ir.treeco.aftabe.API.Socket.Objects.Friends.MatchRequestSFHolder;
import ir.treeco.aftabe.API.Socket.Objects.Friends.MatchResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.Friends.OnlineFriendStatusHolder;
import ir.treeco.aftabe.API.Socket.SocketAdapter;
import ir.treeco.aftabe.API.Socket.SocketFriendMatchListener;
import ir.treeco.aftabe.API.UserFoundListener;
import ir.treeco.aftabe.Adapter.Cache.FriendsHolder;
import ir.treeco.aftabe.Adapter.DBAdapter;
import ir.treeco.aftabe.Adapter.FriendsAdapter;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.FontsHolder;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.SizeConverter;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.Util.UiUtil;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Custom.MyAutoCompleteTextView;
import ir.treeco.aftabe.View.Custom.ToastMaker;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendListFragment extends Fragment implements TextWatcher, View.OnClickListener,
        MyAutoCompleteTextView.OnKeyboardDismiss, TextView.OnEditorActionListener, UserFoundListener, SocketFriendMatchListener, View.OnFocusChangeListener {

    private static final String TAG = "FriendListFragmetn";
    ArrayAdapter<String> searchBarAdapter;
    ImageManager imageManager;
    RecyclerView mFriendsRecyclerView;
    FriendsAdapter mFriendsAdapter;
    MyAutoCompleteTextView mAutoCompleteTextView;
    View clearButton;
    View mainLayout;
    User[] friends;
    ProgressBar mProgressBar;

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

        SocketAdapter.addFriendSocketListener(this);

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

            @Override
            public void onForceLogout() {

                deleteCachedFriends();
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


        TextInputLayout textInputLayout = (TextInputLayout) view.findViewById(R.id.search_text_input_layout);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) (SizeManager.getScreenWidth() * 0.5), (int) (SizeManager.getScreenHeight() * 0.1));
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        textInputLayout.setLayoutParams(layoutParams);

        mAutoCompleteTextView = (MyAutoCompleteTextView) view.findViewById(R.id.search_text_view);
        mAutoCompleteTextView.setOnClickListener(this);


        searchBarAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line);


        mAutoCompleteTextView.setAdapter(searchBarAdapter);
        mAutoCompleteTextView.setOnKeyboardDismiss(this);
        mAutoCompleteTextView.addTextChangedListener(this);
        mAutoCompleteTextView.setOnEditorActionListener(this);
        mAutoCompleteTextView.setTypeface(FontsHolder.getSansMedium(getContext()));

        mAutoCompleteTextView.setHint("شماره تلفن یا نام کاربری      ");
        UiUtil.setTextViewSize(mAutoCompleteTextView, (int) (SizeManager.getScreenWidth() * 0.5), 0.095f);
        mAutoCompleteTextView.setOnFocusChangeListener(this);

//
        ImageView searchFriendImageView = (ImageView) view.findViewById(R.id.search_friend_image);
        SizeConverter searchFriendConverter = SizeConverter.SizeConvertorFromWidth(SizeManager.getScreenWidth() * 0.8f, 1373, 227);
        searchFriendImageView.setImageBitmap
                (imageManager.loadImageFromResource(R.drawable.searchbar, searchFriendConverter.mWidth
                        , searchFriendConverter.mHeight, ImageManager.ScalingLogic.FIT));


        mProgressBar = (ProgressBar) view.findViewById(R.id.search_friend_progress_bar);

        int size = (int) (searchFriendConverter.mHeight * 2 / 3.);
        UiUtil.setWidth(mProgressBar, size);
        UiUtil.setHeight(mProgressBar, size);
        UiUtil.setRightMargin(mProgressBar, (int) (SizeManager.getScreenWidth() * 0.02));


        return view;
    }


    public void setUpAdapters() {


        Log.d(TAG, "setting up adapter");

        final FriendsHolder friendsHolder = FriendsHolder.getInstance();
        if (mFriendsAdapter == null)
            mFriendsAdapter = new FriendsAdapter(getContext(), friendsHolder.getFriends(), null, null, null);


        if (getActivity() == null)
            return;
        if (mAdaptersSet)
            return;

        ((MainActivity) getActivity()).setFriendsAdapter(mFriendsAdapter);

        User myUser = ((MainActivity) getActivity()).getMyUser();


        if (myUser == null) {
            mAdaptersSet = false;
            return;

        }
        mAdaptersSet = true;

//        getContacts();

        Log.d(TAG, "will request friend list");

        AftabeAPIAdapter.getListOfMyFriends(myUser, new BatchUserFoundListener() {
            @Override
            public void onGotUserList(final User[] users) {


                Log.d(TAG, "get friend list");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        friends = users;
                        friendsHolder.updateFriendsFromAPI(users);
                        ArrayList<User> cachedUsers = friendsHolder.getFriends();
                        for (User user : mFriendsAdapter.getFriendList()) {
                            if (!cachedUsers.contains(user)) {
                                mFriendsAdapter.removeUser(user, FriendsAdapter.TYPE_FRIEND);
                            }
                        }
                        for (User user : users)
                            if (!mFriendsAdapter.getFriendList().contains(user))
                                mFriendsAdapter.addUser(user, FriendsAdapter.TYPE_FRIEND);


                    }
                });
            }

            @Override
            public void onGotError() {

                Log.d(TAG, "Dident get friend list");
            }
        });

        AftabeAPIAdapter.getListOfFriendRequestsToMe(myUser, new BatchUserFoundListener() {
            @Override
            public void onGotUserList(final User[] users) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
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

        if (v.getId() == R.id.clear_button) {
            clear();

        }
        if (v.getId() == R.id.search_text_view) {
            ((OnlineMenuFragment) getParentFragment()).verticalViewPager.setPagingEnabled(false);

        }
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

        while (!mFriendsAdapter.mSearched.isEmpty()) {
            User user = mFriendsAdapter.mSearched.get(0);
            mFriendsAdapter.removeUser(user, FriendsAdapter.TYPE_SEARCHED);

        }

        hideKeyboard();

        User myUser = ((MainActivity) getActivity()).getMyUser();

        User cachedUser = Tools.getCachedUser();

        if (cachedUser == null) {
            ToastMaker.show(getContext(), "برای جست و جو لطفا عضو شوید", Toast.LENGTH_SHORT);
            return;
        }

        if (myUser == null)
            return;

        AftabeAPIAdapter.searchForUser(myUser, mAutoCompleteTextView.getText().toString(), this);
        mProgressBar.setVisibility(View.VISIBLE);


    }

    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);

        ((OnlineMenuFragment) getParentFragment()).verticalViewPager.setPagingEnabled(true);

    }

    @Override
    public void onGetUser(User user) {

        Log.d(TAG, new Gson().toJson(user));
        mFriendsAdapter.addUser(user, FriendsAdapter.TYPE_SEARCHED);
        mProgressBar.setVisibility(View.GONE);

    }

    @Override
    public void onGetError() {


        mProgressBar.setVisibility(View.GONE);

        Toast.makeText(getActivity(), "user not found", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetMyUser(User myUser) {

        setUpAdapters();


    }

    @Override
    public void onForceLogout() {

    }


    @Override
    public void onMatchRequest(MatchRequestSFHolder request) {

    }

    @Override
    public void onOnlineFriendStatus(OnlineFriendStatusHolder status) {


        FriendsHolder friendsHolder = FriendsHolder.getInstance();
        ArrayList<User> friendList = friendsHolder.getFriends();
        if (status.isOnlineAndEmpty()) {
            for (User user : friendList) {
                if (status.getFriendId().equals(user.getId())) {
                    final User user1 = user;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mFriendsAdapter.addUser(user1, FriendsAdapter.TYPE_ONLINE_FRIENDS);

                        }
                    });
//                    Log.d(TAG, "added online !");
                    return;
                }
            }
        } else {
            User u = null;
            for (User user : friendList) {
                if (status.getFriendId().equals(user.getId())) {
                    u = user;
                    break;
                }
            }
            if (u == null) {
//                Log.d(TAG, " friend not found in friend list !");
                return;
            }
            final User finalU = u;
//            Log.d(TAG, "removing offline user");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mFriendsAdapter.removeUser(finalU, FriendsAdapter.TYPE_ONLINE_FRIENDS);


                }
            });
        }
    }

    @Override
    public void onMatchResultToSender(MatchResultHolder result) {

    }


    public void deleteCachedFriends() {

        FriendsHolder friendsHolder = FriendsHolder.getInstance();

        for (User user : mFriendsAdapter.getFriendList()) {
            friendsHolder.removeFriend(user);
        }

        mFriendsAdapter.removeAll();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        ((OnlineMenuFragment) getParentFragment()).verticalViewPager.setPagingEnabled(!hasFocus);

    }
}
