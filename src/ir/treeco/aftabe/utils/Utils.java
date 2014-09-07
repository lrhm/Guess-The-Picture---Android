package ir.treeco.aftabe.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by hamed on 8/12/14.
 */
public class Utils {
    public static void toggleVisibility(View view) {
        if (view.getVisibility() == View.VISIBLE)
            view.setVisibility(View.GONE);
        else
            view.setVisibility(View.VISIBLE);
    }

    public static InputStream getInputStreamFromRaw(Context context, String name, String type) {
        return context.getResources().openRawResource(
                context.getResources().getIdentifier("raw/" + name,
                        type, context.getPackageName()));
    }

    public static void download(Context context, String url, String path) throws Exception {
        /**
         *
         *          THIS method should overwrite the existing FILE.
         *                      BUT does IT???????
         *
         */
        Log.d("localing",url + " " + path);
        InputStream is = new URL(url).openStream();
        Log.d("localing", "is creater");
        OutputStream os = context.openFileOutput(path,0);
        Log.d("localing","before piping");
        pipe(is,os);
        os.close();
//        URL source = null;
//        try {
//            Log.d("synch",url+" try "+path);
//            source = new URL(url);
//        } catch (MalformedURLException e) {
//            Log.d("synch",url+" catch "+path);
//            throw new Exception("Bad url",e);
//        }
//        Log.d("synch",url+" rbc "+path);
//        ReadableByteChannel rbc = Channels.newChannel(source.openStream());
//        Log.d("synch",url+" fos "+path);
//        FileOutputStream fos = context.openFileOutput(path,0);
//        Log.d("synch",url+" transfer "+path);
//        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
//        Log.d("synch","end of download" + url+ " " + path);
    }

    public static void pipe(InputStream is, OutputStream os) throws IOException {
        int n;
        byte[] buffer = new byte[1024];
        int sum=0;
        while ((n = is.read(buffer)) > -1) {
            sum += n;
            Log.d("localing","downloading " + sum);
            os.write(buffer, 0, n);   // Don't allow any extra bytes to creep in, final write
        }
        os.close();
    }

    public static void reverseLinearLayout(LinearLayout linearLayout) {
        View views[] = new View[linearLayout.getChildCount()];
        for (int i = 0; i < views.length; i++)
            views[i] = linearLayout.getChildAt(i);
        linearLayout.removeAllViews();
        for (int i = views.length - 1; i >= 0; i--)
            linearLayout.addView(views[i]);
    }

    public static String sharedPrefrencesTag() {
        return "GOLVAZHE_XEJDIWE";
    }
}
