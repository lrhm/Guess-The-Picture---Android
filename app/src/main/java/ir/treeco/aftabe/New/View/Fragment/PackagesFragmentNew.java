package ir.treeco.aftabe.New.View.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.New.Adapter.DBAdapter;
import ir.treeco.aftabe.New.Adapter.PackageAdapter;
import ir.treeco.aftabe.New.Object.PackageObject;
import ir.treeco.aftabe.R;

public class PackagesFragmentNew extends Fragment {
    private RecyclerView recyclerView;
    private PackageAdapter adapter;
    int type;
    PackageObject[] packageObjects;
    private DBAdapter db;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_package, container, false);

        db = DBAdapter.getInstance(getActivity());
        type = getArguments().getInt(MainFragment.FRAGMENT_TYPE);

        recyclerView = (RecyclerView) view.findViewById(R.id.package_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        switch (type) {
            case 0:
                packageObjects = ((MainApplication) getActivity().getApplication()).getHeadObject().getNews();
                break;

            case 1:
                PackageObject[] downloadedPackage = db.getPackages();
                if (downloadedPackage != null) {
                    packageObjects = downloadedPackage;
                }
                break;

            case 2:
                packageObjects = ((MainApplication) getActivity().getApplication()).getHeadObject().getSaller();
                break;
        }

        adapter = new PackageAdapter(getActivity(), packageObjects);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}