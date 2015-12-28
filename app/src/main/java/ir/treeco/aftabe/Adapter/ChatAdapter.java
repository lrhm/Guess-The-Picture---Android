package ir.treeco.aftabe.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ir.treeco.aftabe.Object.ChatObject;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.View.Custom.UserLevelMarkView;

/**
 * Created by al on 12/28/15.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {


    public static final int TYPE_ME = 0;
    public static final int TYPE_OTHER = 1;

    ArrayList<ChatObject> mList;

    public ChatAdapter(ArrayList<ChatObject> list) {
        mList = (list == null) ? new ArrayList<ChatObject>() : list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mChatContent;
        UserLevelMarkView mUserLevelMarkView;

        public ViewHolder(View v, final int type) {

            super(v);
            int chatID = 0;
            int avatarID = 0;
            switch (type) {

                case TYPE_ME :
                    chatID = R.id.text_chat_me;
                    avatarID = R.id.avatar_chat_me;

                    break;
                case TYPE_OTHER:
                    chatID = R.id.text_chat_other;
                    avatarID = R.id.avatar_chat_other;

                    break;
            }

            mChatContent = (TextView) itemView.findViewById(chatID);
            mUserLevelMarkView = (UserLevelMarkView) itemView.findViewById(avatarID);


        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutID = (viewType == TYPE_ME) ? R.layout.item_chat_me : R.layout.item_chat_other;
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutID, parent, false);


        return new ViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ChatObject chatObject = mList.get(position);
        holder.mChatContent.setText(chatObject.getChatContent());

    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
