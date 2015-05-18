package ir.treeco.aftabe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import ir.treeco.aftabe.packages.MetaPackage;

public class DownloadCancelAlert extends DialogFragment {
    private MetaPackage metaPackage;

    public DownloadCancelAlert(MetaPackage metaPackage) {
        this.metaPackage = metaPackage;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("از کرده خود پشیمانی؟")
                .setPositiveButton("بلی", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        metaPackage.setIsDownloading(false);
                    }
                })
                .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        return builder.create();
    }
}

