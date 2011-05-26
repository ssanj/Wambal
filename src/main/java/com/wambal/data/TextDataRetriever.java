/*
 * Copyright 2009 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package com.wambal.data;


import android.app.Activity;
import android.widget.EditText;

public interface TextDataRetriever {

    String getText(Activity context, int id);
    <T> T findView(Activity context, int id);

    abstract class BaseDataRetriever implements TextDataRetriever {
        @SuppressWarnings({"unchecked"}) public <T> T findView(final Activity context, final int id) {
            return (T) context.findViewById(id);
        }
    }

    final class EditTextDataRetriever extends BaseDataRetriever {

        @Override public String getText(final Activity context, final int id) {
            EditText et = findView(context, id);
            if (et != null) {
                 return et.getText().toString();
            } else {
                return "N/A";
            }
        }
    }
}
