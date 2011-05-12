/*
 * Copyright 2009 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package com.example;

import android.widget.TextView;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class HelloWorldActivityTest {

    private HelloWorldActivity activity;
    private TextView tv;

    @Before public void setUp() {
        activity = new HelloWorldActivity();
        activity.onCreate(null);
        tv = (TextView) activity.findViewById(R.id.HelloWorldTextView);
    }

    @Test public void shouldHaveTheCorrectText() {
        assertThat(tv.getText(), equalTo((CharSequence) "Hello World, from Android OS! :)"));
    }
}
