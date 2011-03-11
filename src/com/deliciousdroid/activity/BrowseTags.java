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

import java.util.ArrayList;

import com.deliciousdroid.R;
import com.deliciousdroid.Constants;
import com.deliciousdroid.action.BundleTaskArgs;
import com.deliciousdroid.client.DeliciousApi;
import com.deliciousdroid.client.DeliciousFeed;
import com.deliciousdroid.platform.BookmarkManager;
import com.deliciousdroid.platform.BundleManager;
import com.deliciousdroid.platform.TagManager;
import com.deliciousdroid.providers.BookmarkContentProvider;
import com.deliciousdroid.providers.BookmarkContent.Bookmark;
import com.deliciousdroid.providers.BundleContent.Bundle;
import com.deliciousdroid.providers.TagContent.Tag;

import android.accounts.Account;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.*;

public class BrowseTags extends AppBaseListActivity {
		
	private String sortfield = Tag.Name + " ASC";
	private boolean createBundle = false;
	private ListView lv;
	
	@Override
	public void onCreate(android.os.Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		
		if(mAccount != null) {
			
			Intent intent = getIntent();
			String action = intent.getAction();
			
			Uri data = getIntent().getData();
			if(data != null) {
				username = data.getUserInfo();
			} else username = mAccount.name;
			
			lv = getListView();
			lv.setTextFilterEnabled(true);
			lv.setFastScrollEnabled(true);
			
			createBundle = (action != null && action.equals(Intent.ACTION_PICK) && data.getQueryParameter("bundle") != null);
			
			if(Intent.ACTION_VIEW.equals(action) && data.getLastPathSegment().equals("bookmarks")) {
				Intent i = new Intent();
				i.setAction(Intent.ACTION_VIEW);
				i.addCategory(Intent.CATEGORY_DEFAULT);
				i.setData(data);
				
				startActivity(i);
				finish();			
				
			} else if(Intent.ACTION_SEARCH.equals(action)) {
				setContentView(R.layout.browse_tags);
	  		
	    		String query = intent.getStringExtra(SearchManager.QUERY);
	    		
	    		setTitle("Tag Search Results For \"" + query + "\"");
	    		
	    		Cursor c = TagManager.SearchTags(query, username, this);
	    		startManagingCursor(c);
	    		
	    		setListAdapter(new SimpleCursorAdapter(this, R.layout.tag_view, c, new String[] {Tag.Name, Tag.Count}, new int[] {R.id.tag_name, R.id.tag_count}));
	    		
	    	}  else if(createBundle) {
				Cursor c = TagManager.GetTags(username, sortfield, this);
				startManagingCursor(c);
				
				
				setListAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_multiple_choice, c, new String[] {Tag.Name}, new int[] {android.R.id.text1}));
				
				lv.setItemsCanFocus(false);
				lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				
			} else if(mAccount.name.equals(username)){
				setContentView(R.layout.browse_tags);
				try{
					if(Intent.ACTION_VIEW.equals(action)) {
						setTitle("My Tags");
					} else if(Intent.ACTION_PICK.equals(action)) {
						setTitle("Choose A Tag For The Folder");
					}
	
					loadTagList();
	
				} catch(Exception e) {
					
				}
				
			} else {
				setContentView(R.layout.browse_tags);
				try{
					setTitle("Tags For " + username);
					
					Cursor c = DeliciousFeed.fetchFriendTags(username);
					startManagingCursor(c);
					
					setListAdapter(new SimpleCursorAdapter(this, R.layout.tag_view, c, new String[] {Tag.Name, Tag.Count}, new int[] {R.id.tag_name, R.id.tag_count}));
				}
				catch(Exception e){}
			}
	
			if(action != null && action.equals(Intent.ACTION_PICK) && data.getQueryParameter("livefolder") != null) {
				
				lv.setOnItemClickListener(new OnItemClickListener() {
				    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				    	String tagName = ((TextView)view.findViewById(R.id.tag_name)).getText().toString();
				    	
				    	Intent i = new Intent();
				    	i.putExtra("tagname", tagName);
				    	
						setResult(RESULT_OK, i);
						finish();
				    }
				});
				
			} else if(!action.equals(Intent.ACTION_PICK)) {
				lv.setOnItemClickListener(new OnItemClickListener() {
				    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				    	String tagName = ((TextView)view.findViewById(R.id.tag_name)).getText().toString();
				    	
						Intent i = new Intent();
						i.setAction(Intent.ACTION_VIEW);
						i.addCategory(Intent.CATEGORY_DEFAULT);
		
						Uri.Builder dataBuilder = new Uri.Builder();
						dataBuilder.scheme(Constants.CONTENT_SCHEME);
						dataBuilder.encodedAuthority(username + "@" + BookmarkContentProvider.AUTHORITY);
						dataBuilder.appendEncodedPath("bookmarks");
						dataBuilder.appendQueryParameter("tagname", tagName);
						i.setData(dataBuilder.build());
						
						startActivity(i);
				    }
				});
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		
		if(result && isMyself() && !createBundle) {
		    getMenuInflater().inflate(R.menu.browse_tag_menu, menu);
		} else if(result && createBundle) {
			getMenuInflater().inflate(R.menu.browse_tag_createbundle_menu, menu);
		}
		
	    return result;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		boolean result = false;
		
	    switch (item.getItemId()) {
		    case R.id.menu_tag_sort_name_asc:
		    	sortfield = Tag.Name + " ASC";
				result = true;
				break;
		    case R.id.menu_tag_sort_name_desc:			
		    	sortfield = Tag.Name + " DESC";
		    	result = true;
		    	break;
		    case R.id.menu_tag_sort_count_asc:			
		    	sortfield = Tag.Count + " ASC";
		    	result = true;
		    	break;
		    case R.id.menu_tag_sort_count_desc:			
		    	sortfield = Tag.Count + " DESC";
		    	result = true;
		    	break;
		    case R.id.menu_savebundle:
		    	bundlePopup();
		    	
		    	result = true;
		    	break;
	    }
	    
	    if(result) {
	    	loadTagList();
	    } else result = super.onOptionsItemSelected(item);
	    
	    return result;
	}
	
	private void bundlePopup(){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Title");
		alert.setMessage("Message");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				
				int count = lv.getAdapter().getCount();
				
				ArrayList<String> tags = new ArrayList<String>();
				
				for(int i = 0; i < count; i++) {
					if(lv.isItemChecked(i)) {
						final Cursor c = (Cursor)lv.getItemAtPosition(i);
						Tag t = TagManager.CursorToTag(c);
						tags.add(t.getTagName());
					}
				}
				

				Bundle b = new Bundle();
				b.setName(value);
				b.setTagString(TextUtils.join(" ", tags));
				b.setAccount(mAccount.name);
		  
				BundleTaskArgs args = new BundleTaskArgs(b, mAccount, mContext);
			
				new CreateBundleTask().execute(args);
		  	}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    
		  }
		});

		alert.show();
	}
	
	private void loadTagList() {
		Cursor c = TagManager.GetTags(username, sortfield, this);
		startManagingCursor(c);
		setListAdapter(new SimpleCursorAdapter(this, R.layout.tag_view, c, new String[] {Tag.Name, Tag.Count}, new int[] {R.id.tag_name, R.id.tag_count}));
	}
	
    private class CreateBundleTask extends AsyncTask<BundleTaskArgs, Integer, Boolean>{
    	private Context context;
    	private Bundle bundle;
    	private Account account;
    	private ProgressDialog progress;
    	
    	@Override
    	protected Boolean doInBackground(BundleTaskArgs... args) {
    		context = args[0].getContext();
    		bundle = args[0].getBundle();
    		account = args[0].getAccount();
    		
    		try {
    			Boolean success = DeliciousApi.createBundle(bundle, account, context);
    			if(success){
    				BundleManager.AddBundle(bundle, account.name, context);
    				return true;
    			} else return false;
    		} catch (Exception e) {
    			Log.d("addBookmark error", e.toString());
    			return false;
    		}
    	}
    	
        protected void onPreExecute() {
	        progress = new ProgressDialog(mContext);
	        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        progress.setMessage("Working...");
	        progress.setCancelable(true);
	        progress.show();
        }

        protected void onPostExecute(Boolean result) {
        	progress.dismiss();

    			
    		Toast.makeText(context, "Bundle Created", Toast.LENGTH_SHORT).show();

    		
    		finish();
        }
    }
}