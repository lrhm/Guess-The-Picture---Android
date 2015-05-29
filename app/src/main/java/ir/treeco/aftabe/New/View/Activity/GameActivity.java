package ir.treeco.aftabe.New.View.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import ir.treeco.aftabe.New.Adapter.KeyboardAdapter;
import ir.treeco.aftabe.R;

public class GameActivity extends Activity{
    private RecyclerView recyclerView;
    private KeyboardAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_game);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_game);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 7));

        adapter = new KeyboardAdapter();
        recyclerView.setAdapter(adapter);

    }
}
