package ir.treeco.aftabe.View.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import ir.treeco.aftabe.API.Socket.Objects.GameResult.GameResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.Result.ResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.UserAction.UserActionHolder;
import ir.treeco.aftabe.API.Socket.SocketAdapter;
import ir.treeco.aftabe.API.Socket.SocketListener;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.SizeConverter;
import ir.treeco.aftabe.Util.SizeManager;


/**
 * Created by al on 3/16/16.
 */
public class LoadingDialog extends Dialog implements Runnable, SocketListener {

    Context context;
    private boolean mDismissed = false;
    private int mLoadingStep;
    private ImageView mLoadingImageView;
    private static int mLoadingImageWidth = 0, mLoadingImageHeight = 0;
    private ImageManager imageManager;
    private static int[] mImageLoadingIds;

    public LoadingDialog(Context context) {
        super(context);
        this.context = context;
        imageManager = new ImageManager(context);
        initImageLoading();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.dialog_loading);

        mDismissed = false;
        mLoadingImageView = (ImageView) findViewById(R.id.activity_main_loading_image_view);
        mLoadingStep = 0;
        mLoadingImageView.setImageBitmap(imageManager.loadImageFromResourceNoCache(mImageLoadingIds[0],
                mLoadingImageWidth, mLoadingImageHeight, ImageManager.ScalingLogic.CROP));
        new Handler().postDelayed(this, 1000);
        SocketAdapter.addSocketListener(this);


    }



    private void initImageLoading() {


        if (mLoadingImageHeight != 0 && mLoadingImageWidth != 0
                && mImageLoadingIds != null)
            return;
        SizeConverter converter = SizeConverter.SizeConverterFromLessOffset(SizeManager.getScreenWidth(), SizeManager.getScreenHeight(),
                1200, 2000);
        mLoadingImageHeight = converter.mHeight;
        mLoadingImageWidth = converter.mWidth;

        int[] idt = {R.drawable.search_sc_1,
                R.drawable.search_sc_2,
                R.drawable.search_sc_3,
                R.drawable.search_sc_4,
                R.drawable.search_sc_5,
                R.drawable.search_sc_6,
                R.drawable.search_sc_7,
                R.drawable.search_sc_8,
                R.drawable.search_sc_9,
                R.drawable.search_sc_10,
                R.drawable.search_sc_11,
                R.drawable.search_sc_12,
                R.drawable.search_sc_13,
                R.drawable.search_sc_14,
                R.drawable.search_sc_15,
                R.drawable.search_sc_16,
                R.drawable.search_sc_17,
                R.drawable.search_sc_18,
                R.drawable.search_sc_19,
                R.drawable.search_sc_20,
                R.drawable.search_sc_21,
                R.drawable.search_sc_22};

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
            dismiss();
            return;
        }

        mLoadingImageView.setImageBitmap(imageManager.loadImageFromResourceNoCache(mImageLoadingIds[mLoadingStep],
                mLoadingImageWidth, mLoadingImageHeight, ImageManager.ScalingLogic.CROP));


        new Handler().postDelayed(this, 1000);

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    public void onGotGame(GameResultHolder gameHolder) {
        Log.d(this.getClass().getName(), gameHolder.toString());
        SocketAdapter.removeSocketListener(this);


    }

    @Override
    public void onGotUserAction(UserActionHolder actionHolder) {

        Log.d(this.getClass().getName(), "should not happen");
    }

    @Override
    public void onFinishGame(ResultHolder resultHolder) {
        Log.d(this.getClass().getName(), "should not happen");

    }
}
