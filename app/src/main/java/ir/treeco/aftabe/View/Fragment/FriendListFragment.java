package ir.treeco.aftabe.View.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ir.treeco.aftabe.Adapter.FriendsAdapter;
import ir.treeco.aftabe.Object.Friend;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.SizeManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendListFragment extends Fragment  implements TextWatcher {


    public FriendListFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_friend_list, container, false);

        RecyclerView friendsRecyclerView = (RecyclerView) view.findViewById(R.id.friends_recyler_view);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Friend[] friends = new Friend[20];
        for(int i = 0 ; i < 20 ; i++) {
            friends[i] = new Friend("asghar" , 3);
        }
        friendsRecyclerView.setAdapter(new FriendsAdapter(friends));



        TextInputLayout textInputLayout = (TextInputLayout) view.findViewById(R.id.search_text_input_layout);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (SizeManager.getScreenWidth() * 0.8), (int) (SizeManager.getScreenHeight() * 0.1));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

        textInputLayout.setLayoutParams(layoutParams);

        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.search_text_view);

        autoCompleteTextView.addTextChangedListener(this);


        return view;
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

}
