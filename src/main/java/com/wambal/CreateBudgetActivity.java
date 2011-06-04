/*
 * Copyright 2009 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package com.wambal;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.wambal.cp.WambalContentProvider;

public final class CreateBudgetActivity extends Activity {

    private static class CreatedBudget {
        private String category;
        private String name;
        private float cost;
        private boolean save;

        @Override
        public String toString() {
            return new StringBuilder("Budget [V]").append("\n").
                        append(" category:").append(category).append("\n").
                        append(" name:").append(name).append("\n").
                        append(" cost:").append(cost).append("\n").
                        append(" save:").append(save).append("\n").toString();
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_budget);

        final CreatedBudget createdBudget = new CreatedBudget();

        Spinner category = findTypedViewById(R.id.budgetCategory);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.budget_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(final AdapterView<?> adapterView, final View view, final int pos, final long id) {
                createdBudget.category = adapterView.getItemAtPosition(pos).toString();
            }

            @Override public void onNothingSelected(final AdapterView<?> adapterView) { }
        });

        Button button = findTypedViewById(R.id.saveBudgetButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(final View view) {
                EditText budgetName = findTypedViewById(R.id.budgetName);
                createdBudget.name = budgetName.getText().toString();

                EditText budgetCost = findTypedViewById(R.id.budgetCost);
                createdBudget.cost = Float.parseFloat(budgetCost.getText().toString());

                CheckBox saveBudget = findTypedViewById(R.id.saveBudget);
                createdBudget.save = saveBudget.isChecked();

                insertDataToDatabase(createdBudget);
                Toast.makeText(CreateBudgetActivity.this, createdBudget.toString(), Toast.LENGTH_LONG).show();

                startActivity(new Intent(CreateBudgetActivity.this, ShoppingListActivity.class));
            }
        });
    }

    private void insertDataToDatabase(final CreatedBudget createdBudget) {
        ContentValues values = new ContentValues();
        values.put(WambalContentProvider.Category.NAME, createdBudget.category);
        Uri insert = getContentResolver().insert(WambalContentProvider.CONTENT_URI, values);
        Cursor cursor = getContentResolver().query(WambalContentProvider.CONTENT_URI, new String[]{WambalContentProvider.Category.NAME}, null, null, WambalContentProvider.Category.NAME);
        int nameColumn = cursor.getColumnIndex(WambalContentProvider.Category.NAME);
        StringBuilder sb = new StringBuilder();
        if (cursor.moveToFirst()) {
            do {
                sb.append(cursor.getString(nameColumn)).append(" ");
            } while (cursor.moveToNext());
            Toast.makeText(CreateBudgetActivity.this, sb.toString(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(CreateBudgetActivity.this, "No Results Found", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressWarnings({"unchecked"})
    private <T> T findTypedViewById(int id) {
        return (T) findViewById(id);
    }
}
