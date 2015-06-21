package ir.treeco.aftabe.New.View.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
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

        int breake1;
        int breake2;
        char[] characters1;
        char[] characters2 = new char[0];
        char[] characters3 = new char[0];

        if (solution.length() >= 9){
            breake1 = 10;
            for (int i = 0; i < 10  && i < solution.length(); i ++){
                if (solution.charAt(i) == ' '){
                    breake1 = i;
                }
            }

            characters1 = new char[breake1];
            solution.getChars(0, breake1, characters1, 0);
            for (int i = 0 ; i < characters1.length; i++) {
                Log.e("Asd", String.valueOf(characters1[i]));
            }
            //-------------------------------
            if (solution.length() - breake1 -1  > 9) {
                breake2 = 18;
                for (int i = breake1 + 1; i <= breake1 + 10 && i <= solution.length(); i ++){
                    if (solution.charAt(i) == ' '){
                        breake2 = i;
                    }
                }

                characters2 = new char[breake2 - breake1 - 1];
                solution.getChars(breake1 + 1, breake2, characters2, 0);
                for (int i = 0 ; i < characters2.length; i++) {
                    Log.e("Asd2", String.valueOf(characters2[i]));
                }



                characters3 = new char[solution.length()-1 - breake2];
                solution.getChars(breake2 + 1, solution.length(), characters3, 0);
                for (int i = 0 ; i < characters3.length; i++) {
                    Log.e("Asd3", String.valueOf(characters3[i]));
                }

            } else {
                breake2 = solution.length() -1 ;
                characters2 = new char[breake2 - breake1];
                solution.getChars(breake1 + 1, breake2 + 1, characters2, 0);
                for (int i = 0 ; i < characters2.length; i++) {
                    Log.e("Asd2", String.valueOf(characters2[i]));
                }
            }

        } else {
            characters1 = new char[solution.length()];
            solution.getChars(0, solution.length(), characters1, 0);
            for (int i = 0 ; i < characters1.length; i++) {
                Log.e("Asd", String.valueOf(characters1[i]));
            }
        }

        char[] characters = new char[solution.length()];
        solution.getChars(0, solution.length(), characters, 0);




        SolutionAdapter solutionAdapter1 = new SolutionAdapter(characters1, this);
        RecyclerView recyclerView_solution1 = (RecyclerView) findViewById(R.id.recycler_view_solution1);
        GridLayoutManager gridLayoutManager_solution1 = new GridLayoutManager(this, characters1.length);
        recyclerView_solution1.setHasFixedSize(true);
        recyclerView_solution1.setLayoutManager(gridLayoutManager_solution1);
        recyclerView_solution1.setAdapter(solutionAdapter1);

        if (characters2.length > 0) {
            SolutionAdapter solutionAdapter2 = new SolutionAdapter(characters2, this);
            RecyclerView recyclerView_solution2 = (RecyclerView) findViewById(R.id.recycler_view_solution2);
            GridLayoutManager gridLayoutManager_solution2 = new GridLayoutManager(this, characters2.length);
            recyclerView_solution2.setHasFixedSize(true);
            recyclerView_solution2.setLayoutManager(gridLayoutManager_solution2);
            recyclerView_solution2.setAdapter(solutionAdapter2);
            recyclerView_solution2.setVisibility(View.VISIBLE);
        }

        if (characters3.length > 0) {
            SolutionAdapter solutionAdapter3 = new SolutionAdapter(characters3, this);
            RecyclerView recyclerView_solution3 = (RecyclerView) findViewById(R.id.recycler_view_solution3);
            GridLayoutManager gridLayoutManager_solution3 = new GridLayoutManager(this, characters3.length);
            recyclerView_solution3.setHasFixedSize(true);
            recyclerView_solution3.setLayoutManager(gridLayoutManager_solution3);
            recyclerView_solution3.setAdapter(solutionAdapter3);
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
            if (characters[i] != ' ') {
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
        MainApplication.saveDataAndBackUpData(this);
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
