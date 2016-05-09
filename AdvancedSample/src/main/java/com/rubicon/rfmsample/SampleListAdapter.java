/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */

package com.rubicon.rfmsample;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SampleListAdapter extends ArrayAdapter<Object> {

    private Activity mContext;

    public enum RowType {
        HEADER_ITEM, LIST_ITEM
    }

    public SampleListAdapter(Activity context) {
        super(context, 0, new ArrayList<Object>());
        this.mContext = context;
    }

    @Override
    public int getViewTypeCount() {
        return RowType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        Object currentItem = getItem(position);
        if (currentItem instanceof RFMAd) {
            return RowType.LIST_ITEM.ordinal();
        } else {
            return RowType.HEADER_ITEM.ordinal();
        }
    }

    private static class HeaderHolder {
        public TextView headerTitle;
    }

    private static class ListItemHolder {
        public TextView testCaseName;
        public TextView testDescription;
        public TextView testNumber;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int viewType = this.getItemViewType(position);
        switch (viewType) {
            // header_item view
            case 0:
                HeaderHolder headerHolder;
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.header_item, parent, false);

                    headerHolder = new HeaderHolder();
                    headerHolder.headerTitle = (TextView) v.findViewById(R.id.separator);
                    v.setTag(headerHolder);
                } else {
                    headerHolder = (HeaderHolder) v.getTag();
                }

                // set up the list item
                if (headerHolder.headerTitle != null) {
                    SampleListHeader sampleHeader = (SampleListHeader) getItem(position);
                    headerHolder.headerTitle.setText(sampleHeader.title());
                }

                // return the created view
                return v;

            // ad type list item
            case 1:

                ListItemHolder listItemHolder;
                View v2 = convertView;
                if (v2 == null) {
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v2 = vi.inflate(R.layout.sample_list_item, parent, false);

                    listItemHolder = new ListItemHolder();
                    listItemHolder.testCaseName = (TextView) v2.findViewById(R.id.maintitle);
                    listItemHolder.testDescription = (TextView) v2.findViewById(R.id.description);
                    listItemHolder.testNumber = (TextView) v2.findViewById(R.id.item_number);
                    v2.setTag(listItemHolder);
                } else {
                    listItemHolder = (ListItemHolder) v2.getTag();
                }

                final RFMAd rfmAd = (RFMAd) getItem(position);

                // set up the list item
                if (listItemHolder.testCaseName != null) {
                    listItemHolder.testCaseName.setText(rfmAd.getTestCaseName());
                }
                if (listItemHolder.testDescription != null) {
                    String descriptionText = rfmAd.getAppId();
                    if (!rfmAd.getAdId().isEmpty())
                        descriptionText = rfmAd.getAdId() + " - " + descriptionText;
                    listItemHolder.testDescription.setText(descriptionText);
                }
                if (listItemHolder.testNumber != null) {
                    listItemHolder.testNumber.setText(String.valueOf(rfmAd.getCount()));
                }

                // return the created view
                return v2;
            default:
                return null;
        }
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        int itemType = getItemViewType(position);
        if (itemType == RowType.HEADER_ITEM.ordinal()) {
            return false;
        } else {
            return true;
        }
    }
}
