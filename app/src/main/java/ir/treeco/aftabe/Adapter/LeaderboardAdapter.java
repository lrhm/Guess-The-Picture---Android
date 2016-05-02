package ir.treeco.aftabe.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.FontsHolder;
import ir.treeco.aftabe.View.Custom.UserLevelView;

/**
 * Created by al on 1/22/16.
 */
public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {


    ArrayList<User> mList;

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
        holder.mTextView.setText("#" + user.getLevel() +"");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addUser(User user){
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
        }

    }
}
