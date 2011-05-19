/*
 * Copyright 2009 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package com.wambal;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public final class ShoppingListActivity extends Activity {

    @Override protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list);
    }

    @Override public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.shopping_list_menu, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shopping_list_add_item: return toaster("Add Item");
            case R.id.shopping_list_delete_item: return toaster("Delete Item");
            default: return super.onOptionsItemSelected(item);
        }
    }

    private boolean toaster(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        return true;
    }

//    @Override public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        getMenuInflater().inflate(R.menu.shopping_list_menu, menu);
//    }
//
//    @Override public boolean onContextItemSelected(final MenuItem item) {
//        return onOptionsItemSelected(item);
//    }
}
