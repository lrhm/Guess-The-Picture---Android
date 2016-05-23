package ir.treeco.aftabe2.Adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ir.treeco.aftabe2.Object.User;
import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Util.FontsHolder;
import ir.treeco.aftabe2.Util.SizeManager;
import ir.treeco.aftabe2.Util.Tools;
import ir.treeco.aftabe2.Util.UiUtil;
import ir.treeco.aftabe2.View.Custom.UserLevelView;

/**
 * Created by al on 1/22/16.
 */
public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {


    private static final String TAG = "LeaderboardAdapter";
    ArrayList<User> mList;
    Float textNameSizePx;
    Float textLevelSizePx;

    /**
     * @param list list can be null , input list is null then a new empty list will be created
     */
    public LeaderboardAdapter(ArrayList<User> list) {
        mList = (list == null) ? new ArrayList<User>() : list;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        User user = mList.get(position);
        holder.mUserLevelView.setUser(user);
        holder.mTextView.setText("#" + Tools.numeralStringToPersianDigits(user.getRank() + ""));
        if (user.isMe()) {
            holder.mUserLevelView.setClick(false);
            holder.container.setBackgroundColor(Color.parseColor("#32000000"));
        } else {
            holder.container.setBackgroundColor(Color.TRANSPARENT);
            holder.mUserLevelView.setClick(true);

        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addUser(User user) {
        mList.add(user);
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        UserLevelView mUserLevelView;
        View container;
        View viewContainer;

        public ViewHolder(View v) {

            super(v);

            viewContainer = itemView.findViewById(R.id.item_leaderboard_view_container);
//            UiUtil.setTopMargin(viewContainer , (int) (SizeManager.getScreenHeight() * 0.005) +1);
//            UiUtil.setBottomMargin(viewContainer , (int) (SizeManager.getScreenHeight() * 0.005));


            container = itemView.findViewById(R.id.item_leaderboard_container);
            mTextView = (TextView) itemView.findViewById(R.id.rank_leaderboard_item);
            mTextView.setTypeface(FontsHolder.getNumeralSansMedium(v.getContext()));
            mUserLevelView = (UserLevelView) itemView.findViewById(R.id.leaderboard_mark_view);
            mUserLevelView.setUserNameTextSize(1.3f);
            mUserLevelView.getLevelTextView().setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextLevelSizePx(this));
            mUserLevelView.getUserNameTextView().setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextNameSizePx(this));


        }

    }

    public Float getTextNameSizePx(ViewHolder holder) {
        if (textNameSizePx != null)
            return textNameSizePx;
        textNameSizePx = UiUtil.getAdjustTextSize(holder.mUserLevelView.getUserNameTextView(),
                (int) (SizeManager.getScreenWidth() * 0.33), (int) (holder.mUserLevelView.getRealHeight() * 2 / 3.), 12, "LeaderBoardName");
        return textNameSizePx;
    }

    public Float getTextLevelSizePx(ViewHolder holder) {
        if (textLevelSizePx != null)
            return textLevelSizePx;
        textLevelSizePx = UiUtil.getAdjustTextSize(holder.mUserLevelView.getUserNameTextView(),
                (int) (holder.mUserLevelView.getRealWidth() * 0.4), (int) (holder.mUserLevelView.getRealHeight() * 2 / 3.), 2, "LeaderBoardLevel");
        return textLevelSizePx;
    }
}
