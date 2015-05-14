package ir.treeco.aftabe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import ir.treeco.aftabe.utils.ImageManager;
import ir.treeco.aftabe.utils.LengthManager;
import ir.treeco.aftabe.utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class AdActivity extends Activity {
    File imageFile = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_ad_layout);

        LengthManager LengthManager = new LengthManager();

        Bitmap bitmap;

        Intent intent = getIntent();

        String imageName = intent.getStringExtra("imageName");

        try {
//            imageFile = new File(getCacheDir(), AdReceiver.adFileName);
            imageFile = new File(getFilesDir(), imageName);
            InputStream inputStream = new FileInputStream(imageFile);
            bitmap = ImageManager.loadImageFromInputStream(inputStream, LengthManager.getScreenWidth(), LengthManager.getScreenHeight());
        } catch (FileNotFoundException e) {
            finish();
            return;
        }

        ImageView imageView = (ImageView) findViewById(R.id.adImage);
        imageView.setImageBitmap(bitmap);

        final String onclick = intent.getStringExtra("onclick");

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(onclick));
                startActivity(intent);
            }
        });

        String promote = intent.getStringExtra("promote");
        int prize = intent.getIntExtra("prize", 0);

        if (promote != null)
            addPromotion(promote, prize);
    }

    static final String PROMOTIONS_TAG = "promotions";

    void addPromotion(String promote, int prize) {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(Utils.SHARED_PREFRENCES_TAG, MODE_PRIVATE);
        String jsonData = preferences.getString(PROMOTIONS_TAG, "[]");

        JSONArray promotions;
        try {
            promotions = new JSONArray(jsonData);
        } catch (JSONException e) {
            return;
        }

        for (int i = 0; i < promotions.length(); i++) {
            String appName;
            try {
                JSONArray him = (JSONArray) promotions.get(i);
                appName = him.getString(0);
            } catch (JSONException e) {
                return;
            }
            if (appName.equals(promote))
                return;
        }

        JSONArray me = new JSONArray();
        me.put(promote).put(prize);

        promotions.put(me);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PROMOTIONS_TAG, promotions.toString());
        editor.commit();

        //Log.i("GOLVAZHE", "Promotions updated to: " + promotions);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (imageFile != null)
            imageFile.delete();
    }
}
