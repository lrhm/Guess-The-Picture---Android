package ir.treeco.aftabe2.View.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import ir.treeco.aftabe2.API.Socket.Objects.Friends.MatchRequestSFHolder;
import ir.treeco.aftabe2.API.Socket.Objects.Friends.MatchResultHolder;
import ir.treeco.aftabe2.API.Socket.Objects.Friends.OnlineFriendStatusHolder;
import ir.treeco.aftabe2.API.Socket.Objects.GameResult.GameResultHolder;
import ir.treeco.aftabe2.API.Socket.Objects.GameStart.GameStartObject;
import ir.treeco.aftabe2.API.Socket.Objects.Result.ResultHolder;
import ir.treeco.aftabe2.API.Socket.Objects.UserAction.UserActionHolder;
import ir.treeco.aftabe2.API.Socket.SocketAdapter;
import ir.treeco.aftabe2.API.Socket.Interfaces.SocketFriendMatchListener;
import ir.treeco.aftabe2.API.Socket.Interfaces.SocketListener;
import ir.treeco.aftabe2.Adapter.CoinAdapter;
import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Util.DownloadTask;
import ir.treeco.aftabe2.Util.ImageManager;
import ir.treeco.aftabe2.Util.SizeConverter;
import ir.treeco.aftabe2.Util.SizeManager;
import ir.treeco.aftabe2.Util.UiUtil;
import ir.treeco.aftabe2.View.Activity.MainActivity;
import ir.treeco.aftabe2.View.Custom.ToastMaker;
import ir.treeco.aftabe2.View.Fragment.OnlineGameFragment;


/**
 * Created by al on 3/16/16.
 */
public class LoadingDialog extends Dialog implements Runnable,
        SocketListener, DownloadTask.DownloadTaskListener, SocketFriendMatchListener, View.OnClickListener {

    private static final String TAG = "LoadingDialog";
    Context context;
    private boolean mDismissed = false;
    private boolean mRequestCancel = false;
    private int mLoadingStep;
    private ImageView mLoadingImageView;
    private static int mLoadingImageWidth = 0, mLoadingImageHeight = 0;
    private ImageManager imageManager;
    private static int[] mImageLoadingIds;
    private GameResultHolder mGameResultHolder;
    private static final Object lock = new Object();
    private int mDownloadCount = 0;
    CoinAdapter coinAdapter;
    long creationTime;
    boolean gotGame = false;
    boolean showCancel;

    String baseUrl = "https://aftabe2.com:2020/api/pictures/level/download/";


    public LoadingDialog(Context context) {
        super(context);
        this.context = context;
        creationTime = System.currentTimeMillis();
        imageManager = ImageManager.getInstance(context);
        coinAdapter = ((MainActivity) context).getCoinAdapter();
        SocketAdapter.addSocketListener(this);
        SocketAdapter.addFriendSocketListener(this);
        showCancel = false;
        initImageLoading();


    }


    public LoadingDialog(Context context, boolean showCancel) {
        super(context);
        this.context = context;
        creationTime = System.currentTimeMillis();
        imageManager = ImageManager.getInstance(context);
        coinAdapter = ((MainActivity) context).getCoinAdapter();
        SocketAdapter.addSocketListener(this);
        SocketAdapter.addFriendSocketListener(this);
        this.showCancel = showCancel;
        initImageLoading();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.dialog_loading);
        ((MainActivity) context).setIsInOnlineGame(true);


        mDismissed = false;
        mLoadingImageView = (ImageView) findViewById(R.id.activity_main_loading_image_view);
        mLoadingStep = 0;

        SizeConverter converter = SizeConverter.SizeConverterFromLessOffset(SizeManager.getScreenWidth(), SizeManager.getScreenHeight(),
                1080, 1800);
        mLoadingImageHeight = converter.mHeight;
        mLoadingImageWidth = converter.mWidth;

        UiUtil.setWidth(mLoadingImageView, mLoadingImageWidth);
        UiUtil.setHeight(mLoadingImageView, mLoadingImageHeight);
        UiUtil.setLeftMargin(mLoadingImageView, converter.getLeftOffset() / 2);
        UiUtil.setTopMargin(mLoadingImageView, converter.getTopOffset() / 2);

//        Picasso.with(context).load(R.drawable.search_sc_1).fit().into(mLoadingImageView);
        mLoadingImageHeight /= 2;
        mLoadingImageWidth /= 2;

        mLoadingImageView.setImageBitmap(imageManager.loadImageFromResourceNoCache(R.drawable.search_sc_1,
                mLoadingImageWidth, mLoadingImageHeight, ImageManager.ScalingLogic.FIT));


        if (showCancel) {
            ImageView cancelImageView = (ImageView) findViewById(R.id.loading_dialog_cancel);
            SizeConverter cancelConverter = SizeConverter.SizeConvertorFromWidth(SizeManager.getScreenWidth() * 0.2f, 169, 98);
            cancelImageView.setImageBitmap(imageManager.loadImageFromResource(R.drawable.cancel, cancelConverter.mWidth, cancelConverter.mHeight));
            cancelImageView.setOnClickListener(this);

            UiUtil.setLeftMargin(cancelImageView, SizeManager.getScreenWidth() / 2 - cancelConverter.mWidth / 2);
            UiUtil.setTopMargin(cancelImageView, converter.convertHeight(1400) + converter.getTopOffset());
            cancelImageView.setVisibility(View.VISIBLE);
        }


        new Handler().postDelayed(this, 1000);


    }

    @Override
    protected void onStop() {
        mDismissed = true;
        super.onStop();
    }

    private void initImageLoading() {


        if (mLoadingImageHeight != 0 && mLoadingImageWidth != 0
                && mImageLoadingIds != null)
            return;
        SizeConverter converter = SizeConverter.SizeConverterFromLessOffset(SizeManager.getScreenWidth(), SizeManager.getScreenHeight(),
                1080, 1800);
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
            ((MainActivity) context).setIsInOnlineGame(false);
            coinAdapter.earnCoins(100);
            ToastMaker.show(context, context.getString(R.string.try_later), Toast.LENGTH_SHORT);

            dismiss();
            return;
        }

        mLoadingImageView.setImageBitmap(imageManager.loadImageFromResourceNoCache(mImageLoadingIds[mLoadingStep],
                mLoadingImageWidth, mLoadingImageHeight, ImageManager.ScalingLogic.FIT));


        new Handler().postDelayed(this, 1000);

    }

    @Override
    public void onBackPressed() {
        if (mDismissed)
            return;

        ((MainActivity) context).setIsInOnlineGame(false);
        ((MainActivity) context).setOnlineGame(false);
        coinAdapter.earnCoins(100);
        SocketAdapter.cancelRequest();

        super.onBackPressed();
    }

    public void clearFiles() {
        final String path = context.getFilesDir().getPath() + "/online_game";
        Log.d("TAG", path);

        File parent = new File(path);
        if (!parent.exists()) {
            return;
        }

        for (File f : parent.listFiles())
            f.delete();

    }

    public void downloadURL(final String url) {
        final String path = context.getFilesDir().getPath() + "/online_game";
        Log.d("TAG", path);

        File parent = new File(path);
        if (!parent.exists()) {
            parent.mkdir();
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new DownloadTask(context, LoadingDialog.this).execute(url, path);
            }
        });
    }

    public void showGame(GameResultHolder gameResultHolder) {
        if (System.currentTimeMillis() - creationTime <= 15000)
            onGotGame(gameResultHolder);
    }

    @Override
    public void onGotGame(GameResultHolder gameHolder) {

        if (gotGame)
            return;
        gotGame = true;

        Log.d(TAG, "onGotGame in dialog");
        mGameResultHolder = gameHolder;
        clearFiles();
        String imagePath = baseUrl + gameHolder.getLevels()[0].getUrl();
        downloadURL(imagePath);

        imagePath = baseUrl + gameHolder.getLevels()[1].getUrl();

        downloadURL(imagePath);


    }

    @Override
    public void onDetachedFromWindow() {


        SocketAdapter.removeFriendSocketListener(this);
        SocketAdapter.removeSocketListener(this);

        super.onDetachedFromWindow();
    }

    public void doGameStart(final GameResultHolder gameHolder) {


        if (!((MainActivity) context).isFinishing()) {

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                    if (!((MainActivity) context).isFinishing()) {
                        dismiss();

                        Bundle bundle = new Bundle();
                        bundle.putInt("state", 0);
                        bundle.putBoolean("isMatch", !showCancel);

                        OnlineGameFragment gameFragment = new OnlineGameFragment();
                        gameFragment.setGameResultHolder(gameHolder);
                        gameFragment.setArguments(bundle);

                        FragmentTransaction transaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, gameFragment, "FRAGMENT_ONLINE_GAME");
                        transaction.addToBackStack(null);
                        transaction.commitAllowingStateLoss();
                    }

                }
            });


        }
    }

    @Override
    public void onGameStart(GameStartObject gameStartObject) {

        doGameStart(mGameResultHolder);

    }

    @Override
    public void onGotUserAction(UserActionHolder actionHolder) {

        Log.d(this.getClass().getName(), "should not happen");
    }

    @Override
    public void onFinishGame(ResultHolder resultHolder) {
        Log.d(this.getClass().getName(), "should not happen");

    }

    @Override
    public void onProgress(int progress) {

    }

    @Override
    public void onDownloadSuccess() {
        synchronized (lock) {
            Log.d("TAG", "downloaded");

            if (mDismissed)
                return;

            mDownloadCount++;
            if (mDownloadCount == 2) {
                SocketAdapter.setReadyStatus();
            }
        }
    }

    @Override
    public void onDownloadError(String error) {
        Log.d("TAG", "dodwnload error " + error);

    }


    @Override
    public void onMatchRequest(MatchRequestSFHolder request) {

    }

    @Override
    public void onOnlineFriendStatus(OnlineFriendStatusHolder status) {

    }

    @Override
    public void onMatchResultToSender(MatchResultHolder result) {

        if (!result.isAccept()) {
            dismiss();
        }
    }

    @Override
    public void onClick(View v) {


        ((MainActivity) context).setIsInOnlineGame(false);
        ((MainActivity) context).setOnlineGame(false);
        coinAdapter.earnCoins(100);
        SocketAdapter.cancelRequest();
        dismiss();

    }
}