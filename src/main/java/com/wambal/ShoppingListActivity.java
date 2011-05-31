/*
 * Copyright 2009 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package com.wambal;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.wambal.data.ShoppingListItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class ShoppingListActivity extends ListActivity {

    private List<ShoppingListItem> items;
    private ListAdapter adapter;
    private MyListAdapter myListAdapter;
    private TextView totalCost;

    public ShoppingListActivity() {
    }

    @Override protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restoreSaveData();
        configureList();
    }

    private void configureList() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        ArrayList<ListView.FixedViewInfo> footers = new ArrayList<ListView.FixedViewInfo>();
        ArrayList<ListView.FixedViewInfo> headers = new ArrayList<ListView.FixedViewInfo>();
        headers.add(createFixedViewInfo(inflater, R.layout.shopping_list_header));
        ListView.FixedViewInfo footerFixedViewInfo = createFixedViewInfo(inflater, R.layout.shopping_list_footer);
        totalCost = (TextView) footerFixedViewInfo.view.findViewById(R.id.shopping_list_footer_total_cost);
//        totalCost.setText("0.00");
        footers.add(footerFixedViewInfo);
        myListAdapter = new MyListAdapter(items, ShoppingListActivity.this, totalCost);
        adapter = new HeaderViewListAdapter(headers, footers, myListAdapter);

        setListAdapter(adapter);
        ListView listView = getListView();
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                Toast.makeText(getApplicationContext(), adapter.getItem(i).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ListView.FixedViewInfo createFixedViewInfo(final LayoutInflater inflater, final int layout) {
        ListView.FixedViewInfo fvi= getListView().new FixedViewInfo();
        fvi.data = null;
        fvi.isSelectable = false;
        fvi.view = inflater.inflate(layout, null);
        return fvi;
    }

    @SuppressWarnings({"unchecked"}) private void restoreSaveData() {
        Object saved = getLastNonConfigurationInstance();
        if (saved != null) {
             items = (List<ShoppingListItem>) saved;
        } else {
            items = new ArrayList<ShoppingListItem>();
        }
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
        ShoppingListItem shoppingListItem = (ShoppingListItem) intent.getParcelableExtra("ShoppingListItemActivity.shoppingListItem");
        if (shoppingListItem != null) {
            toaster("Got a new intent: " + shoppingListItem.toString());
            myListAdapter.addItem(shoppingListItem);
        } else { /*  We got nothing */ }
    }

    private boolean toaster(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        return true;
    }

    private void showShoppingListItem() {
        startActivity(new Intent(ShoppingListActivity.this, ShoppingListItemActivity.class));
    }

    private static class MyListAdapter extends BaseAdapter {

        private final List<ShoppingListItem> shoppingListItemList;
        private final TextView totalCost;
        private final WeakReference<Context> context;

        private MyListAdapter(List<ShoppingListItem> shoppingListItemList, Context context, TextView totalCost) {
            this.shoppingListItemList = shoppingListItemList;
            this.totalCost = totalCost;
            this.context = new WeakReference<Context>(context);
            updateTotals();
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

        public void addItem(ShoppingListItem shoppingListItem) {
            shoppingListItemList.add(shoppingListItem);
            notifyDataSetChanged();
            updateTotals();
        }

        private void updateTotals() {
            new AsyncTask<Void, Void, Float>() {
                @Override protected Float doInBackground(final Void... voids) { return getTotal(); }
                @Override protected void onPostExecute(final Float total) { totalCost.setText("" + total); }
            }.execute();
        }

        private float getTotal() {
            float total = 0f;
            for (ShoppingListItem sli : shoppingListItemList) {
                total += sli.cost;
            }

            return total;
        }

        @Override public View getView(final int i, final View view, final ViewGroup viewGroup) {
            View inflated;
            if (view == null) {
                LayoutInflater inflater = getLayoutInflator();
                if (inflater != null) {
                    inflated = inflater.inflate(R.layout.shopping_list_line_item2, viewGroup, false);
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

    @Override public Object onRetainNonConfigurationInstance() {
        return items;
    }
}
