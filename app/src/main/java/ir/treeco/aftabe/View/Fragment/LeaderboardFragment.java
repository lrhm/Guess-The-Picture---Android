package ir.treeco.aftabe.View.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ir.treeco.aftabe.Adapter.LeaderboardAdapter;
import ir.treeco.aftabe.Object.User;

/**
 * Created by al on 1/22/16.
 */
public class LeaderboardFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        User tmp = new User();
        tmp.setScore(32);
        tmp.setName("no name");

        ArrayList<User> list = new ArrayList<>();
        for (int i = 0 ; i < 200 ; i++) {
            list.add(tmp);
        }

        LeaderboardAdapter adapter = new LeaderboardAdapter(list);
        recyclerView.setAdapter(adapter);

        return recyclerView;
    }
}
