package ir.treeco.aftabe.New.View.Dialog;

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
import ir.treeco.aftabe.New.Util.FontsHolder;
import ir.treeco.aftabe.New.Util.ImageManager;
import ir.treeco.aftabe.New.Util.Tools;
import ir.treeco.aftabe.New.View.Custom.DialogDrawable;
import ir.treeco.aftabe.R;

/**
 * Created by behdad on 8/12/15.
 */
public class FinishDailog extends Dialog implements View.OnClickListener {
    private Context context;

    public FinishDailog(Context context) {
        super(context);
        this.context = context;
    }

//    public FinishDailog(Context context, LoginCallback loginCallback) {
//        super (context);
//        this.context = context;
//        this.loginCallback = loginCallback;
//    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(true);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.new_view_level_finished);

            View container = findViewById(R.id.container);

            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) container.getLayoutParams();
            layoutParams.leftMargin = layoutParams.rightMargin = MainApplication.lengthManager.getStoreDialogMargin();



            View contents = findViewById(R.id.contents);

            DialogDrawable drawable = new DialogDrawable(context);
            drawable.setTopPadding(MainApplication.lengthManager.getLevelFinishedDialogTopPadding());
        Tools tools = new Tools();
            tools.setViewBackground(contents, drawable);

            int padding = MainApplication.lengthManager.getLevelFinishedDialogPadding();
            contents.setPadding(padding, 0, padding, padding);

            TextView prize = (TextView) findViewById(R.id.prize);
//            if (mLevel.getCurrentPrize() > 0) {
                String prizeString = "+۳۰";
                customizeTextView(prize, prizeString, MainApplication.lengthManager.getLevelAuthorTextSize());

                tools.resizeView(prize, MainApplication.lengthManager.getPrizeBoxSize(), MainApplication.lengthManager.getPrizeBoxSize());
                tools.setViewBackground(prize, new BitmapDrawable(context.getResources(), ImageManager.loadImageFromResource(context, R.drawable.coin, MainApplication.lengthManager.getPrizeBoxSize(), MainApplication.lengthManager.getPrizeBoxSize())));

                ObjectAnimator.ofFloat(prize, "rotation", 0, 315).setDuration(0).start();
//            } else {
//                prize.setVisibility(View.GONE);
//            }

            ImageView tickView = (ImageView) findViewById(R.id.tickView);
            ((ViewGroup.MarginLayoutParams) tickView.getLayoutParams()).rightMargin = (int) (0.125 * MainApplication.lengthManager.getTickViewSize());
            tickView.setImageBitmap(ImageManager.loadImageFromResource(context, R.drawable.correct, MainApplication.lengthManager.getTickViewSize(), MainApplication.lengthManager.getTickViewSize()));
            tools.resizeView(tickView, MainApplication.lengthManager.getTickViewSize(), MainApplication.lengthManager.getTickViewSize());

            ImageView nextButton = (ImageView) findViewById(R.id.next_level_button);
            ImageView homeButton = (ImageView) findViewById(R.id.home_button);


//            if (mLevel.getId() + 1 < mLevel.getWrapperPackage().getNumberOfLevels()) {
                nextButton.setImageBitmap(ImageManager.loadImageFromResource(context, R.drawable.next_button, MainApplication.lengthManager.getLevelFinishedButtonsSize(), MainApplication.lengthManager.getLevelFinishedButtonsSize()));
                tools.resizeView(nextButton, MainApplication.lengthManager.getLevelFinishedButtonsSize(), MainApplication.lengthManager.getLevelFinishedButtonsSize());

//                nextButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        LoadingManager.startTask(new TaskStartedListener() {
//                            @Override
//                            public void taskStarted() {
//                                ((IntroActivity) getActivity()).popFromViewStack(greetingsView);
//
//                                Level level = mLevel.getWrapperPackage().getLevel(mLevel.getId() + 1);
//                                LevelFragment newFragment = LevelFragment.newInstance(level);
//                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                                fragmentManager.popBackStack();
//
//                                FragmentTransaction transaction = fragmentManager.beginTransaction();
//                                transaction.remove(LevelFragment.this);
//                                transaction.replace(R.id.fragment_container, newFragment);
//                                transaction.addToBackStack(null);
//                                transaction.commit();
//
//                            }
//                        });
//                    }
//                });
//            } else {
//                nextButton.setVisibility(View.GONE);
//                findViewById(R.id.separatorSpace).setVisibility(View.GONE);
//            }

            homeButton.setImageBitmap(ImageManager.loadImageFromResource(context, R.drawable.home_button, MainApplication.lengthManager.getLevelFinishedButtonsSize(), MainApplication.lengthManager.getLevelFinishedButtonsSize()));
            tools.resizeView(homeButton, MainApplication.lengthManager.getLevelFinishedButtonsSize(), MainApplication.lengthManager.getLevelFinishedButtonsSize());

//            homeButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    LoadingManager.startTask(new TaskStartedListener() {
//                        @Override
//                        public void taskStarted() {
//                            ((IntroActivity) getActivity()).popFromViewStack(greetingsView);
//                            getActivity().onBackPressed();
//                            LoadingManager.endTask();
//                        }
//                    });
//                }
//            });

            TextView levelSolution = (TextView) findViewById(R.id.level_solution);
            TextView levelAuthor = (TextView) findViewById(R.id.author);

//            customizeTextView(levelSolution, mLevel.getSolution(), LengthManager.getLevelSolutionTextSize());
//            customizeTextView(levelAuthor, mLevel.getAuthor(), LengthManager.getLevelAuthorTextSize());

    }

    @Override
    public void onClick(View view) {
    }


private void customizeTextView(TextView textView, String label, float textSize) {
        textView.setText(label);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, MainApplication.lengthManager.getStoreItemFontSize());
        textView.setTextColor(Color.WHITE);
        textView.setShadowLayer(1, 2, 2, Color.BLACK);
        textView.setTypeface(FontsHolder.getHoma(textView.getContext()));
        }

        }