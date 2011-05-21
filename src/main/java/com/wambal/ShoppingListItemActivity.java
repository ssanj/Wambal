/*
 * Copyright 2009 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package com.wambal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public final class ShoppingListItemActivity extends Activity {


    private static class ShoppingListItem {
        private String name = "N/A";
        private float cost = 0.0f;
        private int quantity = 0;
        private boolean save = false;

    }


    @Override protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list_item);

        final ShoppingListItem shoppingListItem = new ShoppingListItem();

        List<String> items = new ArrayList<String>();
        items.add("Milk");
        items.add("Ham");
        items.add("Cheese");
        items.add("Bread");
        items.add("Eggs");

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(ShoppingListItemActivity.this, android.R.layout.simple_dropdown_item_1line, items);
        AutoCompleteTextView itemNameACTV = (AutoCompleteTextView) findViewById(R.id.shopping_list_item_actv);
        itemNameACTV.setAdapter(itemsAdapter);
        itemNameACTV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                shoppingListItem.name = (String) adapterView.getItemAtPosition(i);
            }

            @Override public void onNothingSelected(final AdapterView<?> adapterView) {

            }
        });

        Button button = (Button) findViewById(R.id.shopping_list_item_add_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(final View view) {
                EditText quantityText = (EditText) findViewById(R.id.shopping_list_item_qty_editText);
                shoppingListItem.quantity = Integer.parseInt(quantityText.getText().toString());

                EditText costText = (EditText) findViewById(R.id.shopping_list_item_cost_editText);
                shoppingListItem.cost = Float.parseFloat(costText.getText().toString());


                CheckBox saveItemCheckBox = (CheckBox) findViewById(R.id.shopping_list_item_save_item);
                shoppingListItem.save = saveItemCheckBox.isChecked();

                finish();
                Intent intent = new Intent(ShoppingListItemActivity.this, ShoppingListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Bundle bundle = new Bundle();
                //TODO: Make this a parcable object.
                bundle.putString("shopping.item.name", shoppingListItem.name);
                bundle.putFloat("shopping.item.cost", shoppingListItem.cost);
                bundle.putFloat("shopping.item.quantity", shoppingListItem.quantity);
                bundle.putBoolean("shopping.item.save", shoppingListItem.save);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
