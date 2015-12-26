package ir.treeco.aftabe.Adapter;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

import ir.treeco.aftabe.Object.Friend;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Custom.UserLevelMarkView;
import ir.treeco.aftabe.View.Fragment.PackageFragment;

/**
 * Created by al on 12/26/15.
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder>{

    Friend[] friends;

    public FriendsAdapter(Friend[] friends){
        this.friends = friends;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        UserLevelMarkView mUserLevelMarkView;

        public ViewHolder(View v) {
            super(v);
            mUserLevelMarkView = (UserLevelMarkView) itemView.findViewById(R.id.friend_list_mark_view);
        }

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

//        TODO bind this with actual compelte userlevelmark

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return friends.length;
    }
}



