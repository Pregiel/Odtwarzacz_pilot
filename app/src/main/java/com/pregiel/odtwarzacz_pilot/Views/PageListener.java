package com.pregiel.odtwarzacz_pilot.Views;

import android.support.design.widget.TabLayout;

import com.pregiel.odtwarzacz_pilot.connection.Connection;


public class PageListener extends TabLayout.TabLayoutOnPageChangeListener {
    public PageListener(TabLayout tabLayout) {
        super(tabLayout);
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        System.out.println(position);
        if (position == 2) {

            Connection.sendMessage(Connection.SNAPSHOT_REQUEST);
            Connection.setShowPreview(true);
        } else {

            Connection.setShowPreview(false);
        }
    }
}
