package ir.treeco.aftabe.View.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.SizeConverter;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.UiUtil;

/**
 * Created by al on 5/18/16.
 */
public class IntroFragment extends Fragment implements View.OnClickListener {

    int picId;
    int fragmentId;
    OnFragmentButtonListener listener;

    public IntroFragment() {

    }

    public static IntroFragment getInstance(int picId, int fragmentId) {
        IntroFragment introFragment = new IntroFragment();
        introFragment.picId = picId;
        introFragment.fragmentId = fragmentId;

        return introFragment;
    }

    public void setOnFragmentButtonListner(OnFragmentButtonListener onFragmentButtonListner) {
        listener = onFragmentButtonListner;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FrameLayout relativeLayout = new FrameLayout(getContext());
        relativeLayout.setLayoutParams(new FrameLayout.LayoutParams(SizeManager.getScreenWidth(), ViewGroup.LayoutParams.MATCH_PARENT));

        relativeLayout.setBackgroundColor(Color.BLUE);
        ImageView imageView = new ImageView(getContext());

        SizeConverter sizeConverter = SizeConverter.SizeConvertorFromWidth(SizeManager.getScreenWidth(), 1080, 1920);

        ImageManager imageManager = new ImageManager(getContext());

        relativeLayout.addView(imageView);

        imageView.setImageBitmap(imageManager.loadImageFromResourceNoCache(picId, sizeConverter.mWidth, sizeConverter.mHeight, ImageManager.ScalingLogic.FIT));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        UiUtil.setTopMargin(imageView, sizeConverter.getTopOffset() / 2);
        UiUtil.setWidth(imageView, SizeManager.getScreenWidth());

        Button button = new Button(getContext());

        button.setBackgroundColor(Color.TRANSPARENT);

        relativeLayout.addView(button);

        UiUtil.setWidth(button, sizeConverter.convertWidth(650));
        UiUtil.setHeight(button, sizeConverter.convertHeightCalcOffset(230) );



        UiUtil.setTopMargin(button, sizeConverter.convertHeightCalcOffset(1500) );
        UiUtil.setLeftMargin(button, sizeConverter.convertWidth(225));

        button.setOnClickListener(this);


        return relativeLayout;

    }

    @Override
    public void onClick(View v) {

        if (listener != null)
            listener.onClick(fragmentId);
    }


    public interface OnFragmentButtonListener {

        void onClick(int id);
    }
}
