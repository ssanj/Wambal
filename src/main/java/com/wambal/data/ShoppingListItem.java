/*
 * Copyright 2009 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package com.wambal.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ShoppingListItem implements Parcelable {
    public String name = "N/A";
    public float cost = 0.0f;
    public int quantity = 0;
    public boolean save = false;

    public ShoppingListItem() { }

    public ShoppingListItem(final String name, final float cost, final int quantity, final boolean save) {
        this.name = name;
        this.cost = cost;
        this.quantity = quantity;
        this.save = save;
    }

    public ShoppingListItem(final String name, final float cost, final int quantity) {
        this(name, cost, quantity, false);
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(final Parcel parcel, final int i) {
        parcel.writeString(name);
        parcel.writeFloat(cost);
        parcel.writeInt(quantity);
        parcel.writeBooleanArray(new boolean[]{save});
    }

    public static final Creator<ShoppingListItem> CREATOR = new Creator<ShoppingListItem>() {
        @Override public ShoppingListItem createFromParcel(final Parcel parcel) {
            return fromParcel(parcel);
        }

        @Override public ShoppingListItem[] newArray(final int i) {
            return new ShoppingListItem[i];
        }
    };

    private static ShoppingListItem fromParcel(final Parcel parcel) {
        ShoppingListItem item = new ShoppingListItem();
        item.name = parcel.readString();
        item.cost = parcel.readFloat();
        item.quantity = parcel.readInt();
        boolean[] toSave = new boolean[1];
        parcel.readBooleanArray(toSave);
        item.save = toSave[0];
        return item;
    }

    @Override public String toString() {
        return "ShoppingListItem{" +
                "name='" + name + '\'' +
                ", cost=" + cost +
                ", quantity=" + quantity +
                ", save=" + save +
                '}';
    }
}
