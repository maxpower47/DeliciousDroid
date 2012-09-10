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

package com.deliciousdroid.action;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.deliciousdroid.activity.AddBookmark;
import com.deliciousdroid.activity.BrowseBookmarks;
import com.deliciousdroid.activity.BrowseTags;
import com.deliciousdroid.activity.ViewBookmark;
import com.deliciousdroid.Constants;
import com.deliciousdroid.Constants.BookmarkViewType;
import com.deliciousdroid.providers.BookmarkContent.Bookmark;
import com.deliciousdroid.providers.BookmarkContentProvider;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class IntentHelper {

	public static Intent OpenInBrowser(String url){
    	Uri link = Uri.parse(url);
		return new Intent(Intent.ACTION_VIEW, link);
	}
	
	public static Intent ReadBookmark(String url){
    	String readUrl = "";
		try {
			readUrl = Constants.TEXT_EXTRACTOR_URL + URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	Uri readLink = Uri.parse(readUrl);
		return new Intent(Intent.ACTION_VIEW, readLink);
	}
	
	public static Intent SendBookmark(String url, String title) {
    	Intent sendIntent = new Intent(Intent.ACTION_SEND);
    	sendIntent.setType("text/plain");
    	sendIntent.putExtra(Intent.EXTRA_TEXT, url);
    	sendIntent.putExtra(Intent.EXTRA_SUBJECT, title);
    	sendIntent.putExtra(Intent.EXTRA_TITLE, title);
    	
    	return sendIntent;
	}
	
	public static Intent AddBookmark(String url, String account, Context context) {
		Intent addBookmark = new Intent(context, AddBookmark.class);
		addBookmark.setAction(Intent.ACTION_SEND);
		if(url != null)
			addBookmark.putExtra(Intent.EXTRA_TEXT, url);
		
		Uri.Builder data = new Uri.Builder();
		data.scheme(Constants.CONTENT_SCHEME);
		data.encodedAuthority(account + "@" + BookmarkContentProvider.AUTHORITY);
		data.appendEncodedPath("bookmarks");
		addBookmark.setData(data.build());
		
		return addBookmark;
	}
	
	public static Intent ViewBookmark(Bookmark b, BookmarkViewType type, String account, Context context) {
		Intent viewBookmark = new Intent(context, ViewBookmark.class);
		viewBookmark.setAction(Intent.ACTION_VIEW);
		viewBookmark.addCategory(Intent.CATEGORY_DEFAULT);
		viewBookmark.putExtra("com.deliciousdroid.BookmarkViewType", type);
		Uri.Builder data = new Uri.Builder();
		data.scheme(Constants.CONTENT_SCHEME);
		data.encodedAuthority(account + "@" + BookmarkContentProvider.AUTHORITY);
		data.appendEncodedPath("bookmarks");
		
		if(b.getId() != 0) {
			data.appendEncodedPath(Integer.toString(b.getId()));
		} else {
			data.appendEncodedPath(Integer.toString(0));
			data.appendQueryParameter("url", b.getUrl());
			data.appendQueryParameter("title", b.getDescription());
			data.appendQueryParameter("notes", b.getNotes());
			data.appendQueryParameter("tags", b.getTagString());
			data.appendQueryParameter("time", Long.toString(b.getTime()));
			data.appendQueryParameter("account", b.getAccount());
		}
		viewBookmark.setData(data.build());
		
		return viewBookmark;
	}
	
	public static Intent EditBookmark(Bookmark b, String account, Context context) {
		Intent editBookmark = new Intent(context, AddBookmark.class);
		editBookmark.setAction(Intent.ACTION_EDIT);
		Uri.Builder data = new Uri.Builder();
		data.scheme(Constants.CONTENT_SCHEME);
		data.encodedAuthority(account + "@" + BookmarkContentProvider.AUTHORITY);
		data.appendEncodedPath("bookmarks");
		data.appendEncodedPath(Integer.toString(b.getId()));
		editBookmark.setData(data.build());
		
		return editBookmark;
	}
	
	public static Intent ViewBookmarks(String tag, String account, Context context) {
		Intent i = new Intent(context, BrowseBookmarks.class);
		i.setAction(Intent.ACTION_VIEW);
		i.addCategory(Intent.CATEGORY_DEFAULT);
		Uri.Builder data = new Uri.Builder();
		data.scheme(Constants.CONTENT_SCHEME);
		data.encodedAuthority(account + "@" + BookmarkContentProvider.AUTHORITY);
		data.appendEncodedPath("bookmarks");
		
		if(tag != null && !tag.equals(""))
			data.appendQueryParameter("tagname", tag);
		
		i.setData(data.build());
		
		return i;
	}
	
	public static Intent ViewUnread(String account, Context context) {
		Intent i = new Intent(context, BrowseBookmarks.class);
		i.setAction(Intent.ACTION_VIEW);
		i.addCategory(Intent.CATEGORY_DEFAULT);
		Uri.Builder data = new Uri.Builder();
		data.scheme(Constants.CONTENT_SCHEME);
		data.encodedAuthority(account + "@" + BookmarkContentProvider.AUTHORITY);
		data.appendEncodedPath("bookmarks");
		data.appendQueryParameter("unread", "1");
		i.setData(data.build());
		
		return i;
	}
	
	public static Intent ViewTags(String account, Context context) {
		Intent i = new Intent(context, BrowseTags.class);
		i.setAction(Intent.ACTION_VIEW);
		i.addCategory(Intent.CATEGORY_DEFAULT);
		Uri.Builder data = new Uri.Builder();
		data.scheme(Constants.CONTENT_SCHEME);
		data.encodedAuthority(account + "@" + BookmarkContentProvider.AUTHORITY);
		data.appendEncodedPath("tags");
		i.setData(data.build());
		
		return i;
	}
	
	public static Intent ViewTabletTags(String account, Context context) {
		Intent i = new Intent(context, BrowseBookmarks.class);
		i.setAction(Intent.ACTION_VIEW);
		i.addCategory(Intent.CATEGORY_DEFAULT);
		Uri.Builder data = new Uri.Builder();
		data.scheme(Constants.CONTENT_SCHEME);
		data.encodedAuthority(account + "@" + BookmarkContentProvider.AUTHORITY);
		data.appendEncodedPath("tags");
		i.setData(data.build());
		
		return i;
	}
	
	public static Intent SearchBookmarks(String query, String account, Context context) {
		Intent i = new Intent(context, BrowseBookmarks.class);
		i.setAction(Intent.ACTION_SEARCH);
		i.putExtra(SearchManager.QUERY, query);
		i.putExtra("MainSearchResults", "1");
		return i;
	}
	
	public static Intent SearchTags(String query, String account, Context context) {
		Intent i = new Intent(context, BrowseTags.class);
		i.setAction(Intent.ACTION_SEARCH);
		i.putExtra(SearchManager.QUERY, query);
		i.putExtra("MainSearchResults", "1");
		return i;
	}
	
	public static Intent SearchGlobalTags(String query, String account, Context context) {
		Intent i = new Intent(context, BrowseBookmarks.class);
		i.setAction(Intent.ACTION_SEARCH);
		i.putExtra(SearchManager.QUERY, query);
		i.putExtra("MainSearchResults", "1");
		
		Uri.Builder data = new Uri.Builder();
		data.scheme(Constants.CONTENT_SCHEME);
		data.encodedAuthority("global" + "@" + BookmarkContentProvider.AUTHORITY);
		data.appendEncodedPath("bookmarks");
		i.setData(data.build());
		
		return i;
	}
}
