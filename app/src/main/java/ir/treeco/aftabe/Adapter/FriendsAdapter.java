package ir.treeco.aftabe.Adapter;

import android.content.Context;
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
import java.util.Collections;
import java.util.Comparator;

import ir.treeco.aftabe.API.AftabeAPIAdapter;
import ir.treeco.aftabe.API.OnFriendRequest;
import ir.treeco.aftabe.API.Socket.SocketAdapter;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.FontsHolder;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.Util.UiUtil;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Custom.UserLevelView;
import ir.treeco.aftabe.View.Dialog.DialogAdapter;
import ir.treeco.aftabe.View.Dialog.LoadingDialog;
import ir.treeco.aftabe.View.Dialog.LoadingForMatchRequestResult;
import ir.treeco.aftabe.View.Fragment.ChatFragment;
import ir.treeco.aftabe.View.Fragment.OnlineGameFragment;

/**
 * Created by al on 12/26/15.
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {


    private static final String TAG = "FriendsAdapter";

    ImageManager imageManager;

    public ArrayList<User> mFriends;
    public ArrayList<User> mOnlineFriends;
    public ArrayList<User> mRequests;
    public ArrayList<User> mContacts;
    public ArrayList<User> mSearched;

    ArrayList<ArrayList<User>> arrayLists;
    private final String[] HEADERS = {"یافت شده گان", "درخواست ها", "دوستان انلاین", "دوستان", "مخاطبان"};

    public static final int TYPE_FRIEND = 3;
    public static final int TYPE_SEARCHED = 0;
    public static final int TYPE_CONTACT = 4;
    public static final int TYPE_REQUEST = 1;
    public static final int TYPE_ONLINE_FRIENDS = 2;
    public static final int TYPE_HEADER = 5;


    public FriendsAdapter(Context context, ArrayList<User> friends, ArrayList<User> requests, ArrayList<User> contacts, ArrayList<User> searched) {

        imageManager = new ImageManager(context);
        mFriends = friends == null ? new ArrayList<User>() : friends;
        mRequests = requests == null ? new ArrayList<User>() : requests;
        mContacts = contacts == null ? new ArrayList<User>() : contacts;
        mSearched = searched == null ? new ArrayList<User>() : searched;
        mOnlineFriends = new ArrayList<>();

        arrayLists = new ArrayList<>();
        arrayLists.add(mSearched);
        arrayLists.add(mRequests);
        arrayLists.add(mOnlineFriends);
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
                    FontsHolder.setFont(mHeaderTextView, FontsHolder.SANS_BOLD);
                    UiUtil.setTextViewSize(mHeaderTextView, SizeManager.getScreenWidth(), 0.25f * 0.23f);
                    UiUtil.setTopMargin(mHeaderTextView, (int) (SizeManager.getScreenHeight() * 0.05));
                    UiUtil.setBottomMargin(mHeaderTextView, (int) (SizeManager.getScreenHeight() * 0.05));

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

            mMatchButton.setImageBitmap(imageManager.loadImageFromResource(R.drawable.challengebutton, size, size));
            mChatButton.setImageBitmap(imageManager.loadImageFromResource(R.drawable.chatbutton, size, size));

            mUserLevelView = (UserLevelView) itemView.findViewById(R.id.friend_list_mark_view);

        }

    }

    public void addUser(User user, final int type) {
        ArrayList<User> mList = arrayLists.get(type);

        for (User u : mList)
            if (u.getId().equals(user.getId()))
                return;

        mList.add(user);
        Collections.sort(mList, new Comparator<User>() {
            @Override
            public int compare(User lhs, User rhs) {
                return Integer.valueOf(lhs.getScore()).compareTo(rhs.getScore());
            }
        });
        notifyDataSetChanged();

    }

    public void removeUser(User user, int type) {
        ArrayList<User> mList = arrayLists.get(type);
        int position = mList.indexOf(user);
        if (position == -1)
            return;
        mList.remove(position);

        int size = 0;
        for (int i = 0; i < type; i++) {
            ArrayList<User> list = arrayLists.get(i);
            size += list.size() + (list.isEmpty() ? 0 : 1);
        }

        notifyDataSetChanged();
//        if (mList.isEmpty())
//            notifyItemRangeRemoved(position + size, 2);
//        else notifyItemRemoved(position + size + 1);

    }

    public void removeAll() {
        for (ArrayList<User> list : arrayLists)
            list.clear();
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final int type = getItemViewType(position);
        final int realPosition = getRealPosition(position, type);
        if (type == TYPE_HEADER) {

            holder.mHeaderTextView.setText(String.format("%s (%s)", HEADERS[realPosition], Tools.numeralStringToPersianDigits(arrayLists.get(realPosition).size() + "")));
            return;
        }

        final User user = getUser(type, realPosition);
        int size = (int) (SizeManager.getScreenWidth() * 0.1);

        if (user.isFriend()) {
            if (holder.mChatButton.getVisibility() == View.GONE)
                holder.mChatButton.setVisibility(View.VISIBLE);
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

            holder.mMatchButton.setImageBitmap(imageManager.loadImageFromResource(R.drawable.challengebutton, size, size));

            holder.mMatchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogAdapter.makeMatchRequestDialog(v.getContext(), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            SocketAdapter.requestToAFriend(user.getId());
                            new LoadingForMatchRequestResult(v.getContext(), user).show();
                        }
                    });

                }
            });

        } else if (type == TYPE_SEARCHED) {

            holder.mChatButton.setVisibility(View.GONE);

            holder.mMatchButton.setVisibility(View.VISIBLE);
            holder.mMatchButton.setImageBitmap(imageManager.loadImageFromResource(R.drawable.addfriends, size, size));
            holder.mMatchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogAdapter.makeFriendRequestDialog(v.getContext(), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            AftabeAPIAdapter.requestFriend(Tools.getCachedUser(), user.getId(), new OnFriendRequest() {
                                @Override
                                public void onFriendRequestSent() {

                                    removeUser(user, type);

                                }

                                @Override
                                public void onFriendRequestFailedToSend() {

                                }
                            });
                        }
                    });
                }
            });
        } else if (type == TYPE_REQUEST) {

            Log.d(TAG, "binded to type request");
            holder.mChatButton.setVisibility(View.VISIBLE);
            holder.mMatchButton.setVisibility(View.VISIBLE);

            holder.mChatButton.setImageBitmap(imageManager.loadImageFromResource(R.drawable.no, size, size));
            holder.mMatchButton.setImageBitmap(imageManager.loadImageFromResource(R.drawable.yes, size, size));

            holder.mChatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "click on no");
                    removeUser(user, type);
                    SocketAdapter.answerFriendRequest(user.getId(), false);
                }
            });

            holder.mMatchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d(TAG, "click on yes");
                    removeUser(user, type);
//                    user.setIsFriend(true);
//                    addUser(user, TYPE_FRIEND);
//                    SocketAdapter.answerFriendRequest(user.getId(), true);
                    AftabeAPIAdapter.requestFriend(Tools.getCachedUser(), user.getId(), null);
                }
            });
        }

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
        int[] sizes = new int[arrayLists.size()];
        int size = 0;
        int tempSize = 0;
        for (int i = 0; i < arrayLists.size(); i++) {
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
        for (int i = 0; i < arrayLists.size(); i++) {
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
            for (i = 0; i < arrayLists.size(); i++) {
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
        for (int i = 0; i < arrayLists.size(); i++) {
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

    public ArrayList<User> getFriendList() {
        ArrayList<User> res = new ArrayList<>();
        res.addAll(mFriends);
        res.addAll(mOnlineFriends);
        return res;
    }

}