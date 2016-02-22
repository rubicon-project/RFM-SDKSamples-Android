/*
 * Copyright (C) 2012 Rubicon Project. All rights reserved
 * 
 * @author: Rubicon Project.
 * 
 */


package com.rubicon.rfmsample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Class built on base adapter for providing data to be displayed in list items.
 * Each list item view has a title and description for the demo test case.
 */
public class ListViewAdapter extends BaseAdapter {

	Activity context;
	String title[];
	String description[];

	public ListViewAdapter(Activity context, String[] title, String[] description) {
		super();
		this.context = context;
		this.title = title;
		this.description = description;
	}
	
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return title.length;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int arg0) {
		return null;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return 0;
	}

	
	private class ListItemViewHolder {
        TextView txtViewTitle;
        TextView txtViewDescription;
	}
	
	
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemViewHolder viewHolder;
		LayoutInflater inflater =  context.getLayoutInflater();

		if (convertView == null) {
			//if old view does not exist, create a new instance of the holder 
			convertView = inflater.inflate(R.layout.list_item, null);
			viewHolder = new ListItemViewHolder();
			viewHolder.txtViewTitle = (TextView) convertView.findViewById(R.id.maintitle);
			viewHolder.txtViewDescription = (TextView) convertView.findViewById(R.id.description);
			convertView.setTag(viewHolder);
		} else {
			// reuse existing view
			viewHolder = (ListItemViewHolder) convertView.getTag();
		}

		//set title and description for the list item view
		viewHolder.txtViewTitle.setText(title[position]);
		viewHolder.txtViewDescription.setText(description[position]);
		//viewHolder.txtViewDescription.setHeight(200);
		viewHolder.txtViewDescription.setMinHeight(100);
		
		return convertView;
	}

}
