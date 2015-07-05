package ir.treeco.aftabe.New.View.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

import ir.treeco.aftabe.BackgroundDrawable;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.New.Adapter.KeyboardAdapter;
import ir.treeco.aftabe.New.Adapter.SolutionAdapter;
import ir.treeco.aftabe.R;

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

//        LengthManager.initialize(this);

//        HeaderFragmentNew header = (HeaderFragmentNew) getSupportFragmentManager().findFragmentById(R.id.header);
//        header.setUpHeader(R.drawable.cheat_button);

        // set background drawable
//        setOriginalBackgroundColor();


        Intent intent = getIntent();
        levelId = intent.getIntExtra("id", 0);
        packageNumber = intent.getIntExtra("packageNumber", 0);

        String solution1 = MainApplication.downloadedObject.getDownloaded().get(packageNumber).getLevels().get(levelId).getJavab();

        byte[] data = Base64.decode(solution1, Base64.DEFAULT);
        String solution = null;

        try {
            solution = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String status;
        StringBuilder myName = new StringBuilder(solution);

        for (int i = 0; i < solution.length(); i++) {

            System.out.println(myName);
            if (solution.charAt(i) != '.' && solution.charAt(i) != ' ') {
                myName.setCharAt(i, '-');
            }
        }
        status = String.valueOf(myName);

//        int breake1;
//        int breake2;
//        char[] characters1;
//        char[] characters2 = new char[0];
//        char[] characters3 = new char[0];

        Log.e("solotion" , solution);
        String[] characters0;
        String[] status0;// = new String[3];

//        if (solution != null) {
            if (solution.length() <= 12) {

                characters0 = new String[1];
                characters0[0] = solution;

                status0 = new String[1];
                status0[0] = status;
            } else {
                characters0 = solution.split("\\.");
                status0 = status.split("\\.");
            }
//        }

        char[] characters = new char[solution.length()];
        solution.getChars(0, solution.length(), characters, 0);

        RecyclerView recyclerView_solution1 = (RecyclerView) findViewById(R.id.recycler_view_solution1);
        recyclerView_solution1.setHasFixedSize(true);
        recyclerView_solution1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        recyclerView_solution1.setAdapter(new SolutionAdapter(characters0[0], this, status0[0]));

        if (characters0.length > 1 && characters0[1] != null && characters0[1].length() > 0) {
            RecyclerView recyclerView_solution2 = (RecyclerView) findViewById(R.id.recycler_view_solution2);
            recyclerView_solution2.setHasFixedSize(true);
            recyclerView_solution2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
            recyclerView_solution2.setAdapter(new SolutionAdapter(characters0[1], this, status0[1]));
            recyclerView_solution2.setVisibility(View.VISIBLE);
        }

        if (characters0.length > 2 && characters0[2] != null && characters0[2].length() > 0) {
            RecyclerView recyclerView_solution3 = (RecyclerView) findViewById(R.id.recycler_view_solution3);
            recyclerView_solution3.setHasFixedSize(true);
            recyclerView_solution3.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
            recyclerView_solution3.setAdapter(new SolutionAdapter(characters0[2], this, status0[2]));
            recyclerView_solution3.setVisibility(View.VISIBLE);
        }












        String[] strings = getResources().getStringArray(R.array.alphabet);
        char[] keyboardChars = new char[21];

        ArrayList<Integer> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 21; i++) {
            keyboardChars[i] = strings[random.nextInt(33)].charAt(0);
            list.add(i);
        }

        for (int i = 0; i < characters.length && i <21; i++) {
            if (characters[i] != ' ' && characters[i] != '.') {
                int j = random.nextInt(list.size());
                keyboardChars[list.get(j)] = characters[i];
                list.remove(j);
            }
        }


        imageView = (ImageView) findViewById(R.id.image_game);
//        ImageView imageView_game_frame = (ImageView) findViewById(R.id.image_game_frame);
//        imageView_game_frame.setBackgroundResource(R.drawable.frame);





        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_keyboard);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 7);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new KeyboardAdapter(this, keyboardChars);
        recyclerView.setAdapter(adapter);

        String a = "file://" + getFilesDir().getPath() + "/Downloaded/"
                + MainApplication.downloadedObject.getDownloaded().get(packageNumber).getId()
                + "_" + MainApplication.downloadedObject.getDownloaded().get(packageNumber).getLevels().get(levelId).getResources();

        Picasso.with(this).load(a).into(imageView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("armin onPause", this.getClass().toString() + " is on Pause and we save data");
//        MainApplication.saveDataAndBackUpData(this);
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
