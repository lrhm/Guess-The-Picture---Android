package ir.treeco.aftabe.View.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ir.treeco.aftabe.Adapter.DBAdapter;
import ir.treeco.aftabe.Adapter.LevelsAdapter;
import ir.treeco.aftabe.Object.Level;
import ir.treeco.aftabe.R;

public class LevelsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_levels, container, false);

        int page = getArguments().getInt(PackageFragment.LEVEL_PAGE);
        int packageId = getArguments().getInt("id");

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.levels_recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));

        DBAdapter db = DBAdapter.getInstance(getActivity());
        Level[] levels = db.getLevels(packageId);

        LevelsAdapter adapter = new LevelsAdapter(getActivity(), packageId, page, levels);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity().getSupportFragmentManager().findFragmentByTag("GameFragment") != null) {
            GameFragment gameFragment = (GameFragment) getActivity().getSupportFragmentManager().findFragmentByTag("GameFragment");
            getActivity().getSupportFragmentManager().beginTransaction().remove(gameFragment).commit();
        }
    }
}
