package ir.treeco.aftabe.New.View.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import ir.treeco.aftabe.BackgroundDrawable;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.New.Adapter.KeyboardAdapter;
import ir.treeco.aftabe.New.Adapter.SolutionAdapter;
import ir.treeco.aftabe.New.Util.Tools;
import ir.treeco.aftabe.R;

public class GameActivity extends FragmentActivity {
    private RecyclerView recyclerView;
    private KeyboardAdapter adapter;
    int levelId;
    ImageView imageView;
    int packageNumber;
    private Tools tools;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_game);

//        LengthManager.initialize(this);
//        HeaderFragmentNew header = (HeaderFragmentNew) getSupportFragmentManager().findFragmentById(R.id.header);
//        header.setUpHeader(R.drawable.cheat_button);
//        setOriginalBackgroundColor();

        tools = new Tools();

        Intent intent = getIntent();
        levelId = intent.getIntExtra("id", 0);
        packageNumber = intent.getIntExtra("packageNumber", 0);

        String solution = tools.decodeBase64(MainApplication
                .downloadedObject.getDownloaded()
                .get(packageNumber).getLevels().get(levelId).getJavab());

        String status;
        StringBuilder stringBuilder = new StringBuilder(solution);

        for (int i = 0; i < solution.length(); i++) {
            System.out.println(stringBuilder);
            if (solution.charAt(i) != '.' && solution.charAt(i) != ' ') {
                stringBuilder.setCharAt(i, '-');
            }
        }
        status = String.valueOf(stringBuilder);

        String[] solutionAdapter;
        String[] statusAdapter;

        if (solution.length() <= 12) {
            solutionAdapter = new String[1];
            solutionAdapter[0] = solution;

            statusAdapter = new String[1];
            statusAdapter[0] = status;

        } else {
            solutionAdapter = solution.split("\\.");
            statusAdapter = status.split("\\.");
        }

        RecyclerView recyclerView_solution1 =
                (RecyclerView) findViewById(R.id.recycler_view_solution1);

        recyclerView_solution1.setHasFixedSize(true);

        recyclerView_solution1.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));

        recyclerView_solution1.setAdapter(
                new SolutionAdapter(solutionAdapter[0], this, statusAdapter[0]));

        if (solutionAdapter.length > 1 && solutionAdapter[1] != null
                && solutionAdapter[1].length() > 0) {

            RecyclerView recyclerView_solution2 = (
                    RecyclerView) findViewById(R.id.recycler_view_solution2);

            recyclerView_solution2.setHasFixedSize(true);

            recyclerView_solution2.setLayoutManager (
                    new LinearLayoutManager (this, LinearLayoutManager.HORIZONTAL, true));

            recyclerView_solution2.setAdapter (
                    new SolutionAdapter(solutionAdapter[1], this, statusAdapter[1]));

            recyclerView_solution2.setVisibility(View.VISIBLE);
        }

        if (solutionAdapter.length > 2 && solutionAdapter[2] != null
                && solutionAdapter[2].length() > 0) {

            RecyclerView recyclerView_solution3 = (
                    RecyclerView) findViewById(R.id.recycler_view_solution3);

            recyclerView_solution3.setHasFixedSize(true);

            recyclerView_solution3.setLayoutManager (
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));

            recyclerView_solution3.setAdapter (
                    new SolutionAdapter(solutionAdapter[2], this, statusAdapter[2]));

            recyclerView_solution3.setVisibility(View.VISIBLE);
        }

        char[] alphabet = {
                'ی', 'ه','و' , 'ن', 'م', 'ل', 'گ', 'ک', 'ق', 'ف', 'غ', 'ع', 'ظ', 'ط', 'ض', 'ص', 'ش',
                'س','ژ' , 'ز', 'ر', 'ذ','د' , 'خ', 'ح', 'چ', 'ج', 'ث', 'ت', 'پ', 'ب', 'ا', 'آ' };

        char[] keyboardChars = new char[21];

        ArrayList<Integer> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 21; i++) {
            keyboardChars[i] = alphabet[random.nextInt(33)];
            list.add(i);
        }

        for (int i = 0; i < solution.length() && i < 21; i++) {
            if (solution.charAt(i) != ' ' && solution.charAt(i) != '.') {
                int j = random.nextInt(list.size());
                keyboardChars[list.get(j)] = solution.charAt(i);
                list.remove(j);
            }
        }

        RecyclerView recyclerView_keyboard = (
                RecyclerView) findViewById(R.id.recycler_view_keyboard);

        recyclerView_keyboard.setHasFixedSize(true);
        recyclerView_keyboard.setLayoutManager(new GridLayoutManager(this, 7));
        recyclerView_keyboard.setAdapter(new KeyboardAdapter(this, keyboardChars));

        imageView = (ImageView) findViewById(R.id.image_game);
//        ImageView imageView_game_frame = (ImageView) findViewById(R.id.image_game_frame);
//        imageView_game_frame.setBackgroundResource(R.drawable.frame);

        String a = "file://" + getFilesDir().getPath() + "/Downloaded/"
                + MainApplication.downloadedObject.getDownloaded().get(packageNumber).getId()
                + "_" + MainApplication.downloadedObject.getDownloaded()
                .get(packageNumber).getLevels().get(levelId).getResources();

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
