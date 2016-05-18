package ir.treeco.aftabe.View.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.LengthManager;
import ir.treeco.aftabe.Util.SizeConverter;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.UiUtil;

/**
 * Created by al on 5/18/16.
 */
public class CreditsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_credits);

        ImageView credits = (ImageView) findViewById(R.id.credits_image_view);
        ImageView aftabe = (ImageView) findViewById(R.id.credits_aftabe_image_view);

        ImageManager imageManager = new ImageManager(this);
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


    }
}
