package ir.treeco.aftabe.View.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;

import ir.treeco.aftabe.API.Socket.Objects.GameResult.GameResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.GameStart.GameStartObject;
import ir.treeco.aftabe.API.Socket.Objects.Result.ResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.UserAction.UserActionHolder;
import ir.treeco.aftabe.API.Socket.SocketAdapter;
import ir.treeco.aftabe.API.Socket.SocketListener;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.DownloadTask;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.SizeConverter;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Fragment.GameResultFragment;
import ir.treeco.aftabe.View.Fragment.OnlineGameFragment;
import ir.treeco.aftabe.View.Fragment.StoreFragment;


/**
 * Created by al on 3/16/16.
 */
public class LoadingForGameResultDialog extends Dialog implements Runnable, SocketListener {

    Context context;
    private boolean mDismissed = false;
    private ImageView mLoadingImageView;
    private static int mLoadingImageWidth = 0, mLoadingImageHeight = 0;
    private ImageManager imageManager;
    private static int[] mImageLoadingIds;
    int mLoadingStep = 0;


    public LoadingForGameResultDialog(Context context) {
        super(context);
        this.context = context;
        imageManager = new ImageManager(context);
        ((MainActivity) context).setLoadingForGameResultDialog(this);
        initImageLoading();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#90000000")));

        setContentView(R.layout.dialog_game_result_loading);

        mDismissed = false;
        mLoadingImageView = (ImageView) findViewById(R.id.activity_main_loading_image_view);
        mLoadingImageView.setImageBitmap(imageManager.loadImageFromResourceNoCache(mImageLoadingIds[0],
                mLoadingImageWidth, mLoadingImageHeight, ImageManager.ScalingLogic.FIT));
        new Handler().postDelayed(this, 20);
        SocketAdapter.addSocketListener(this);


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = SizeManager.getScreenWidth();
        lp.height = SizeManager.getScreenHeight();
        getWindow().setAttributes(lp);


    }


    private void initImageLoading() {


        if (mLoadingImageHeight != 0 && mLoadingImageWidth != 0
                && mImageLoadingIds != null)
            return;
        SizeConverter converter = SizeConverter.SizeConvertorFromWidth(SizeManager.getScreenWidth() * 0.5f,
                128, 128);
        mLoadingImageHeight = converter.mHeight;
        mLoadingImageWidth = converter.mWidth;

        int[] idt = {R.drawable.pr_2_00000,
                R.drawable.pr_2_00001,
                R.drawable.pr_2_00002,
                R.drawable.pr_2_00003,
                R.drawable.pr_2_00004,
                R.drawable.pr_2_00005,
                R.drawable.pr_2_00006,
                R.drawable.pr_2_00007,
                R.drawable.pr_2_00008,
                R.drawable.pr_2_00009,
                R.drawable.pr_2_00010,
                R.drawable.pr_2_00011,
                R.drawable.pr_2_00012,
                R.drawable.pr_2_00013,
                R.drawable.pr_2_00014,
                R.drawable.pr_2_00015,
                R.drawable.pr_2_00016,
                R.drawable.pr_2_00017,
                R.drawable.pr_2_00018,
                R.drawable.pr_2_00019,
                R.drawable.pr_2_00020,
                R.drawable.pr_2_00021,
                R.drawable.pr_2_00022,
                R.drawable.pr_2_00023,
                R.drawable.pr_2_00024,
                R.drawable.pr_2_00025,
                R.drawable.pr_2_00026,
                R.drawable.pr_2_00027,
                R.drawable.pr_2_00028,
                R.drawable.pr_2_00029,
                R.drawable.pr_2_00030,
                R.drawable.pr_2_00031,
                R.drawable.pr_2_00032};

        mImageLoadingIds = idt;


    }


    @Override
    public void dismiss() {
        mDismissed = true;
        super.dismiss();
    }

    @Override
    public void run() {

        mLoadingStep++;

        if (mDismissed)
            return;

        if (mLoadingStep == mImageLoadingIds.length) { // the last image
            mLoadingStep = 0;

        }

        mLoadingImageView.setImageBitmap(imageManager.loadImageFromResourceNoCache(mImageLoadingIds[mLoadingStep],
                mLoadingImageWidth, mLoadingImageHeight, ImageManager.ScalingLogic.FIT));


        new Handler().postDelayed(this, 20);

    }

    @Override
    public void onGotGame(GameResultHolder gameHolder) {


    }

    public void doGameStart(GameResultHolder gameHolder) {


    }

    @Override
    public void onGameStart(GameStartObject gameStartObject) {


    }

    @Override
    public void onGotUserAction(UserActionHolder actionHolder) {

    }

    @Override
    public void onFinishGame(ResultHolder resultHolder) {


        dismiss();

        boolean win = false;
        if (resultHolder.getScores()[0].getUserId().equals(Tools.getCachedUser().getId()))
            win = resultHolder.getScores()[0].isWinner();

        if (resultHolder.getScores()[1].getUserId().equals(Tools.getCachedUser().getId()))
            win = resultHolder.getScores()[1].isWinner();

        GameResultFragment gameResultFragment = GameResultFragment.newInstance(win);
        FragmentTransaction transaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, gameResultFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public void onDetachedFromWindow() {
        SocketAdapter.removeSocketListener(this);
        ((MainActivity) context).setLoadingForGameResultDialog(null);
        super.onDetachedFromWindow();
    }
}
