package ir.treeco.aftabe.View.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;

import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.LengthManager;
import ir.treeco.aftabe.Util.SizeConverter;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.UiUtil;
import ir.treeco.aftabe.View.Custom.ToastMaker;

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

    int clickCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_credits);

        ImageView credits = (ImageView) findViewById(R.id.credits_image_view);
        ImageView aftabe = (ImageView) findViewById(R.id.credits_aftabe_image_view);

        imageManager = new ImageManager(this);
        LengthManager lengthManager = new LengthManager(this);


        aftabe.setImageBitmap(imageManager.loadImageFromResource(
                R.drawable.header, lengthManager.getScreenWidth(),
                lengthManager.getScreenWidth() / 4
        ));


        credits.setScaleType(ImageView.ScaleType.FIT_XY);

        SizeConverter creditsConverter = SizeConverter.SizeConvertorFromWidth(SizeManager.getScreenWidth(), 1080, 1800);

        credits.setImageBitmap(imageManager.loadImageFromResourceNoCache(
                R.drawable.credits, creditsConverter.mWidth, creditsConverter.mHeight,
                ImageManager.ScalingLogic.FIT));

        facesDrawables = new int[]{R.drawable.alaangry,
                R.drawable.alaface,
                R.drawable.alanormal,
                R.drawable.aminangry,
                R.drawable.aminface,
                R.drawable.amin,
                R.drawable.aliface,
                R.drawable.alinormal,
                R.drawable.aliangry
        };

        int[] imageIds = new int[]{
                R.id.credits_ala,
                R.id.credits_amin,
                R.id.credits_ali
        };

        faces = new ImageView[imageIds.length];

        imageConverter = SizeConverter.SizeConvertorFromWidth(creditsConverter.convertWidth(200), 156, 234);

        for (int i = 0; i < 3; i++) {
            faces[i] = (ImageView) findViewById(imageIds[i]);
            faces[i].setTag(i);
            faces[i].setOnClickListener(this);
            UiUtil.setTopMargin(faces[i], creditsConverter.convertHeight(1266));

            int leftMargin = (int) (i * imageConverter.mWidth * 1.05 + creditsConverter.convertWidth(240));
            UiUtil.setLeftMargin(faces[i], leftMargin);

            setFace(i, MODE_NORMAL);
        }
    }

    public void setFace(int index, int mode) {

        int faceDrawable = facesDrawables[index * (facesDrawables.length / 3) + mode];

        faces[index].setImageBitmap(imageManager.loadImageFromResource(faceDrawable, imageConverter.mWidth, imageConverter.mHeight));
    }

    @Override
    public void onClick(View v) {

        clickCount++;

        Random random = new Random(System.currentTimeMillis());
        int randFace = random.nextInt(3);
        int randIndx = random.nextInt(3);

        if (clickCount % 10 == 0) {

            for (int i = 0; i < 3; i++)
                setFace(i, MODE_ANGRY);
            ToastMaker.show(this, "نزن !", Toast.LENGTH_SHORT);
            return;
        }

        setFace(randIndx, randFace);


    }
}
