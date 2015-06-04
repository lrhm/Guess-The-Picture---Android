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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import ir.treeco.aftabe.New.Adapter.PackageAdapter;
import ir.treeco.aftabe.New.Object.HeadObject;
import ir.treeco.aftabe.New.Object.PackageObject;
import ir.treeco.aftabe.New.View.Activity.MainActivity;
import ir.treeco.aftabe.R;

public class PackageFragmentNew extends Fragment {
    private RecyclerView recyclerView;
    private PackageAdapter adapter;
    int type;
    ArrayList<PackageObject> packageObjects;
    HeadObject downlodedObject;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_package, container, false);

        type = getArguments().getInt(MainActivity.FRAGMENT_TYPE);
        parseJson();

        recyclerView = (RecyclerView) view.findViewById(R.id.package_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        switch (type) {
            case 0:
                packageObjects =  ((MainActivity) this.getActivity()).getHeadObject().getNews();
                break;

            case 1:
                packageObjects =  downlodedObject.getDownloaded();
                break;

            case 2:
                packageObjects =  ((MainActivity) this.getActivity()).getHeadObject().getSaller();
                break;
        }

        adapter = new PackageAdapter(getActivity(), packageObjects, (MainActivity) getActivity());
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void parseJson() {
        try {
            String a = getActivity().getFilesDir().getPath() + "/downloaded.json";
            Log.e("path", a);
            InputStream inputStream = new FileInputStream(a);
            Reader reader = new InputStreamReader(inputStream, "UTF-8");
            Gson gson = new GsonBuilder().create();
            downlodedObject = gson.fromJson(reader, HeadObject.class);

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}