/*
 * Copyright 2009 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package com.wambal.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
    private static final int EDIT_DIALOG = 2;
    private static final int PROGRESS_DIALOG = 3;
    private static final String CATEGORY_EDIT_ID = "category.edit.id";
    private static final String CATEGORY_EDIT_NAME = "category.edit.name";

    private ProgressDialog progressDialog;

    @Override protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_category);
        progressDialog = createProgressDialog();
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

            @Override protected void onPreExecute() {
                if (!progressDialog.isShowing()) {
                    showDialog(PROGRESS_DIALOG);
                }
            }

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

                if (progressDialog.isShowing()) {
                    dismissDialog(PROGRESS_DIALOG);
                }

            }
        }.execute((Void) null);
    }

    @Override protected void onDestroy() {
        if (progressDialog.isShowing()) {
            removeDialog(PROGRESS_DIALOG);
        }
        super.onDestroy();
    }

    private Cursor getCategoryListCursor() {
        return getContentResolver().query(WambalContentProvider.CONTENT_URI,
                                            new String[]{WambalContentProvider.Category._ID,
                                                    WambalContentProvider.Category.NAME}, null, null, WambalContentProvider.Category.NAME);

    }

    @Override protected Dialog onCreateDialog(final int id, final Bundle args) {
        switch (id) {
            case CREATE_DIALOG:
                return createCreateCategoryDialog();
            case EDIT_DIALOG:
                return createEditCategoryDialog(args);
            case PROGRESS_DIALOG:
                return progressDialog;
            default:
                return super.onCreateDialog(id);
        }
    }

    private ProgressDialog createProgressDialog() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setTitle("Manage Category");
        dialog.setMessage("Loading...");
        return dialog;
    }

    private Dialog createEditCategoryDialog(final Bundle args) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.edit_category_title);
        final View view = getLayoutInflater().inflate(R.layout.manage_category_dialog_template, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        final Button okButton = (Button) view.findViewById(R.id.manage_category_dialog_template_ok_button);
        final TextView error = (TextView) view.findViewById(R.id.manage_category_dialog_template_error_text);
        error.setText("Please update category name");
        error.setVisibility(View.INVISIBLE);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(final View button) {
                final EditText text = (EditText) view.findViewById(R.id.manage_category_dialog_template_name);
                String category = text.getText().toString();
                if (isUnique(category)) {
                    updateCategory(category, text.getTag().toString());
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

    private void updateCategory(final String category, final String categoryId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WambalContentProvider.Category.NAME, category);
        try {
            getContentResolver().update(Uri.withAppendedPath(WambalContentProvider.CONTENT_URI, categoryId), contentValues, null, null);
        } catch (Exception e) {
            Toast.makeText(this, "Could not update category. " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override protected void onPrepareDialog(final int id, final Dialog dialog, final Bundle args) {
        switch (id) {
            case CREATE_DIALOG:
                TextView error = (TextView) dialog.findViewById(R.id.manage_category_dialog_template_error_text);
                error.setVisibility(View.INVISIBLE);
                EditText text = (EditText) dialog.findViewById(R.id.manage_category_dialog_template_name);
                text.getText().clear();
                break;
            case EDIT_DIALOG:
                TextView error1 = (TextView) dialog.findViewById(R.id.manage_category_dialog_template_error_text);
                error1.setVisibility(View.INVISIBLE);
                EditText text1 = (EditText) dialog.findViewById(R.id.manage_category_dialog_template_name);
                text1.setText(args.getString(CATEGORY_EDIT_NAME));
                text1.setTag(args.getInt(CATEGORY_EDIT_ID));
                break;
            default: super.onPrepareDialog(id, dialog, args);
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
                final Cursor cursor = (Cursor) list.getItemAtPosition(info.position);
                editItem(cursor);
                return true;
            case R.id.manage_category_delete_item_context_menu:
                deleteItem((int) info.id);
                return true;
            default: return super.onContextItemSelected(item);
        }
    }

    private boolean editItem(final Cursor cursor) {
        int idCol = cursor.getColumnIndex(WambalContentProvider.Category._ID);
        int nameCol = cursor.getColumnIndex(WambalContentProvider.Category.NAME);
        final Bundle args = new Bundle();
        if (idCol != -1 && nameCol != -1) {
            args.putInt(CATEGORY_EDIT_ID, cursor.getInt(idCol));
            args.putString(CATEGORY_EDIT_NAME, cursor.getString(nameCol));
            return showDialog(EDIT_DIALOG, args);
        } else {
            Toast.makeText(this, "There was an error connecting to the database.", Toast.LENGTH_LONG);
            return true; // there was a problem with the table columns, return true to signify the event was handled.
        }

    }

    private void deleteItem(final int id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WambalContentProvider.Category._ID, id);
        getContentResolver().delete(Uri.withAppendedPath(WambalContentProvider.Category.CONTENT_URI_SINGLE, ""+id), null, null);
    }
}
