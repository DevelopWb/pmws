package com.softconfig.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.softconfig.R;

public class MainActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initHost();
    }
    private void initHost() {
        final TabHost host = getTabHost();
        TabHost.TabSpec tab1 = host.newTabSpec("tab1");
        TabHost.TabSpec tab2 = host.newTabSpec("tab2");
        TabHost.TabSpec tab3 = host.newTabSpec("tab3");
        TabHost.TabSpec tab4 = host.newTabSpec("tab4");
        TabHost.TabSpec tab5 = host.newTabSpec("tab5");

        View view1 = LayoutInflater.from(this).inflate(R.layout.tab, null);
//        final ImageView iv1 = (ImageView) view1.findViewById(R.id.image_iv);
//        iv1.setBackgroundResource(R.drawable.cell_search_press);
        final TextView tv1 = (TextView) view1.findViewById(R.id.textview_iv);
        tv1.setText("屏幕卫士");
        tv1.setTextColor(getResources().getColor(R.color.blue_text));
        Intent intent1 = new Intent(this, PmwsConfigActivity.class);
        tab1.setIndicator(view1).setContent(intent1);
        host.addTab(tab1);


        View view2 = LayoutInflater.from(this).inflate(R.layout.tab, null);
//        final ImageView iv2 = (ImageView) view2.findViewById(R.id.image_iv);
//        iv2.setBackgroundResource(R.drawable.cell_location_normal);
        final  TextView tv2 = (TextView) view2.findViewById(R.id.textview_iv);
        tv2.setText("侦查终端");
        tv2.setTextColor(getResources().getColor(R.color.white));
        final Intent intent2 = new Intent(this, InvestigationTerminalActivity.class);
        tab2.setIndicator(view2).setContent(intent2);
        host.addTab(tab2);

        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals("tab1")) {
                    tv1.setTextColor(getResources().getColor(R.color.blue_text));
                    tv2.setTextColor(getResources().getColor(R.color.white));
                } else if (tabId.equals("tab2")) {
                    tv2.setTextColor(getResources().getColor(R.color.blue_text));
                    tv1.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

    }
}
