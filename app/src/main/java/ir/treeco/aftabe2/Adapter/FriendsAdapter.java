package ir.treeco.aftabe2.Adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;

import ir.treeco.aftabe2.Util.Logger;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.IllegalFieldValueException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ir.treeco.aftabe2.API.Rest.AftabeAPIAdapter;
import ir.treeco.aftabe2.API.Rest.Interfaces.OnFriendRequest;
import ir.treeco.aftabe2.API.Socket.SocketAdapter;
import ir.treeco.aftabe2.Adapter.Cache.FriendRequestState;
import ir.treeco.aftabe2.Adapter.Cache.FriendsHolder;
import ir.treeco.aftabe2.Object.User;
import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Util.FontsHolder;
import ir.treeco.aftabe2.Util.ImageManager;
import ir.treeco.aftabe2.Util.SizeManager;
import ir.treeco.aftabe2.Util.Tools;
import ir.treeco.aftabe2.Util.UiUtil;
import ir.treeco.aftabe2.View.Activity.MainActivity;
import ir.treeco.aftabe2.View.Custom.ToastMaker;
import ir.treeco.aftabe2.View.Custom.UserLevelView;
import ir.treeco.aftabe2.View.Dialog.DialogAdapter;
import ir.treeco.aftabe2.View.Dialog.LoadingForMatchRequestResult;
import ir.treeco.aftabe2.View.Dialog.RegistrationDialog;

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
    private final String[] HEADERS = {"یافت شده ها", "درخواست ها", "دوستان آنلاین", "دوستان", "مخاطبان"};

    public static final int TYPE_FRIEND = 3;
    public static final int TYPE_SEARCHED = 0;
    public static final int TYPE_CONTACT = 4;
    public static final int TYPE_REQUEST = 1;
    public static final int TYPE_ONLINE_FRIENDS = 2;
    public static final int TYPE_HEADER = 5;

    Context context;

    private CoinAdapter coinAdapter;

    public FriendsAdapter(Context context, ArrayList<User> friends, ArrayList<User> requests, ArrayList<User> contacts, ArrayList<User> searched) {

        this.context = context;
        imageManager = ImageManager.getInstance(context);
        mFriends = friends == null ? new ArrayList<User>() : friends;
        mRequests = requests == null ? new ArrayList<User>() : requests;
        mContacts = contacts == null ? (FriendsHolder.getInstance().getContacts()) : contacts;
        mSearched = searched == null ? new ArrayList<User>() : searched;
        mOnlineFriends = new ArrayList<>();


        Collections.sort(mFriends, new Comparator<User>() {
            @Override
            public int compare(User lhs, User rhs) {
                return Integer.valueOf(rhs.getScore()).compareTo(lhs.getScore());
            }
        });


        Collections.sort(mContacts, new Comparator<User>() {
            @Override
            public int compare(User lhs, User rhs) {
                return Integer.valueOf(rhs.getScore()).compareTo(lhs.getScore());
            }
        });

        arrayLists = new ArrayList<>();
        arrayLists.add(mSearched);
        arrayLists.add(mRequests);
        arrayLists.add(mOnlineFriends);
        arrayLists.add(mFriends);
        arrayLists.add(mContacts);

        coinAdapter = ((MainActivity) context).getCoinAdapter();
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

            mChatButton.setImageBitmap(imageManager.loadImageFromResource(R.drawable.notifmsg, size, size));

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
                return Integer.valueOf(rhs.getScore()).compareTo(lhs.getScore());
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

        Collections.sort(mList, new Comparator<User>() {
            @Override
            public int compare(User lhs, User rhs) {
                return Integer.valueOf(rhs.getScore()).compareTo(lhs.getScore());
            }
        });

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
        final int size = (int) (SizeManager.getScreenWidth() * 0.1);

        if (user.isFriend()) {
            if (holder.mChatButton.getVisibility() == View.GONE)
                holder.mChatButton.setVisibility(View.VISIBLE);
            holder.mChatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ToastMaker.show(context, context.getResources().getString(R.string.chat_not_available), Toast.LENGTH_SHORT);
//                    ChatFragment chatFragment = new ChatFragment();
//
//                    Logger.d("TAG", "click Temp real pos " + realPosition);
//                    FragmentTransaction transaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
//                    transaction.replace(R.id.fragment_container, chatFragment);
//                    transaction.addToBackStack(null);
//                    transaction.commit();
                }
            });

            holder.mMatchButton.setImageBitmap(imageManager.loadImageFromResource(R.drawable.challengebutton, size, size));

            holder.mMatchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogAdapter.makeMatchRequestDialog(context, user, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (!coinAdapter.spendCoinDiffless(100)) {
                                return;
                            }

                            SocketAdapter.requestToAFriend(user.getId());
                            new LoadingForMatchRequestResult(context, user).show();
                        }
                    });

                }
            });

        } else if (type == TYPE_SEARCHED || type == TYPE_CONTACT) {

            holder.mChatButton.setVisibility(View.GONE);

            holder.mMatchButton.setVisibility(View.VISIBLE);
            final int friendReqDrawable = (FriendRequestState.getInstance().requestShallPASS(user)) ? R.drawable.addfriends : R.drawable.notifreq;

            holder.mMatchButton.setImageBitmap(imageManager.loadImageFromResource(friendReqDrawable, size, size , ImageManager.ScalingLogic.FIT));
            if (FriendRequestState.getInstance().requestShallPASS(user))
                holder.mMatchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!FriendRequestState.getInstance().requestShallPASS(user)) {
                            return;
                        }

                        final User myUser = Tools.getCachedUser(context);

                        if (myUser == null || myUser.isGuest()) {

                            new RegistrationDialog(context, false).setTextVisible().show();
                            return;
                        }
                        DialogAdapter.makeFriendRequestDialog(context, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


//                                removeUser(user, type);

                                AftabeAPIAdapter.requestFriend(myUser, user.getId(), new OnFriendRequest() {
                                    @Override
                                    public void onFriendRequestSent() {
                                        holder.mMatchButton.setImageBitmap(imageManager.loadImageFromResource(R.drawable.notifreq, size, size));

                                    }

                                    @Override
                                    public void onFriendRequestFailedToSend() {

                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {

                                                ToastMaker.show(context, context.getResources().getString(R.string.try_later), Toast.LENGTH_SHORT);

                                            }
                                        });

                                    }
                                });
                            }
                        });
                    }
                });
        } else if (type == TYPE_REQUEST) {

            Logger.d(TAG, "binded to type request");
            holder.mChatButton.setVisibility(View.VISIBLE);
            holder.mMatchButton.setVisibility(View.VISIBLE);

            holder.mChatButton.setImageBitmap(imageManager.loadImageFromResource(R.drawable.no, size, size));
            holder.mMatchButton.setImageBitmap(imageManager.loadImageFromResource(R.drawable.yes, size, size));

            holder.mChatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final User myUser = Tools.getCachedUser(context);

                    if (myUser == null || myUser.isGuest()) {

                        new RegistrationDialog(v.getContext(), false).show();
                        return;
                    }

                    removeUser(user, type);
                    SocketAdapter.answerFriendRequest(user.getId(), false);
                }
            });

            holder.mMatchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final User myUser = Tools.getCachedUser(context);

                    if (myUser == null || myUser.isGuest()) {

                        new RegistrationDialog(v.getContext(), false).show();
                        return;
                    }


                    Logger.d(TAG, "click on yes");
                    removeUser(user, type);
//                    user.setIsFriend(true);
//                    addUser(user, TYPE_FRIEND);
//                    SocketAdapter.answerFriendRequest(user.getId(), true);
                    AftabeAPIAdapter.requestFriend(Tools.getCachedUser(context), user.getId(), new OnFriendRequest() {
                        @Override
                        public void onFriendRequestSent() {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    user.setIsFriend(true);
                                    addUser(user, TYPE_FRIEND);
                                }
                            });
                        }

                        @Override
                        public void onFriendRequestFailedToSend() {

                            ToastMaker.show(context, context.getResources().getString(R.string.connection_to_internet_sure), Toast.LENGTH_SHORT);
                            addUser(user, type);
                        }
                    });
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

    public ArrayList<User> getList(int type){
        return arrayLists.get(type);
    }

    public void clearAll(){
        for(ArrayList<User> users : arrayLists)
            users.clear();
        notifyDataSetChanged();

    }

}