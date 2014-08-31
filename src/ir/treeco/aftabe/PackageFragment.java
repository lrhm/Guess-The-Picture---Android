package ir.treeco.aftabe;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import ir.treeco.aftabe.packages.*;

/**
 * Created by hossein on 8/31/14.
 */
public class PackageFragment extends Fragment {
    TextView log;
    String logStr;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_package, container, false);
        log = (TextView) layout.findViewById(R.id.log_textview);
        log.setText(logStr);
        return layout;
    }

    public void setLog(ir.treeco.aftabe.packages.Package pkg) {
        logStr="";
        for(Level level:pkg.getLevels()) {
            logStr += level.getSoultion()+"\n";
        }
    }
}
