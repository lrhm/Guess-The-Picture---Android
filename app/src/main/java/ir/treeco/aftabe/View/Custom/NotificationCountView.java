package ir.treeco.aftabe.View.Custom;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.SizeConverter;
import ir.treeco.aftabe.Util.SizeManager;

/**
 * Created by al on 4/27/16.
 */
public class NotificationCountView extends FrameLayout {


    private BadgeView mBadgeView;
    private ImageView mImageView;

    public NotificationCountView(Context context, int drawable) {
        super(context);
        ImageManager imageManager = new ImageManager(context);

        int width = (int) (SizeManager.getScreenWidth() * 0.14);

        mImageView = new ImageView(context);
        mImageView.setImageBitmap(imageManager.loadImageFromResource(drawable, width, width));

        addView(mImageView);

        mBadgeView = new BadgeView(context, mImageView);




    }

    public void setCount(int count) {
        mBadgeView.setText(count+"");
       if(count == 0)
           mBadgeView.hide();
        else
           mBadgeView.show();

    }

}
