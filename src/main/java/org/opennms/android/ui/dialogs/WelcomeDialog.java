package org.opennms.android.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import org.opennms.android.R;
import org.opennms.android.Utils;
import org.opennms.android.ui.SettingsActivity;

public class WelcomeDialog extends DialogFragment {

    public static final String TAG = "WelcomeDialog";

    public WelcomeDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.welcome)
                .setMessage(getResources().getString(R.string.welcome_message))
                .setPositiveButton(
                        getResources().getString(R.string.welcome_message_pos_button),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent settingsIntent =
                                        new Intent(getActivity(), SettingsActivity.class);
                                startActivity(settingsIntent);
                            }
                        })
                .setNegativeButton(
                        getResources().getString(R.string.welcome_message_neg_button),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Toast message = Toast.makeText(
                                        getActivity(),
                                        getString(R.string.welcome_settings_toast),
                                        Toast.LENGTH_LONG);
                                message.setGravity(Gravity.TOP,
                                                   0, Utils.getActionBarHeight(getActivity()));
                                message.show();
                            }
                        })
                .setCancelable(false)
                .create();

        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        return dialog;
    }

}