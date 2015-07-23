package ir.treeco.aftabe.New.View.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ir.treeco.aftabe.New.Adapter.LevelsAdapter;
import ir.treeco.aftabe.R;

public class LevelsFragmentNew extends Fragment {
    private RecyclerView recyclerView;
    private LevelsAdapter adapter;
    private int page;
    private int packageNumber;
    private Bitmap levelLocked;
    private Bitmap levelUnlocked;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_levels, container, false);

        PackageFragmentNew fragment = (PackageFragmentNew) getParentFragment();
//        LevelsActivityNew fragment = (LevelsActivityNew)fm.findFragmentByTag("LevelsActivityNew1");

        levelLocked = fragment.getLevelLocked();
        levelUnlocked = fragment.getLevelUnlocked();

        page = getArguments().getInt(PackageFragmentNew.LEVEL_PAGE);
        packageNumber = getArguments().getInt(PackageFragmentNew.PACKAGE_NUMBER);

        recyclerView = (RecyclerView) view.findViewById(R.id.levels_recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));

        adapter = new LevelsAdapter(getActivity(), packageNumber, page, levelLocked, levelUnlocked);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
