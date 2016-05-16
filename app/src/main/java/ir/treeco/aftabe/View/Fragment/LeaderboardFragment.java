package ir.treeco.aftabe.View.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import ir.treeco.aftabe.API.AftabeAPIAdapter;
import ir.treeco.aftabe.API.BatchUserFoundListener;
import ir.treeco.aftabe.Adapter.LeaderboardAdapter;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.Util.Tools;


/**
 * Created by al on 1/22/16.
 */
public class LeaderboardFragment extends Fragment implements BatchUserFoundListener {


    LeaderboardAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        mAdapter = new LeaderboardAdapter(null);
        recyclerView.setAdapter(mAdapter);

        AftabeAPIAdapter.getLoaderboard(Tools.getCachedUser(getActivity()), this);

        return recyclerView;
    }

    @Override
    public void onGotUserList(User[] users) {

        Log.d("TAG", "on got leaderboad size of " + users.length);
        for (int i = 0; i < users.length; i++) {
            mAdapter.addUser(users[i]);
        }
    }

    @Override
    public void onGotError() {


    }
}
