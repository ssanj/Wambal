/*
 * Copyright 2009 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package com.wambal.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.wambal.R;
import com.wambal.cp.WambalContentProvider;

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

        loadCategories();
    }

    private void loadCategories() {
        new AsyncTask<Void, Void, Cursor>() {

            @Override protected Cursor doInBackground(final Void... voids) {
                return getCategoryListCursor();
            }

            @Override protected void onPostExecute(final Cursor cursor) {
                startManagingCursor(cursor);
                ListView listView = (ListView) findViewById(R.id.manage_category_category_list);
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(ManageCategoryActivity.this, R.layout.manage_category_list_line_item, cursor,
                new String[]{WambalContentProvider.Category.NAME}, new int[]{R.id.manage_category_list_line_item_text});
                listView.setAdapter(adapter);
                registerForContextMenu(listView);
            }
        }.execute((Void) null);
    }

    private Cursor getCategoryListCursor() {
        return getContentResolver().query(WambalContentProvider.CONTENT_URI,
                                            new String[]{WambalContentProvider.Category._ID,
                                                    WambalContentProvider.Category.NAME}, null, null, WambalContentProvider.Category.NAME);

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
        final View view = getLayoutInflater().inflate(R.layout.manage_category_dialog_template, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        final Button okButton = (Button) view.findViewById(R.id.manage_category_dialog_template_ok_button);
        final TextView error = (TextView) view.findViewById(R.id.manage_category_dialog_template_error_text);
        error.setText("Please enter a unique category name");
        error.setVisibility(View.INVISIBLE);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(final View button) {
                EditText text = (EditText) view.findViewById(R.id.manage_category_dialog_template_name);
                String category = text.getText().toString();
                if (isUnique(category)) {
                    insertCategory(category);
                    dialog.dismiss();
                } else {
                    error.setVisibility(View.VISIBLE);
                }
            }
        });
        final Button cancelButton = (Button) view.findViewById(R.id.manage_category_dialog_template_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(final View button) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    private boolean isUnique(final String category) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WambalContentProvider.Category.NAME, category);
        Cursor cursor = null;
        try {
             cursor = getContentResolver().query(WambalContentProvider.CONTENT_URI,
                    new String[]{WambalContentProvider.Category.NAME}, WambalContentProvider.Category.NAME + "='" + category + "'", null, null);
            boolean result = !cursor.moveToFirst();
            return result;
        } catch (Exception e) {
            return true;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

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
                TextView error = (TextView) dialog.findViewById(R.id.manage_category_dialog_template_error_text);
                error.setVisibility(View.INVISIBLE);
                EditText text = (EditText) dialog.findViewById(R.id.manage_category_dialog_template_name);
                text.getText().clear();
                break;
            default: super.onPrepareDialog(id, dialog);
        }
    }

    @Override public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(R.string.manage_category_context_menu_title);
        getMenuInflater().inflate(R.menu.manage_category_menu, menu);
    }

    @Override public boolean onContextItemSelected(final MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
         ListView list = (ListView) findViewById(R.id.manage_category_category_list);
         switch(item.getItemId()) {
            case R.id.manage_category_edit_item_context_menu:
                Toast.makeText(this, "Edit Item: " + list.getItemAtPosition(info.position).toString(), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.manage_category_delete_item_context_menu:
                deleteItem((int) info.id);
                return true;
            default: return super.onContextItemSelected(item);
        }
    }

    private void deleteItem(final int id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WambalContentProvider.Category._ID, id);
        getContentResolver().delete(Uri.withAppendedPath(WambalContentProvider.Category.CONTENT_URI_SINGLE, ""+id), null, null);
    }
}
