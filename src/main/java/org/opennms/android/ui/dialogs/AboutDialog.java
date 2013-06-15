package org.opennms.android.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
    public static final String TAG = "about";

    public AboutDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.about_dialog, null);

        TextView content = (TextView) view.findViewById(R.id.about_info);
        content.setText(Html.fromHtml(getResources().getString(R.string.about_info)));
        content.setMovementMethod(LinkMovementMethod.getInstance());

        Dialog dialog = new AlertDialog.Builder(getActivity())
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