/*
 * Copyright 2009 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package com.wambal;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.wambal.data.ShoppingListItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class ShoppingListActivity extends ListActivity {

    private List<ShoppingListItem> items;
    private MyListAdapter adapter;

    public ShoppingListActivity() {
        items = new ArrayList<ShoppingListItem>();
        adapter = new MyListAdapter(items, ShoppingListActivity.this);
    }

    @Override protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(adapter);
        ListView listView = getListView();
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                Toast.makeText(getApplicationContext(), adapter.getItem(i).toString(), Toast.LENGTH_SHORT).show();
            }
        });
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
        items.add(shoppingListItem);
        onContentChanged();
    }

    private boolean toaster(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        return true;
    }

    private void showShoppingListItem() {
        startActivity(new Intent(ShoppingListActivity.this, ShoppingListItemActivity.class));
    }

    private static class MyListAdapter extends BaseAdapter {

        private List<ShoppingListItem> shoppingListItemList;
        private WeakReference<Context> context;

        private MyListAdapter(List<ShoppingListItem> shoppingListItemList, Context context) {
            this.shoppingListItemList = shoppingListItemList;
            this.context = new WeakReference<Context>(context);
        }

        @Override public int getCount() {
            return shoppingListItemList.size();
        }

        @Override public Object getItem(final int i) {
            return shoppingListItemList.get(i);
        }

        @Override public long getItemId(final int i) {
            return i;
        }

        @Override public View getView(final int i, final View view, final ViewGroup viewGroup) {
            View inflated;
            if (view == null) {
                LayoutInflater inflater = getLayoutInflator();
                if (inflater != null) {
                    inflated = inflater.inflate(R.layout.shopping_list_line_item, viewGroup, false);
                } else {
                    return null;
                }
            } else {
                inflated = view;
            }

            TextView nameTV = (TextView) inflated.findViewById(R.id.shopping_list_line_item_name);
            TextView quantityTV = (TextView) inflated.findViewById(R.id.shopping_list_line_item_quantity);
            TextView costTV = (TextView) inflated.findViewById(R.id.shopping_list_line_item_cost);
            ShoppingListItem item = (ShoppingListItem) getItem(i);
            nameTV.setText(item.name);
            quantityTV.setText(""+item.quantity);
            costTV.setText(""+item.cost);
            return inflated;
        }

        private LayoutInflater getLayoutInflator() {
            if (context != null && context.get() != null) {
                return (LayoutInflater)context.get().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            } else {
                return null;
            }
        }
    }


}
