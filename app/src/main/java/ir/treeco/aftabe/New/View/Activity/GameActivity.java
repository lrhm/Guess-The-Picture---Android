package ir.treeco.aftabe.New.View.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Random;

import ir.treeco.aftabe.BackgroundDrawable;
import ir.treeco.aftabe.New.Adapter.KeyboardAdapter;
import ir.treeco.aftabe.New.Adapter.SolutionAdapter;
import ir.treeco.aftabe.New.View.Fragment.HeaderFragmentNew;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.utils.LengthManager;

public class GameActivity extends FragmentActivity {
    private RecyclerView recyclerView;
    private KeyboardAdapter adapter;
    int levelId;
    ImageView imageView;
    int packageNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_game);

        LengthManager.initialize(this);

        HeaderFragmentNew header = (HeaderFragmentNew) getSupportFragmentManager().findFragmentById(R.id.header);
        header.setUpHeader(R.drawable.cheat_button);

        // set background drawable
        setOriginalBackgroundColor();

        String solution = "سیبسیبسیبلسب";
        char[] characters = new char[solution.length()];
        char[] keyboardChars = new char[21];
        solution.getChars(0, solution.length(), characters, 0);

        SolutionAdapter solutionAdapter = new SolutionAdapter(characters);
        RecyclerView recyclerView_solution = (RecyclerView) findViewById(R.id.recycler_view_solution);
        GridLayoutManager gridLayoutManager_solution = new GridLayoutManager(this, 9);
        recyclerView_solution.setHasFixedSize(true);
        recyclerView_solution.setLayoutManager(gridLayoutManager_solution);
        recyclerView_solution.setAdapter(solutionAdapter);

        String[] strings = getResources().getStringArray(R.array.alphabet);

        for (int i = 0; i < characters.length; i++) {
            Log.d("aaaaaaa", String.valueOf(characters[i]));
        }


        Random random = new Random();
        for (int i = 0; i < 21; i++) {
            keyboardChars[i] = strings[random.nextInt(33)].charAt(0);
        }
        for (int i = 0; i < characters.length; i++) {
            if (characters[i] != ' ') {
                keyboardChars[random.nextInt(21)] = characters[i];
            }
        }

        for (int i = 0; i < 21; i++) {
            Log.d("salam3", String.valueOf(keyboardChars[i]));
        }

        Intent intent = getIntent();
        levelId = intent.getIntExtra("id", 0);
        packageNumber = intent.getIntExtra("packageNumber", 0);

        imageView = (ImageView) findViewById(R.id.image_game);
//        ImageView imageView_game_frame = (ImageView) findViewById(R.id.image_game_frame);
//        imageView_game_frame.setBackgroundResource(R.drawable.frame);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_keyboard);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 7);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new KeyboardAdapter(this, keyboardChars);
        recyclerView.setAdapter(adapter);

        Log.d("armin gridLayout ortion", String.valueOf(gridLayoutManager.getOrientation()));
        String a = "file://" + getFilesDir().getPath() + "/Downloaded/"
                + MainActivity.downlodedObject.getDownloaded().get(packageNumber).getId()
                + "_" + MainActivity.downlodedObject.getDownloaded().get(packageNumber).getLevels().get(levelId).getResources();

        Picasso.with(this).load(a).into(imageView);
    }
















    //region SetBackGroundDrawable
    private void setOriginalBackgroundColor() {
        ImageView background = (ImageView) findViewById(R.id.background);
        background.setImageDrawable(new BackgroundDrawable(this, new int[]{
                Color.parseColor("#29CDB8"),
                Color.parseColor("#1FB8AA"),
                Color.parseColor("#0A8A8C")
        }));
    }
    //endregion
}
