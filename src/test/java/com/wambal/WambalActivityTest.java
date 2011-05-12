/*
 * Copyright 2009 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package com.wambal;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(RobolectricTestRunner.class)
public class WambalActivityTest {

    private WambalActivity activity;

    @Before public void setUp() {
        activity = new WambalActivity();
        activity.onCreate(null);
    }

    @Test public void shouldTestSomething() {

    }
}
