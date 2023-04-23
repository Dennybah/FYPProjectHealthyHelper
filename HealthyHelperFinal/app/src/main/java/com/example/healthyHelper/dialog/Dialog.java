package com.example.healthyHelper.dialog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.healthyHelper.BloodGlucoseRecorderMenu;

public class Dialog extends AppCompatDialogFragment {

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Added Entries")
                .setMessage(BloodGlucoseRecorderMenu.dialogString)
                .setPositiveButton("ok", (dialog, which) -> {

                });
        return builder.create();
    }
}
