package ir.treeco.aftabe2.View.Fragment;

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

import ir.treeco.aftabe2.Util.ImageManager;
import ir.treeco.aftabe2.Util.SizeConverter;
import ir.treeco.aftabe2.Util.SizeManager;
import ir.treeco.aftabe2.Util.UiUtil;

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

        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(SizeManager.getScreenWidth(), ViewGroup.LayoutParams.MATCH_PARENT));

        RelativeLayout buttonContainer = new RelativeLayout(getContext());

        frameLayout.setBackgroundColor(Color.BLUE);
        ImageView imageView = new ImageView(getContext());

        SizeConverter sizeConverter = SizeConverter.SizeConvertorFromWidth(SizeManager.getScreenWidth(), 1080, 1920);

        ImageManager imageManager = ImageManager.getInstance(getContext());

        frameLayout.addView(imageView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT));

        frameLayout.addView(buttonContainer);

        imageView.setImageBitmap(imageManager.loadImageFromResourceNoCache(picId, sizeConverter.mWidth, sizeConverter.mHeight, ImageManager.ScalingLogic.FIT));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        UiUtil.setTopMargin(imageView , sizeConverter.getTopOffset()/2);
        UiUtil.setWidth(imageView, SizeManager.getScreenWidth());
        UiUtil.setHeight(imageView , sizeConverter.mHeight);

        Button button = new Button(getContext());

        button.setBackgroundColor(Color.TRANSPARENT);

        buttonContainer.addView(button);

        UiUtil.setWidth(button, sizeConverter.convertWidth(650));
        UiUtil.setHeight(button, sizeConverter.convertHeight(230) );

//
//        Log.d("TEST", sizeConverter.mHeight + " " + sizeConverter.convertHeight(1500) + " " + sizeConverter.getTopOffset()  );
//        Log.d("TEST", SizeManager.getScreenWidth() + " " + SizeManager.getScreenHeight());
        UiUtil.setTopMargin(button, sizeConverter.convertHeight(1500) +sizeConverter.getTopOffset()/2  );
        UiUtil.setLeftMargin(button, sizeConverter.convertWidth(225));

        button.setOnClickListener(this);


        return frameLayout;

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
