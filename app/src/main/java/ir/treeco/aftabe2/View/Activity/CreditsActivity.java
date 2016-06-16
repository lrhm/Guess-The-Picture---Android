package ir.treeco.aftabe2.View.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Util.ImageManager;
import ir.treeco.aftabe2.Util.LengthManager;
import ir.treeco.aftabe2.Util.SizeConverter;
import ir.treeco.aftabe2.Util.SizeManager;
import ir.treeco.aftabe2.Util.UiUtil;
import ir.treeco.aftabe2.View.Custom.ToastMaker;

/**
 * Created by al on 5/18/16.
 */
public class CreditsActivity extends Activity implements View.OnClickListener {

    ImageView[] faces;
    int[] facesDrawables;
    SizeConverter imageConverter;
    ImageManager imageManager;

    private final static int MODE_ANGRY = 0;
    private final static int MODE_NORMAL = 2;
    private final static int MODE_FACE = 1;

    int[] facesStatus;

    String[] names;

    boolean[] showedNames;

    private long lastTimeClicked = 0;

    int clickCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_credits);

        if (SizeManager.getScreenHeight() == 0 || SizeManager.getScreenHeight() == 0) {
            SizeManager.initSizes(this);
        }


        ImageView credits = (ImageView) findViewById(R.id.credits_image_view);
        ImageView aftabe = (ImageView) findViewById(R.id.credits_aftabe_image_view);

        imageManager = ImageManager.getInstance(this);
        LengthManager lengthManager = new LengthManager(this);


        aftabe.setImageBitmap(imageManager.loadImageFromResource(
                R.drawable.header, lengthManager.getScreenWidth(),
                lengthManager.getScreenWidth() / 4
        ));


        credits.setScaleType(ImageView.ScaleType.FIT_XY);

        SizeConverter creditsConverter = SizeConverter.SizeConvertorFromWidth(SizeManager.getScreenWidth(), 1200, 2000);

        UiUtil.setWidth(credits, creditsConverter.mWidth);
        UiUtil.setHeight(credits, creditsConverter.mHeight);
        Picasso.with(this).load(R.drawable.credits).into(credits);


        facesDrawables = new int[]{R.drawable.alaangry,
                R.drawable.alaface,
                R.drawable.alanormal,
                R.drawable.aminangry,
                R.drawable.aminface,
                R.drawable.amin,
                R.drawable.aliface,
                R.drawable.alinormal,
                R.drawable.aliangry,
                R.drawable.sina1,
                R.drawable.sina2,
                R.drawable.sinanormal,

        };

        int[] imageIds = new int[]{
                R.id.credits_ala,
                R.id.credits_amin,
                R.id.credits_ali,
                R.id.credits_sina
        };

        facesStatus = new int[imageIds.length];

        names = new String[]{"امیرعلا معصومی", "محمد امین مرادی", "علی رحیمی", "سینا شرف زاده"};
        faces = new ImageView[imageIds.length];
        showedNames = new boolean[imageIds.length];

        imageConverter = SizeConverter.SizeConvertorFromWidth(creditsConverter.convertWidth(200), 180, 260);

        int facesHeight = creditsConverter.convertHeight(1550);
        if (creditsConverter.getTopOffset() < 0)
            facesHeight = creditsConverter.convertHeightCalcOffset(1550);
        UiUtil.setTopMargin(findViewById(R.id.credits_faces_container), facesHeight);


        for (int i = 0; i < imageIds.length; i++) {
            showedNames[i] = false;
            faces[i] = (ImageView) findViewById(imageIds[i]);
            faces[i].setTag(i);
            faces[i].setOnClickListener(this);

            facesStatus[i] = MODE_ANGRY;

            int leftMargin = (int) (imageConverter.mWidth * 0.05);
            if (i != 0)
                UiUtil.setLeftMargin(faces[i], leftMargin);

            setFace(i, MODE_NORMAL);

        }


        setUpInstaAndTelegram(creditsConverter);

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    void setUpInstaAndTelegram(SizeConverter creditsConverter) {

        Button telegram = (Button) findViewById(R.id.credits_telegram);
        Button insta = (Button) findViewById(R.id.credits_insta);

        UiUtil.setWidth(telegram, creditsConverter.convertWidth(200));
        UiUtil.setHeight(telegram, creditsConverter.convertHeight(65));


        UiUtil.setWidth(insta, creditsConverter.convertWidth(200));
        UiUtil.setHeight(insta, creditsConverter.convertHeight(65));


        int buttonsHeight = creditsConverter.convertHeight(1080);
        if (creditsConverter.getTopOffset() < 0)
            buttonsHeight = creditsConverter.convertHeightCalcOffset(1080);

        UiUtil.setLeftMargin(insta, creditsConverter.convertWidth(660));
        UiUtil.setTopMargin(insta, buttonsHeight);

        UiUtil.setLeftMargin(telegram, creditsConverter.convertWidth(895));
        UiUtil.setTopMargin(telegram, buttonsHeight);


        telegram.setOnClickListener(this);
        insta.setOnClickListener(this);
    }


    public void setFace(int index, int mode) {

        if (facesStatus[index] == mode)
            mode = Math.abs(mode - 1);

        facesStatus[index] = mode;
        int faceDrawable = facesDrawables[index * (facesDrawables.length / faces.length) + mode];

        faces[index].setImageBitmap(imageManager.loadImageFromResource(faceDrawable, imageConverter.mWidth, imageConverter.mHeight));
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.credits_telegram) {
            startTelegramIntent();

            return;
        }
        if (v.getId() == R.id.credits_insta) {


            startInstaIntent();

            return;
        }

        // clicked on faces

        long curTimeClicked = System.currentTimeMillis();

        Random random = new Random(System.currentTimeMillis());
        int randFace = random.nextInt(3);
        int indx = (int) v.getTag();
        setFace(indx, randFace);


        if (!showedNames[indx]) {

            ToastMaker.show(this, names[indx], Toast.LENGTH_SHORT);
            showedNames[indx] = true;
        }

    }

    public void startViewPages(String appName, String name, String parse) {
        boolean isAppInstalled = isAppAvailable(appName);
        if (isAppInstalled) {
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(parse));
            myIntent.setPackage(appName);
            startActivity(myIntent);
        } else {

            ToastMaker.show(this, "نصب نیست " + name, Toast.LENGTH_SHORT);
        }
    }

    public void startInstaIntent() {
        String appName = "com.instagram.android";

        String parse = "http://instagram.com/_u/aftabe2";

        String name = "اینستا";

        startViewPages(appName, name, parse);


    }

    public void startTelegramIntent() {
        String appName = "org.telegram.messenger";

        String parse = "https://telegram.me/aftabe2";

        String name = "تلگرام";

        startViewPages(appName, name, parse);
    }

    public boolean isAppAvailable(String appName) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(appName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


}
