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
import android.widget.Button;
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

public class GameActivity extends FragmentActivity implements View.OnClickListener {
    int levelId;
    ImageView imageView;
    int packageNumber;
    private Tools tools;
    private String status;
    private char[] statusAdapter;
    private char[] keyboardChars;
    private char[] solutionAdapter;
    private SolutionAdapter solutionAdapter0;
    private SolutionAdapter solutionAdapter1;
    private SolutionAdapter solutionAdapter2;
    int break0;
    int break1;
    private int[] sAndkIndex;  //!! should use hashMap
    private KeyboardAdapter keyboardAdapter;
    private boolean[] keyboardStatus;
    Button hazf;
    Button azafe;
    private Random random;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_game);

//        LengthManager.initialize(this);
//        HeaderFragmentNew header = (HeaderFragmentNew) getSupportFragmentManager().findFragmentById(R.id.header);
//        header.setUpHeader(R.drawable.cheat_button);
//        setOriginalBackgroundColor();

        hazf = (Button) findViewById(R.id.hazf);
        azafe = (Button) findViewById(R.id.azafe);
        hazf.setOnClickListener(this);
        azafe.setOnClickListener(this);

        tools = new Tools();

        Intent intent = getIntent();
        levelId = intent.getIntExtra("id", 0);
        packageNumber = intent.getIntExtra("packageNumber", 0);

        String solution = tools.decodeBase64(MainApplication
                .downloadedObject.getDownloaded()
                .get(packageNumber).getLevels().get(levelId).getJavab());

        StringBuilder stringBuilder = new StringBuilder(solution);

        for (int i = 0; i < solution.length(); i++) {
            if (solution.charAt(i) != '.' && solution.charAt(i) != ' ') {
                stringBuilder.setCharAt(i, '-');
            }
        }
        status = String.valueOf(stringBuilder);

        Log.e("solotion", solution);

        solutionAdapter = solution.toCharArray();
        statusAdapter = status.toCharArray();
        sAndkIndex = new int[statusAdapter.length];

        if (solution.length() > 12) {
            break0 = getBreak(solutionAdapter, 0);

            if (solution.length() > 24) {
                break1 = getBreak(solutionAdapter, 1);
            } else {
                break1 = solution.length();
            }
        } else {
            break0 = solution.length();
            break1 = 0;
        }

        RecyclerView recyclerView_solution1 =
                (RecyclerView) findViewById(R.id.recycler_view_solution1);

        recyclerView_solution1.setHasFixedSize(true);

        recyclerView_solution1.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));

        solutionAdapter0 = new SolutionAdapter(solutionAdapter, this, statusAdapter, 0, break0, break1);
        recyclerView_solution1.setAdapter(solutionAdapter0);

        if (solutionAdapter.length > 12) {
            RecyclerView recyclerView_solution2 = (
                    RecyclerView) findViewById(R.id.recycler_view_solution2);

            recyclerView_solution2.setHasFixedSize(true);

            recyclerView_solution2.setLayoutManager(
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));

            solutionAdapter1 = new SolutionAdapter(solutionAdapter, this, statusAdapter, 1, break0, break1);
            recyclerView_solution2.setAdapter (solutionAdapter1);

            recyclerView_solution2.setVisibility(View.VISIBLE);
        }

        if (solutionAdapter.length > 24) {
            RecyclerView recyclerView_solution3 = (
                    RecyclerView) findViewById(R.id.recycler_view_solution3);

            recyclerView_solution3.setHasFixedSize(true);

            recyclerView_solution3.setLayoutManager(
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));

            solutionAdapter2 = new SolutionAdapter(solutionAdapter, this, statusAdapter, 2, break0, break1);
            recyclerView_solution3.setAdapter (solutionAdapter2);

            recyclerView_solution3.setVisibility(View.VISIBLE);
        }

        char[] alphabet = {
                'ی', 'ه','و' , 'ن', 'م', 'ل', 'گ', 'ک', 'ق', 'ف', 'غ', 'ع', 'ظ', 'ط', 'ض', 'ص', 'ش',
                'س','ژ' , 'ز', 'ر', 'ذ','د' , 'خ', 'ح', 'چ', 'ج', 'ث', 'ت', 'پ', 'ب', 'ا', 'آ' };

        keyboardChars = new char[21];
        keyboardStatus = new boolean[21];

        ArrayList<Integer> list = new ArrayList<>();
        random = new Random();
        for (int i = 0; i < 21; i++) {
            keyboardChars[i] = alphabet[random.nextInt(33)];
            list.add(i);
        }

        for (int i = 0; i < solutionAdapter.length; i++) { //fuck
            if (solutionAdapter[i] != ' ' && solutionAdapter[i] != '.') {
                int j = random.nextInt(list.size());
                keyboardChars[list.get(j)] = solutionAdapter[i];
                list.remove(j);
            }
        }

        RecyclerView recyclerView_keyboard = (
                RecyclerView) findViewById(R.id.recycler_view_keyboard);

        recyclerView_keyboard.setHasFixedSize(true);
        recyclerView_keyboard.setLayoutManager(new GridLayoutManager(this, 7));
        keyboardAdapter = new KeyboardAdapter(this, keyboardChars, keyboardStatus);
        recyclerView_keyboard.setAdapter(keyboardAdapter);

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

    public void selectKeyboard(int adapterPosition) {
        for (int i = 0; i < statusAdapter.length; i++) {
            if (statusAdapter[i] == '-') {
                statusAdapter[i] = keyboardChars[adapterPosition];
                sAndkIndex[i] = adapterPosition;
                keyboardStatus[adapterPosition] = true;

                keyboardAdapter.notifyDataSetChanged();

                if (i <= break0) {
                    solutionAdapter0.notifyDataSetChanged();
                } else if(i <= break1) {
                    solutionAdapter1.notifyDataSetChanged();
                } else {
                    solutionAdapter2.notifyDataSetChanged();
                }
                return;
            }
        }
    }

    public void removeFromSolution (int adapterPosition) {
        statusAdapter[adapterPosition] = '-';
        keyboardStatus[sAndkIndex[adapterPosition]] = false;
        keyboardAdapter.notifyDataSetChanged();

        if (adapterPosition <= break0) {
            solutionAdapter0.notifyDataSetChanged();
        } else if(adapterPosition <= break1) {
            solutionAdapter1.notifyDataSetChanged();
        } else {
            solutionAdapter2.notifyDataSetChanged();
        }
    }

    private int getBreak (char[] string, int n) {
        int number = n;
        for (int i = 0; i < string.length; i++) {
            if (string[i] == '.'){
                if (number == 0) {
                    return i;
                } else {
                    number--;
                }
            }
        }
        return n;
    }

    private void cheatHazf() {

    }

    private void cheatAzafe() { //is bad algoritm
        int rand;
        while (true) {
            rand = random.nextInt(statusAdapter.length);

            if (statusAdapter[rand] != '*' && statusAdapter[rand] != ' ' && statusAdapter[rand] != '.') {

                if (statusAdapter[rand] != '-') {
                    if (solutionAdapter[rand] != statusAdapter[rand]) {
                        removeFromSolution(rand);
                        break;
                    }
                } else {
                    break;
                }
            }
        }

        statusAdapter[rand] = '*';

        boolean key = false;

        for (int i = 0; i < keyboardChars.length; i++) {
            if (keyboardChars[i] == solutionAdapter[rand] && !keyboardStatus[i]) {
                keyboardStatus[i] = true;
                keyboardAdapter.notifyDataSetChanged();
                key = true;
                break;
            }
        }

        if (!key) {
            for (int i = 0; i < keyboardChars.length; i++) {
                if (keyboardChars[i] == solutionAdapter[rand]) {
                    for (int j = 0; j < sAndkIndex.length; j++) {
                        if (sAndkIndex[j] == i) {
                            removeFromSolution(j);
                            break;
                        }
                    }

                    keyboardStatus[i] = true;
                    keyboardAdapter.notifyDataSetChanged();
                    break;
                }

            }
        }

        if (rand <= break0) {
            solutionAdapter0.notifyDataSetChanged();
        } else if(rand <= break1) {
            solutionAdapter1.notifyDataSetChanged();
        } else {
            solutionAdapter2.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hazf:
                cheatHazf();
                break;

            case R.id.azafe:
                cheatAzafe();
                break;
        }
    }
}
