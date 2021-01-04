package com.petdox.mct.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;

import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.petdox.mct.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maroof Ahmed Siddique
 * Mindcrew Technologies Pvt Ltd
 */
public class ConstantUtils {

    public static ProgressDialog showLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.setContentView(R.layout.progress_dialog);

        AppCompatImageView pb_loading = progressDialog.findViewById(R.id.pb_loading);

        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        Glide.with(context).load(R.drawable.mainloading).into(pb_loading);

        return progressDialog;
    }

    public static void sendEmailMultipleFiles(Context context, String toAddress, String subject, String body, ArrayList<String> attachmentPath) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{toAddress});
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, body);
            intent.setType("message/rfc822");
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
            ResolveInfo best = null;
            for (final ResolveInfo info : matches) {
                if (info.activityInfo.packageName.contains(".gm.") || info.activityInfo.name.toLowerCase().contains("gmail"))
                    best = info;
            }
            ArrayList<Uri> uri = new ArrayList<>();
            for (int i = 0; i < attachmentPath.size(); i++) {
                File file = new File(attachmentPath.get(i));
                uri.add(Uri.fromFile(file));
            }

            if (uri.size() > 0)
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uri);

            if (best != null)
                intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);

            context.startActivity(Intent.createChooser(intent, "Choose an email application..."));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
}
