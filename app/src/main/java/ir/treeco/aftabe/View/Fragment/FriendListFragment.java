package ir.treeco.aftabe.View.Fragment;

import android.app.Activity;
import android.os.Bundle;
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

import java.util.ArrayList;

import ir.treeco.aftabe.Adapter.FriendsAdapter;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.FontsHolder;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.SizeConverter;
import ir.treeco.aftabe.Util.SizeManager;
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
        MyAutoCompleteTextView.OnKeyboardDismiss, TextView.OnEditorActionListener {


    ImageManager imageManager;
    RecyclerView mFriendsRecyclerView;
    FriendsAdapter mFriendsAdapter;
    MyAutoCompleteTextView mAutoCompleteTextView;
    View clearButton;
    View mainLayout;

    public FriendListFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        imageManager = ((MainApplication) getActivity().getApplication()).getImageManager();

        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);

        mainLayout = view.findViewById(R.id.friend_list_main_layout);
        mFriendsRecyclerView = (RecyclerView) view.findViewById(R.id.friends_recyler_view);


        clearButton = view.findViewById(R.id.clear_button);
        clearButton.setOnClickListener(this);
        clearButton.setVisibility(View.GONE);

        ((ImageView)clearButton).setImageBitmap(imageManager.loadImageFromResource(R.drawable.clear_button,
                (int) (SizeManager.getScreenWidth() * 0.15), (int) (SizeManager.getScreenWidth() * 0.15)));

        setUpAdapters();
        setUpRecylerViews();

        String[] objct = new String[6];
        objct[0] = "asghar";
        objct[1] = "ahmad";
        objct[5] = "ahmagh";
        objct[2] = "sahar";
        objct[3] = "golpar";
        objct[4] = "saghi";


//        TODO visibility of headtextview


        TextInputLayout textInputLayout = (TextInputLayout) view.findViewById(R.id.search_text_input_layout);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) (SizeManager.getScreenWidth() * 0.5), (int) (SizeManager.getScreenHeight() * 0.1));
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        textInputLayout.setLayoutParams(layoutParams);

        mAutoCompleteTextView = (MyAutoCompleteTextView) view.findViewById(R.id.search_text_view);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, objct);


        mAutoCompleteTextView.setAdapter(adapter);
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

        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            User tmp = new User();
            tmp.setScore(5);
            tmp.setName("asghar");
            users.add(tmp);
        }
        mFriendsAdapter = new FriendsAdapter(users, null, null, null);


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

        User tmp = new User();
        tmp.setScore(32);
        tmp.setName("no name");
        if (s.length() > 0 && clearButton.getVisibility() != View.VISIBLE)
            clearButton.setVisibility(View.VISIBLE);
        if (s.length() > 1)
            mFriendsAdapter.addUser(tmp, FriendsAdapter.TYPE_SEARCHED);
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
                ){
            submitSearch();
            handled = true;
        }
        return handled;
    }


    public void submitSearch() {
        hideKeyboard();
        Log.d("TAG", "TODO submit search");

    }
    public  void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity() .getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
    }
}
