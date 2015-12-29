package ir.treeco.aftabe.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ir.treeco.aftabe.Object.ChatObject;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.SizeManager;
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

                case TYPE_ME:
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
        int chatID = 0;
        int avatarID = 0;
        switch (viewType) {

            case TYPE_ME:
                chatID = R.id.text_chat_me;
                avatarID = R.id.avatar_chat_me;

                break;
            case TYPE_OTHER:
                chatID = R.id.text_chat_other;
                avatarID = R.id.avatar_chat_other;

                break;
        }
        View textView = v.findViewById(chatID);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) (SizeManager.getScreenWidth() * 0.7), ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        if (viewType == TYPE_OTHER)
            lp.rightMargin = (int) (SizeManager.getScreenWidth() * 0.07);
        else
            lp.leftMargin = (int) (SizeManager.getScreenWidth() * 0.07);
        textView.setLayoutParams(lp);

        return new ViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ChatObject chatObject = mList.get(position);
        holder.mChatContent.setText(chatObject.getChatContent());

    }

    public void addChatItem(ChatObject chatObject) {
        mList.add(0 , chatObject);
        notifyItemInserted(0);
        
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
