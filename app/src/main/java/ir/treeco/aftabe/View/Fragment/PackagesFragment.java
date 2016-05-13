package ir.treeco.aftabe.View.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Adapter.DBAdapter;
import ir.treeco.aftabe.Adapter.PackageAdapter;
import ir.treeco.aftabe.Object.PackageObject;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.PackageTools;

public class PackagesFragment extends Fragment implements PackageTools.OnNewPackageFoundListener {
    private RecyclerView recyclerView;
    private PackageAdapter adapter;
    int type;
    private DBAdapter db;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_package, container, false);

        db = DBAdapter.getInstance(getActivity());
        type = getArguments().getInt(MainFragment.FRAGMENT_TYPE);

        recyclerView = (RecyclerView) view.findViewById(R.id.package_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        PackageObject[] downloadedPackage = db.getPackages();

        PackageTools.getInstance(getContext()).checkForNewPackage(this);


        adapter = new PackageAdapter(getActivity(), downloadedPackage);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNewPackage(final PackageObject packageObject) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                adapter.addPackage(packageObject);

            }
        });

    }
}