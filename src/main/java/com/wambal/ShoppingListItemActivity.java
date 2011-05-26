/*
 * Copyright 2009 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package com.wambal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import com.wambal.data.ShoppingListItem;
import com.wambal.data.TextDataRetriever;

import java.util.ArrayList;
import java.util.List;

public final class ShoppingListItemActivity extends Activity {

    private final TextDataRetriever.EditTextDataRetriever editTextDataRetriever = new TextDataRetriever.EditTextDataRetriever();

    @Override protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list_item);

        final ShoppingListItem shoppingListItem = new ShoppingListItem();
        final AutoCompleteTextView itemNameACTV = createItemNameDropDown();
        createAddButton(shoppingListItem, itemNameACTV);
    }

    private AutoCompleteTextView createItemNameDropDown() {
        List<String> items = new ArrayList<String>();
        items.add("Milk");
        items.add("Ham");
        items.add("Cheese");
        items.add("Bread");
        items.add("Eggs");

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(ShoppingListItemActivity.this, android.R.layout.simple_dropdown_item_1line, items);
        final AutoCompleteTextView itemNameACTV = (AutoCompleteTextView) findViewById(R.id.shopping_list_item_actv);
        itemNameACTV.setAdapter(itemsAdapter);
        return itemNameACTV;
    }

    private void createAddButton(final ShoppingListItem shoppingListItem, final AutoCompleteTextView itemNameACTV) {
        Button button = (Button) findViewById(R.id.shopping_list_item_add_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(final View view) {
                shoppingListItem.quantity = Integer.parseInt(editTextDataRetriever.getText(ShoppingListItemActivity.this,
                        R.id.shopping_list_item_qty_editText));
                shoppingListItem.cost = Float.parseFloat(editTextDataRetriever.getText(ShoppingListItemActivity.this,
                        R.id.shopping_list_item_cost_editText));
                shoppingListItem.save = editTextDataRetriever.<CheckBox>findView(ShoppingListItemActivity.this,
                        R.id.shopping_list_item_save_item).isChecked();
                shoppingListItem.name = itemNameACTV.getEditableText().toString();

                startShoppingListActivity(shoppingListItem);
            }
        });
    }

    private void startShoppingListActivity(final ShoppingListItem shoppingListItem) {
        Intent intent = new Intent(this, ShoppingListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putParcelable("ShoppingListItemActivity.shoppingListItem", shoppingListItem);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
