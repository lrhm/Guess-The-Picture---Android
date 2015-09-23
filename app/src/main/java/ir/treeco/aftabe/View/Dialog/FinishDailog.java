package ir.treeco.aftabe.View.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Interface.FinishLevel;
import ir.treeco.aftabe.Object.Level;
import ir.treeco.aftabe.Util.FontsHolder;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Custom.DialogDrawable;
import ir.treeco.aftabe.R;

public class FinishDailog extends Dialog implements View.OnClickListener {
    private Context context;
    private Level level;
    private int packageSize;
    private FinishLevel finishLevel;

    public FinishDailog(Context context, Level level, int packageSize, FinishLevel finishLevel) {
        super(context);
        this.context = context;
        this.level = level;
        this.packageSize = packageSize;
        this.finishLevel = finishLevel;
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(true);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.level_finished);
        View container = findViewById(R.id.container);

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) container.getLayoutParams();
        layoutParams.leftMargin = layoutParams.rightMargin = MainApplication.lengthManager.getStoreDialogMargin();

        View contents = findViewById(R.id.contents);

        DialogDrawable drawable = new DialogDrawable();
        drawable.setTopPadding(MainApplication.lengthManager.getLevelFinishedDialogTopPadding());
        Tools tools = new Tools();
        tools.setViewBackground(contents, drawable);

        int padding = MainApplication.lengthManager.getLevelFinishedDialogPadding();
        contents.setPadding(padding, 0, padding, padding);

        TextView prize = (TextView) findViewById(R.id.prize);
        if (!level.isResolved()) {
            String prizeString = "+۳۰";
            customizeTextView(prize, prizeString, MainApplication.lengthManager.getLevelAuthorTextSize());

            tools.resizeView(prize, MainApplication.lengthManager.getPrizeBoxSize(), MainApplication.lengthManager.getPrizeBoxSize());
            tools.setViewBackground(prize, new BitmapDrawable(context.getResources(), MainApplication.imageManager.loadImageFromResource(R.drawable.coin, MainApplication.lengthManager.getPrizeBoxSize(), MainApplication.lengthManager.getPrizeBoxSize())));

            ObjectAnimator.ofFloat(prize, "rotation", 0, 315).setDuration(0).start();
        } else {
            prize.setVisibility(View.GONE);
        }

        ImageView tickView = (ImageView) findViewById(R.id.tickView);
        ((ViewGroup.MarginLayoutParams) tickView.getLayoutParams()).rightMargin = (int) (0.125 * MainApplication.lengthManager.getTickViewSize());
        tickView.setImageBitmap(MainApplication.imageManager.loadImageFromResource(R.drawable.correct, MainApplication.lengthManager.getTickViewSize(), MainApplication.lengthManager.getTickViewSize()));
        tools.resizeView(tickView, MainApplication.lengthManager.getTickViewSize(), MainApplication.lengthManager.getTickViewSize());

        ImageView nextButton = (ImageView) findViewById(R.id.next_level_button);
        ImageView homeButton = (ImageView) findViewById(R.id.home_button);


        if (level.getId() + 1 < packageSize) {
            nextButton.setImageBitmap(MainApplication.imageManager.loadImageFromResource(R.drawable.next_button, MainApplication.lengthManager.getLevelFinishedButtonsSize(), MainApplication.lengthManager.getLevelFinishedButtonsSize()));
            tools.resizeView(nextButton, MainApplication.lengthManager.getLevelFinishedButtonsSize(), MainApplication.lengthManager.getLevelFinishedButtonsSize());
            nextButton.setOnClickListener(this);

        } else {
            nextButton.setVisibility(View.GONE);
            findViewById(R.id.separatorSpace).setVisibility(View.GONE);
        }

        homeButton.setImageBitmap(MainApplication.imageManager.loadImageFromResource(R.drawable.home_button, MainApplication.lengthManager.getLevelFinishedButtonsSize(), MainApplication.lengthManager.getLevelFinishedButtonsSize()));
        tools.resizeView(homeButton, MainApplication.lengthManager.getLevelFinishedButtonsSize(), MainApplication.lengthManager.getLevelFinishedButtonsSize());
        homeButton.setOnClickListener(this);

        TextView levelSolution = (TextView) findViewById(R.id.level_solution);
        customizeTextView(levelSolution, tools.decodeBase64(level.getJavab()), MainApplication.lengthManager.getLevelSolutionTextSize());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_button:
                finishLevel.Home();
                break;

            case R.id.next_level_button:
                finishLevel.NextLevel();
                break;
        }

        dismiss();
    }

    private void customizeTextView(TextView textView, String label, float textSize) {
        textView.setText(label);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, MainApplication.lengthManager.getStoreItemFontSize());
        textView.setTextColor(Color.WHITE);
        textView.setShadowLayer(1, 2, 2, Color.BLACK);
        textView.setTypeface(FontsHolder.getHoma(textView.getContext()));
    }
}