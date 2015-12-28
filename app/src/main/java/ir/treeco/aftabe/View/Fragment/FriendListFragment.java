package ir.treeco.aftabe.View.Fragment;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ir.treeco.aftabe.Adapter.FriendsAdapter;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.SizeConverter;
import ir.treeco.aftabe.Util.SizeManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendListFragment extends Fragment implements TextWatcher{


    ImageManager imageManager;
    RecyclerView mFriendsRecylerView;
    FriendsAdapter mFriendsAdapter;
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


        mFriendsRecylerView = (RecyclerView) view.findViewById(R.id.friends_recyler_view);


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

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) (SizeManager.getScreenWidth() * 0.5), (int) (SizeManager.getScreenHeight() * 0.1));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        textInputLayout.setLayoutParams(layoutParams);

        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.search_text_view);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, objct);


        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.addTextChangedListener(this);
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
            users.add(new User("asghar", 3));
        }
        mFriendsAdapter = new FriendsAdapter(users, null , null , null );


    }

    public void setUpRecylerViews() {
        mFriendsRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFriendsRecylerView.setAdapter(mFriendsAdapter);

    }






    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        mFriendsAdapter.addUser(new User("ta",23) , FriendsAdapter.TYPE_SEARCHED);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

}
