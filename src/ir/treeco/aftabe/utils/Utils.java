package ir.treeco.aftabe.utils;

import android.content.Context;
import android.view.View;

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
        URL source = null;
        try {
            source = new URL(url);
        } catch (MalformedURLException e) {
            throw new Exception("Bad url",e);
        }
        ReadableByteChannel rbc = Channels.newChannel(source.openStream());
        FileOutputStream fos = context.openFileOutput(path,0);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }

    public static void pipe(InputStream is, OutputStream os) throws IOException {
        int n;
        byte[] buffer = new byte[1024];
        while ((n = is.read(buffer)) > -1) {
            os.write(buffer, 0, n);   // Don't allow any extra bytes to creep in, final write
        }
        os.close();
    }
}
