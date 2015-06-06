package ir.treeco.aftabe.New.View.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ir.treeco.aftabe.New.Adapter.KeyboardAdapter;
import ir.treeco.aftabe.R;

public class GameActivity extends Activity{
    private RecyclerView recyclerView;
    private KeyboardAdapter adapter;
    int levelId;
    ImageView imageView;
    int packageNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_game);

        Intent intent = getIntent();
        levelId = intent.getIntExtra("id", 0);
        packageNumber = intent.getIntExtra("packageNumber", 0);

        imageView = (ImageView) findViewById(R.id.image_game);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_game);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 7));

        adapter = new KeyboardAdapter();
        recyclerView.setAdapter(adapter);
        String a = "file://" + getFilesDir().getPath() + "/Downloaded/"
                + MainActivity.downlodedObject.getDownloaded().get(packageNumber).getId()
                + "_" + MainActivity.downlodedObject.getDownloaded().get(packageNumber).getLevels().get(levelId).getResources();

        Picasso.with(this).load(a).into(imageView);
    }
}
