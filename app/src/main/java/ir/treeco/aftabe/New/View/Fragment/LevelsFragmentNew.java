package ir.treeco.aftabe.New.View.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ir.treeco.aftabe.New.Adapter.DBAdapter;
import ir.treeco.aftabe.New.Adapter.LevelsAdapter;
import ir.treeco.aftabe.New.Object.Level;
import ir.treeco.aftabe.R;

public class LevelsFragmentNew extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_levels, container, false);

//        PackageFragmentNew fragment = (PackageFragmentNew) getParentFragment();
//        LevelsActivityNew fragment = (LevelsActivityNew)fm.findFragmentByTag("LevelsActivityNew1");

        int page = getArguments().getInt(PackageFragmentNew.LEVEL_PAGE);
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
            GameFragmentNew gameFragmentNew = (GameFragmentNew) getActivity().getSupportFragmentManager().findFragmentByTag("GameFragment");
            getActivity().getSupportFragmentManager().beginTransaction().remove(gameFragmentNew).commit();
        }
    }
}
