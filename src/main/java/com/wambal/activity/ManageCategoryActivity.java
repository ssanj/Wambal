/*
 * Copyright 2009 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package com.wambal.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.wambal.R;

public final class ManageCategoryActivity extends Activity {

    private static final int CREATE_DIALOG = 1;

    @Override protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_category);
        Button addButton = (Button) findViewById(R.id.manage_category_add_category_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(final View view) {
                showDialog(CREATE_DIALOG);
            }
        });
    }

    @Override protected Dialog onCreateDialog(final int id) {
        switch (id) {
            case CREATE_DIALOG:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.create_category_title);
                final View customView = getLayoutInflater().inflate(R.layout.manage_category_dialog_template, null);
                builder.setView(customView);
                builder.setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (dialog instanceof Dialog) {
                                    EditText text = (EditText) ((Dialog) dialog).findViewById(R.id.manage_category_dialog_template_name);
                                    Toast.makeText(ManageCategoryActivity.this, text.getText().toString(), Toast.LENGTH_SHORT).show();
                                } else { /*  Not a dialog so do nothing. */ }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                return builder.create();
            default:
                return super.onCreateDialog(id);
        }
    }

    @Override protected void onPrepareDialog(final int id, final Dialog dialog) {
        switch (id) {
            case CREATE_DIALOG:
                EditText text = (EditText) dialog.findViewById(R.id.manage_category_dialog_template_name);
                text.getText().clear();
                break;
            default: super.onPrepareDialog(id, dialog);
        }
    }
}
