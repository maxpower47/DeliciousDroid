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

package com.deliciousdroid.activity;

import com.deliciousdroid.R;
import com.deliciousdroid.Constants;
import com.deliciousdroid.platform.BundleManager;
import com.deliciousdroid.providers.BookmarkContentProvider;
import com.deliciousdroid.providers.BundleContent.Bundle;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;

public class BrowseBundles extends AppBaseListActivity {
		
	private String sortfield = Bundle.Name + " ASC";
	
	@Override
	public void onCreate(android.os.Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browse_bundles);
		
		if(mAccount != null) {
			
			Intent intent = getIntent();
			String action = intent.getAction();
			
			Uri data = getIntent().getData();
			if(data != null) {
				username = data.getUserInfo();
			} else username = mAccount.name;
			
			if(Intent.ACTION_VIEW.equals(action) && data.getLastPathSegment().equals("bookmarks")) {
				Intent i = new Intent();
				i.setAction(Intent.ACTION_VIEW);
				i.addCategory(Intent.CATEGORY_DEFAULT);
				i.setData(data);
				
				startActivity(i);
				finish();			
				
			} else if(Intent.ACTION_SEARCH.equals(action)) {
				if(intent.hasExtra(SearchManager.QUERY)){
					Intent i = new Intent(mContext, MainSearchResults.class);
					i.putExtras(intent.getExtras());
					startActivity(i);
					finish();
				} else {
					onSearchRequested();
				}
	    	}  else if(Intent.ACTION_VIEW.equals(action)) {
				String path = null;
				String tagname = null;

				if(data != null) {
					path = data.getPath();
					tagname = data.getQueryParameter("tagname");
				}

				if(data.getScheme() == null || !data.getScheme().equals("content")){
					Intent i = new Intent(Intent.ACTION_VIEW, data);

					startActivity(i);
					finish();        
				} else if(path.contains("bookmarks") && TextUtils.isDigitsOnly(data.getLastPathSegment())) {
					Intent viewBookmark = new Intent(this, ViewBookmark.class);
					viewBookmark.setData(data);

					Log.d("View Bookmark Uri", data.toString());
					startActivity(viewBookmark);
					finish();
				} else if(tagname != null) {
					Intent viewTags = new Intent(this, BrowseBookmarks.class);
					viewTags.setData(data);

					Log.d("View Tags Uri", data.toString());
					startActivity(viewTags);
					finish();
				}
			} 

			if(mAccount.name.equals(username)){
				try{
					if(Intent.ACTION_VIEW.equals(action)) {
						setTitle("My Bundles");
					} else if(Intent.ACTION_PICK.equals(action)) {
						setTitle("Choose A Bundle For The Folder");
					}
	
					loadBundleList();
	
				} catch(Exception e) {
					
				}
				
			} else {
				try{
					setTitle("Bundles For " + username);
					
					//Cursor c = DeliciousFeed.fetchFriendTags(username);
					//startManagingCursor(c);
					
					//setListAdapter(new SimpleCursorAdapter(this, R.layout.tag_view, c, new String[] {Tag.Name, Tag.Count}, new int[] {R.id.tag_name, R.id.tag_count}));
				}
				catch(Exception e){}
			}
	
			final ListView lv = getListView();
			lv.setTextFilterEnabled(true);
			lv.setFastScrollEnabled(true);
		

			lv.setOnItemClickListener(new OnItemClickListener() {
			    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					final Cursor c = (Cursor)lv.getItemAtPosition(position);
					Bundle b = BundleManager.CursorToBundle(c);
			    	
					Intent i = new Intent();
					i.setAction(Intent.ACTION_VIEW);
					i.addCategory(Intent.CATEGORY_DEFAULT);
	
					Uri.Builder dataBuilder = new Uri.Builder();
					dataBuilder.scheme(Constants.CONTENT_SCHEME);
					dataBuilder.encodedAuthority(username + "@" + BookmarkContentProvider.AUTHORITY);
					dataBuilder.appendEncodedPath("bookmarks");
					dataBuilder.appendQueryParameter("tagname", TextUtils.join(",", b.getTags()));
					dataBuilder.appendQueryParameter("bundlename", b.getName());
					i.setData(dataBuilder.build());
					
					startActivity(i);
			    }
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		
		if(result && isMyself()) {
		    getMenuInflater().inflate(R.menu.browse_bundle_menu, menu);
		}
		
	    return result;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		boolean result = false;
		
	    switch (item.getItemId()) {
		    case R.id.menu_createbundle:
		    	
		        Intent tagIntent = new Intent();
		        tagIntent.setAction(Intent.ACTION_PICK);
		        tagIntent.addCategory(Intent.CATEGORY_DEFAULT);
				Uri.Builder data = new Uri.Builder();
				data.scheme(Constants.CONTENT_SCHEME);
				data.encodedAuthority(mAccount.name + "@" + BookmarkContentProvider.AUTHORITY);
				data.appendEncodedPath("tags");
				data.appendQueryParameter("bundle", "1");
				tagIntent.setData(data.build());
				startActivity(tagIntent);
		    	
				result = true;
				break;
	    }
	    
	    if(result) {
	    } else result = super.onOptionsItemSelected(item);
	    
	    return result;
	}
	
	private void loadBundleList() {
		Cursor c = BundleManager.GetBundles(username, sortfield, this);
		startManagingCursor(c);
		setListAdapter(new SimpleCursorAdapter(this, R.layout.bundle_view, c, new String[] {Bundle.Name}, new int[] {R.id.bundle_name}));
	}
}