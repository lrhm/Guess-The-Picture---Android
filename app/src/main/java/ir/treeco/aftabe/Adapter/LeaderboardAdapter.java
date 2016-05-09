package ir.treeco.aftabe.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.FontsHolder;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.Util.UiUtil;
import ir.treeco.aftabe.View.Custom.UserLevelView;

/**
 * Created by al on 1/22/16.
 */
public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {


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

        public ViewHolder(View v) {

            super(v);

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
                (int) (SizeManager.getScreenWidth() * 0.33), (int) (holder.mUserLevelView.getRealHeight() * 2 / 3.), 12);
        return textNameSizePx;
    }

    public Float getTextLevelSizePx(ViewHolder holder) {
        if (textLevelSizePx != null)
            return textLevelSizePx;
        textLevelSizePx = UiUtil.getAdjustTextSize(holder.mUserLevelView.getUserNameTextView(),
                (int) (holder.mUserLevelView.getRealWidth() * 0.4), (int) (holder.mUserLevelView.getRealHeight() * 2 / 3.), 2);
        return textLevelSizePx;
    }
}
