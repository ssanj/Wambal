/*
 * Copyright 2009 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package com.wambal.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import com.wambal.R;
import com.wambal.cp.WambalContentProvider;

import java.util.ArrayList;

public final class ManageCategoryActivity extends Activity {

    private static final int CREATE_DIALOG = 1;

    @Override protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_category);
        Button addButton = (Button) findViewById(R.id.manage_category_add_category_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(final View view) {
                ManageCategoryActivity.this.showDialog(CREATE_DIALOG);
            }
        });

        ListView listView = (ListView) findViewById(R.id.manage_category_category_list);
//        ListAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, new String[] {"one", "two", "three"});
        Cursor cursor = getContentResolver().query(WambalContentProvider.CONTENT_URI, new String[]{WambalContentProvider.Category._ID, WambalContentProvider.Category.NAME}, null, null, null);
        startManagingCursor(cursor);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor,
                new String[]{WambalContentProvider.Category.NAME}, new int[]{android.R.id.text1});
        listView.setAdapter(adapter);
    }

    private ListView.FixedViewInfo createFixedViewInfo(ListView listView, final LayoutInflater inflater, final int layout) {
        ListView.FixedViewInfo fvi= listView.new FixedViewInfo();
        fvi.data = null;
        fvi.isSelectable = false;
        fvi.view = inflater.inflate(layout, null);
        return fvi;
    }


    @Override protected Dialog onCreateDialog(final int id) {
        switch (id) {
            case CREATE_DIALOG:
                return createCreateCategoryDialog();
            default:
                return super.onCreateDialog(id);
        }
    }

    private Dialog createCreateCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.create_category_title);
        final View customView = getLayoutInflater().inflate(R.layout.manage_category_dialog_template, null);
        builder.setView(customView);
        builder.setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (dialog instanceof Dialog) {
                            EditText text = (EditText) ((Dialog) dialog).findViewById(R.id.manage_category_dialog_template_name);
                            insertCategory(text.getText().toString());
                        } else { /*  Not a dialog so do nothing. */ }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    //todo: Should this run in an AsyncTask?
    private void insertCategory(final String category) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WambalContentProvider.Category.NAME, category);
        try {
            getContentResolver().insert(WambalContentProvider.CONTENT_URI, contentValues);
        } catch (Exception e) {
            Toast.makeText(this, "Could not insert category. " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
