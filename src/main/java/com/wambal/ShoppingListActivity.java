/*
 * Copyright 2009 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package com.wambal;

import android.app.Activity;
import android.os.Bundle;

public final class ShoppingListActivity extends Activity {

    @Override protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list);
    }
}
