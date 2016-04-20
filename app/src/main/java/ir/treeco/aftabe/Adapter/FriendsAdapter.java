package ir.treeco.aftabe.Adapter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.IllegalFieldValueException;

import java.util.ArrayList;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.FontsHolder;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Custom.UserLevelView;
import ir.treeco.aftabe.View.Fragment.ChatFragment;
import ir.treeco.aftabe.View.Fragment.OnlineGameFragment;

/**
 * Created by al on 12/26/15.
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {


    public ArrayList<User> mFriends;
    public ArrayList<User> mRequests;
    public ArrayList<User> mContacts;
    public ArrayList<User> mSearched;

    ArrayList<ArrayList<User>> arrayLists;
    private final String[] HEADERS = {"یافت شده گان", "درخواست ها", "دوستان", "مخاطبان"};

    public static final int TYPE_FRIEND = 2;
    public static final int TYPE_SEARCHED = 0;
    public static final int TYPE_CONTACT = 3;
    public static final int TYPE_REQUEST = 1;

    public static final int TYPE_HEADER = 4;


    public FriendsAdapter(ArrayList<User> friends, ArrayList<User> requests, ArrayList<User> contacts, ArrayList<User> searched) {

        mFriends = friends == null ? new ArrayList<User>() : friends;
        mRequests = requests == null ? new ArrayList<User>() : requests;
        mContacts = contacts == null ? new ArrayList<User>() : contacts;
        mSearched = searched == null ? new ArrayList<User>() : searched;
        User tmp = new User();
        tmp.setScore(23);
        tmp.setName("ali");

        mContacts.add(tmp);
        arrayLists = new ArrayList<>();
        arrayLists.add(mSearched);
        arrayLists.add(mRequests);
        arrayLists.add(mFriends);
        arrayLists.add(mContacts);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mHeaderTextView;
        UserLevelView mUserLevelView;
        ImageView mMatchButton;
        ImageView mChatButton;

        public ViewHolder(View v, final int type) {

            super(v);


            switch (type) {
                case TYPE_HEADER:
                    mHeaderTextView = (TextView) itemView.findViewById(R.id.header_item);
                    FontsHolder.setFont(mHeaderTextView, FontsHolder.SANS_REGULAR);
                    return;
                case TYPE_CONTACT:
                    break;
                case TYPE_FRIEND:
                    break;

                case TYPE_REQUEST:
                    break;
                case TYPE_SEARCHED:

                    break;
            }

            mMatchButton = (ImageView) itemView.findViewById(R.id.match_button);
            mChatButton = (ImageView) itemView.findViewById(R.id.start_chat_button);
            int size = (int) (SizeManager.getScreenWidth() * 0.1);

            ImageManager imageManager = ((MainApplication) v.getContext().getApplicationContext()).getImageManager();

            mMatchButton.setImageBitmap(imageManager.loadImageFromResource(R.drawable.challengebutton, size, size));
            mChatButton.setImageBitmap(imageManager.loadImageFromResource(R.drawable.chatbutton, size, size));

            mUserLevelView = (UserLevelView) itemView.findViewById(R.id.friend_list_mark_view);

        }

    }

    public void addUser(User user, int type) {
        ArrayList<User> mList = arrayLists.get(type);
        mList.add(user);
        notifyDataSetChanged();

    }

    public void removeUser(User user, int type) {
        ArrayList<User> mList = arrayLists.get(type);
        int position = mList.indexOf(user);
        mList.remove(position);
        int size = 0;
        for (int i = 0; i < type; i++) {
            ArrayList<User> list = arrayLists.get(i);
            size += list.size() + (list.isEmpty() ? 0 : 1);
        }
        if (mList.isEmpty())
            notifyItemRangeRemoved(position + size, 2);
        else notifyItemRemoved(position + size + 1);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        int type = getItemViewType(position);
        final int realPosition = getRealPosition(position, type);
        if (type == TYPE_HEADER) {

            holder.mHeaderTextView.setText(HEADERS[realPosition]);
            return;
        }

        User user = getUser(type, realPosition);

        holder.mChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChatFragment chatFragment = new ChatFragment();

                Log.d("TAG", "click Temp real pos " + realPosition);
                FragmentTransaction transaction = ((MainActivity) v.getContext()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, chatFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        holder.mMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                int levelID = 0;
//                bundle.putInt("LevelId", levelID);
//                bundle.putInt("id", 0);
//
//                OnlineGameFragment gameFragment = new OnlineGameFragment();
//                gameFragment.setArguments(bundle);
//
//                FragmentTransaction transaction = ((MainActivity) v.getContext()).getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragment_container, gameFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
            }
        });

        holder.mUserLevelView.setUser(user);



    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER)
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false), viewType);

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(v, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        int[] sizes = new int[4];
        int size = 0;
        int tempSize = 0;
        for (int i = 0; i < 4; i++) {
            ArrayList<User> list = arrayLists.get(i);
            sizes[i] = list.size();
            tempSize = size;
            size += list.size() + (list.isEmpty() ? 0 : 1);
            if (list.isEmpty())
                continue;
            if (tempSize == position)
                return TYPE_HEADER;
            if (position < size) {
                return i;

            }
        }
        throw new IllegalFieldValueException("getItemViewType", position + "");


    }

    @Override
    public int getItemCount() {
        int size = 0;
        for (int i = 0; i < 4; i++) {
            ArrayList<User> list = arrayLists.get(i);
            size += list.size() + (list.isEmpty() ? 0 : 1);
        }
        return size;

    }

    private int getRealPosition(int position, int type) {
        if (type == TYPE_HEADER) {

            int size = 0;
            int i = 0;
            int tempSize = 0;
            for (i = 0; i < 4; i++) {
                ArrayList<User> list = arrayLists.get(i);
                tempSize = size;
                size += list.size() + (list.isEmpty() ? 0 : 1);
                if (list.isEmpty())
                    continue;
                if (tempSize == position)
                    return i;

            }
            throw new IllegalFieldValueException("position", position + " from type " + type);

        }
        int size = 0;
        for (int i = 0; i < 4; i++) {
            ArrayList<User> list = arrayLists.get(i);
            size += list.size() + (list.isEmpty() ? 0 : 1);
            if (position < size)
                return list.size() - size + position;


        }

        throw new IllegalFieldValueException("position", position + " from type " + type);

    }

    private User getUser(int type, int pos) {
        return arrayLists.get(type).get(pos);
    }

}