package org.opennms.android.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import org.opennms.android.R;

public class AboutDialog extends DialogFragment {

  public static final String TAG = "AboutDialog";

  public AboutDialog() {
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    View view = LayoutInflater.from(getActivity()).inflate(R.layout.about_dialog, null);

    TextView content = (TextView) view.findViewById(R.id.about_info);
    content.setText(Html.fromHtml(getResources().getString(R.string.about_info)));
    content.setMovementMethod(LinkMovementMethod.getInstance());

    TextView versionTextView = (TextView) view.findViewById(R.id.about_version);
    String version = getResources().getString(R.string.version) + " ";
    try {
      version += getActivity().getPackageManager()
          .getPackageInfo(getActivity().getPackageName(), 0).versionName;
    } catch (NameNotFoundException e) {
      version += getResources().getString(R.string.unknown);
    }
    versionTextView.setText(version);

    Dialog dialog = new AlertDialog.Builder(getActivity())
        .setTitle(R.string.app_name_full)
        .setView(view)
        .setNeutralButton(
            getString(R.string.close_dialog),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
              }
            }).create();

    dialog.setCanceledOnTouchOutside(true);
    dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

    return dialog;
  }

}