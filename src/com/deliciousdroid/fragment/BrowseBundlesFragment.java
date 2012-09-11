/*
 * DeliciousDroid - http://code.google.com/p/DeliciousDroid/
 *
 * Copyright (C) 2010 Matt Schmidt
 *
 * DeliciousDroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * DeliciousDroid is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DeliciousDroid; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package com.deliciousdroid.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.deliciousdroid.R;
import com.deliciousdroid.platform.BundleManager;
import com.deliciousdroid.providers.BundleContent.Bundle;

public class BrowseBundlesFragment extends ListFragment
	implements LoaderManager.LoaderCallbacks<Cursor> {

	private String sortfield = Bundle.Name + " ASC";
	private SimpleCursorAdapter mAdapter;
	
	private String username = null;
	private String query = null;
	
	private OnBundleSelectedListener bundleSelectedListener;
	private OnItemClickListener clickListener;
	
	public interface OnBundleSelectedListener {
		public void onBundleSelected(String bundle);
	}
	
	@Override
	public void onCreate(android.os.Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		clickListener = viewListener;
	}
	
	@Override
	public void onActivityCreated(android.os.Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);

		setHasOptionsMenu(true);
		
		mAdapter = new SimpleCursorAdapter(this.getActivity(), 
				R.layout.bundle_view, null, 
				new String[] {Bundle.Name, Bundle.Tags}, new int[] {R.id.bundle_name, R.id.bundle_tags}, 0);
		
		setListAdapter(mAdapter);	
		
		getLoaderManager().initLoader(0, null, this);
		
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setFastScrollEnabled(true);
		lv.setOnItemClickListener(clickListener);

		lv.setItemsCanFocus(false);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}
	
	public void setAccount(String account) {
		this.username = account;
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	private OnItemClickListener viewListener = new OnItemClickListener() {
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    	String tags = ((TextView)view.findViewById(R.id.bundle_tags)).getText().toString();
	    	tags = TextUtils.join(",", tags.split(" "));
	    	bundleSelectedListener.onBundleSelected(tags);
	    }
	};
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.browse_bundle_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	
		boolean result = false;

	    switch (item.getItemId()) {
		    case R.id.menu_bundle_sort_name_asc:
		    	sortfield = Bundle.Name + " ASC";
				result = true;
				break;
		    case R.id.menu_bundle_sort_name_desc:			
		    	sortfield = Bundle.Name + " DESC";
		    	result = true;
		    	break;
	    }
	    
	    if(result) {
	    	getLoaderManager().restartLoader(0, null, this);
	    } else result = super.onOptionsItemSelected(item);
	    
	    return result;
	}
	
	public Loader<Cursor> onCreateLoader(int id, android.os.Bundle args) {
		if(username != null && !username.equals("")) {
			if(query != null) {
				return BundleManager.SearchBundles(query, username, this.getActivity());
			} else {
				return BundleManager.GetBundles(username, sortfield, this.getActivity());
			}
		}
		else return null;
	}
	
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
	    mAdapter.swapCursor(data);
	}
	
	public void onLoaderReset(Loader<Cursor> loader) {
	    mAdapter.swapCursor(null);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			bundleSelectedListener = (OnBundleSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnBundleSelectedListener");
		}
	}
}