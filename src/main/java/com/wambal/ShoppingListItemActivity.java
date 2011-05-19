/*
 * Copyright 2009 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package com.wambal;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public final class ShoppingListItemActivity extends Activity {

    @Override protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list_item);

        List<String> items = new ArrayList<String>();
        items.add("Milk");
        items.add("Ham");
        items.add("Cheese");
        items.add("Bread");
        items.add("Eggs");

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(ShoppingListItemActivity.this, android.R.layout.simple_dropdown_item_1line, items);
        AutoCompleteTextView itemNameACTV = (AutoCompleteTextView) findViewById(R.id.shopping_list_item_actv);
        itemNameACTV.setAdapter(itemsAdapter);

        Button button = (Button) findViewById(R.id.shopping_list_item_add_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(final View view) {
                finish();
            }
        });
    }
}
