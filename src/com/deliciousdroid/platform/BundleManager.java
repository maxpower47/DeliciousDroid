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

package com.deliciousdroid.platform;

import java.util.ArrayList;

import com.deliciousdroid.providers.BookmarkContent.Bookmark;
import com.deliciousdroid.providers.BundleContent.Bundle;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

public class BundleManager {
	
	public static Cursor GetBundles(String account, String sortorder, Context context) {	
		String[] projection = new String[] {Bundle._ID, Bundle.Name, Bundle.Tags};
		String selection = Bundle.Account + "=?";
		String[] selectionargs = new String[]{account};

		return context.getContentResolver().query(Bundle.CONTENT_URI, projection, selection, selectionargs, sortorder);				
	}
	
	public static void AddBundle(Bundle bundle, String account, Context context){
		ContentValues values = new ContentValues();
		
		values.put(Bundle.Name, bundle.getName());
		values.put(Bundle.Tags, bundle.getTagString());
		values.put(Bundle.Account, account);
	
		context.getContentResolver().insert(Bundle.CONTENT_URI, values);
	}
	
	public static void BulkInsert(ArrayList<Bundle> list, String account, Context context){
		int bundlesize = list.size();
		ContentValues[] bcv = new ContentValues[bundlesize];
		
		for(int i = 0; i < bundlesize; i++){
			Bundle b = list.get(i);
			
			ContentValues values = new ContentValues();
			
			values.put(Bundle.Name, b.getName());
			values.put(Bundle.Tags, b.getTagString());
			values.put(Bundle.Account, account);
		
			bcv[i] = values;
		}
		context.getContentResolver().bulkInsert(Bundle.CONTENT_URI, bcv);
	}
	
	public static void UpsertBundle(Bundle bundle, String account, Context context){
		String[] projection = new String[] {Bundle.Name, Bundle.Tags};
		String selection = Bundle.Name + "=? AND " +
			Bundle.Account + "=?";
		String[] selectionargs = new String[]{bundle.getName(), account};
		
		ContentResolver cr = context.getContentResolver();
		Cursor c = cr.query(Bundle.CONTENT_URI, projection, selection, selectionargs, null);
		
		if(c.moveToFirst()){
			UpdateBundle(bundle, account, context);
		} else {
			AddBundle(bundle, account, context);
		}
		c.close();
	}
	
	public static void UpdateBundle(Bundle bundle, String account, Context context){
		
		String selection = Bundle.Name + "=? AND " +
			Bundle.Account + "=?";
		String[] selectionargs = new String[]{bundle.getName(), account};
		
		ContentValues values = new ContentValues();
		
		values.put(Bundle.Tags, bundle.getTagString());
		
		context.getContentResolver().update(Bundle.CONTENT_URI, values, selection, selectionargs);
	}

	public static void DeleteBundle(Bundle bundle, String account, Context context){
		
		String selection = Bundle.Name + "=? AND " +
			Bundle.Account + "=?";
		String[] selectionargs = new String[]{bundle.getName(), account};
		
		context.getContentResolver().delete(Bundle.CONTENT_URI, selection, selectionargs);
	}
	
	public static void TruncateBundles(String account, Context context){
		
		String selection = Bundle.Account + "=?";
		String[] selectionargs = new String[]{account};
		
		context.getContentResolver().delete(Bundle.CONTENT_URI, selection, selectionargs);
	}
	
	public static void TruncateOldBundles(ArrayList<String> accounts, Context context){
		
		ArrayList<String> selectionList = new ArrayList<String>();
		
		for(String s : accounts) {
			selectionList.add(Bundle.Account + " <> '" + s + "'");
		}
		
		String selection = TextUtils.join(" AND ", selectionList);
		
		context.getContentResolver().delete(Bundle.CONTENT_URI, selection, null);
	}
	
	public static Cursor SearchBundles(String query, String username, Context context) {
		String[] projection = new String[] { Bundle._ID, Bundle.Name, Bundle.Tags };
		String selection = null;
		String sortorder = null;
		
		String[] queryBundles = query.split(" ");
		
		ArrayList<String> queryList = new ArrayList<String>();
		final ArrayList<String> selectionlist = new ArrayList<String>();
		
		for(String s : queryBundles) {
			queryList.add(Bundle.Name + " LIKE ?");
		 	selectionlist.add("%" + s + "%");
		}
		selectionlist.add(username);
		
		if(query != null && query != "") {
			selection = "(" + TextUtils.join(" OR ", queryList) + ")" + 
				" AND " + Bundle.Account + "=?";
		} else {
			selection = Bundle.Account + "=?";
		}
		
		sortorder = Bundle.Name + " ASC";
		
		Uri bundles = Bundle.CONTENT_URI;
		
		return context.getContentResolver().query(bundles, projection, selection, selectionlist.toArray(new String[]{}), sortorder);				
	}
	
	public static Bundle CursorToBundle(Cursor c) {
		Bundle b = new Bundle();
		b.setId(c.getInt(c.getColumnIndex(Bookmark._ID)));
		b.setName(c.getString(c.getColumnIndex(Bundle.Name)));
		b.setTagString(c.getString(c.getColumnIndex(Bookmark.Tags)));

		if(c.getColumnIndex(Bookmark.Account) != -1)
			b.setAccount(c.getString(c.getColumnIndex(Bundle.Account)));

		return b;
	}
}