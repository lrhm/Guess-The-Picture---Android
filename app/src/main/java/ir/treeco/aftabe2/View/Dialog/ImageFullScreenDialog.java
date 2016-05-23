package ir.treeco.aftabe2.View.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Util.SizeManager;

public class ImageFullScreenDialog extends Dialog implements View.OnClickListener {
    Context context;
    ImageView imageView;
    String path;

    public ImageFullScreenDialog(Context context, String path) {
        super (context);
        this.context = context;
        this.path = path;
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.dialog_image_fullscreen);
        imageView = (ImageView) findViewById (R.id.image);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = SizeManager.getScreenWidth();
        lp.height = SizeManager.getScreenHeight();
        getWindow().setAttributes(lp);

        imageView.setOnClickListener(this);

        Picasso.with(context).load(path).rotate(-90).into(imageView);
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }
}

