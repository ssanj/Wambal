package com.wambal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.wambal.activity.ManageCategoryActivity;

public class WambalActivity extends Activity {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setTitle(R.string.app_title);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long index) {
                //TODO: Fix!
                startActivity((position == 2) ? new Intent(WambalActivity.this, ManageCategoryActivity.class) :
                        new Intent(WambalActivity.this, CreateBudgetActivity.class));
            }
        });
    }
}
