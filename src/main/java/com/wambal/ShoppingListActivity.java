/*
 * Copyright 2009 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package com.wambal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.wambal.data.ShoppingListItem;

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
            case R.id.shopping_list_add_item:
                showShoppingListItem();
                return true;
            case R.id.shopping_list_delete_item: return toaster("Delete Item");
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        ShoppingListItem shoppingListItem = (ShoppingListItem) intent.getParcelableExtra("ShoppingListItemActivity.shoppingListItem");
        toaster("Got a new intent: " + shoppingListItem.toString());
    }

    private boolean toaster(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        return true;
    }

    private void showShoppingListItem() {
        startActivity(new Intent(ShoppingListActivity.this, ShoppingListItemActivity.class));
    }
}
