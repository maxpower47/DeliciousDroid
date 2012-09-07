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

package com.deliciousdroid.client;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.deliciousdroid.providers.TagContent.Tag;
import com.deliciousdroid.client.HttpClientFactory;
import com.deliciousdroid.xml.SaxFeedParser;

import android.accounts.Account;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;

public class DeliciousFeed {
    private static final String TAG = "DeliciousFeed";
    public static final String USER_AGENT = "AuthenticationService/1.0";
    public static final int REGISTRATION_TIMEOUT = 30 * 1000; // ms

    public static final String FETCH_FRIEND_UPDATES_URI = "http://feeds.delicious.com/v2/json/networkmembers/";
    public static final String FETCH_FRIEND_BOOKMARKS_URI = "http://feeds.delicious.com/v2/rss/";
    public static final String FETCH_NETWORK_RECENT_BOOKMARKS_URI = "http://feeds.delicious.com/v2/rss/network/";
    public static final String FETCH_RECENT_BOOKMARKS_URI = "http://feeds.delicious.com/v2/rss/recent";
    public static final String FETCH_STATUS_URI = "http://feeds.delicious.com/v2/json/network/";
    public static final String FETCH_TAGS_URI = "http://feeds.delicious.com/v2/json/tags/";
    public static final String FETCH_BOOKMARKS_URI = "http://feeds.delicious.com/v2/json/";
	
    /**
     * Retrieves a list of contacts in a users network.
     * 
     * @param account The account being synced.
     * @return The list of contacts received from the server.
     * @throws JSONException If an error was encountered in deserializing the JSON object returned from 
     * the server.
     * @throws IOException If a server error was encountered.
     * @throws AuthenticationException If an authentication error was encountered.
     */
    public static List<User> fetchFriendUpdates(Account account) 
    	throws JSONException, IOException, AuthenticationException, FeedForbiddenException {
        final ArrayList<User> friendList = new ArrayList<User>();

        final HttpGet post = new HttpGet(FETCH_FRIEND_UPDATES_URI + account.name);

        final HttpResponse resp = HttpClientFactory.getThreadSafeClient().execute(post);
        final String response = EntityUtils.toString(resp.getEntity());

        if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

            final JSONArray friends = new JSONArray(response);
            Log.d(TAG, response);
            for (int i = 0; i < friends.length(); i++) {
                friendList.add(User.valueOf(friends.getJSONObject(i)));
            }
        } else {
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
                Log.e(TAG, "Authentication exception in fetching remote contacts");
                throw new AuthenticationException();
            }  else if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN) {
            	Log.e(TAG, "Fetching contact updates forbidden");
            	throw new FeedForbiddenException();
            } else {
                Log.e(TAG, "Server error in fetching remote contacts: "
                    + resp.getStatusLine());
                throw new IOException();
            }
        }
        return friendList;
    }
    
    /**
     * Retrieves a list of bookmark updates for contacts in a users network.
     * 
     * @param account The account being synced.
     * @return The list of bookmark updates received from the server.
     * @throws JSONException If an error was encountered in deserializing the JSON object returned from 
     * the server.
     * @throws IOException If a server error was encountered.
     * @throws AuthenticationException If an authentication error was encountered.
     */
    public static List<User.Status> fetchFriendStatuses(Account account) 
    	throws JSONException, IOException, AuthenticationException {
        final ArrayList<User.Status> statusList = new ArrayList<User.Status>();

        final HttpGet post = new HttpGet(FETCH_STATUS_URI + account.name + "?count=15");

        final HttpResponse resp = HttpClientFactory.getThreadSafeClient().execute(post);
        final String response = EntityUtils.toString(resp.getEntity());

        if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

            final JSONArray statuses = new JSONArray(response);
            Log.d(TAG, response);
            for (int i = 0; i < statuses.length(); i++) {
                statusList.add(User.Status.valueOf(statuses.getJSONObject(i)));
            }
        } else {
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
                Log.e(TAG, "Authentication exception in fetching friend status list");
                throw new AuthenticationException();
            } else {
                Log.e(TAG, "Server error in fetching friend status list");
                throw new IOException();
            }
        }
        return statusList;
    }
    
    public static List<User.Status> fetchFriendStatuses(String friendUsername) 
        	throws JSONException, IOException, AuthenticationException {
            final ArrayList<User.Status> statusList = new ArrayList<User.Status>();

            final HttpGet post = new HttpGet(FETCH_BOOKMARKS_URI + friendUsername + "?count=5");

            final HttpResponse resp = HttpClientFactory.getThreadSafeClient().execute(post);
            final String response = EntityUtils.toString(resp.getEntity());

            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                final JSONArray statuses = new JSONArray(response);
                Log.d(TAG, response);
                for (int i = 0; i < statuses.length(); i++) {
                    statusList.add(User.Status.valueOf(statuses.getJSONObject(i)));
                }
            } else {
                if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
                    Log.e(TAG, "Authentication exception in fetching friend status list");
                    throw new AuthenticationException();
                } else {
                    Log.e(TAG, "Server error in fetching friend status list");
                    throw new IOException();
                }
            }
            return statusList;
        }
    
    /**
     * Retrieves a list of tags for a Delicious user.
     * 
     * @param username Username of the Delicious user.
     * @return The list of tags received from the server.
     * @throws JSONException If an error was encountered in deserializing the JSON object returned from 
     * the server.
     * @throws IOException If a server error was encountered.
     * @throws AuthenticationException If an authentication error was encountered.
     */
    public static Cursor fetchFriendTags(String username) 
    	throws JSONException, IOException, AuthenticationException {
    	
        final HttpGet post = new HttpGet(FETCH_TAGS_URI + username + "?count=100");
        
        final MatrixCursor tagCursor = new MatrixCursor(new String[] {Tag._ID, Tag.Name, 
    	        Tag.Count});

        final HttpResponse resp = HttpClientFactory.getThreadSafeClient().execute(post);
        final String response = EntityUtils.toString(resp.getEntity());

        if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

            final JSONObject tags = new JSONObject(response);
            Iterator<?> i = tags.keys();
            while(i.hasNext()){
            	Object e = i.next();
            	
            	tagCursor.addRow(new Object[]{0, e.toString(), tags.getInt(e.toString())});
            }
            
            Log.d(TAG, response);

        } else {
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
                Log.e(TAG, "Authentication exception in fetching friend status list");
                throw new AuthenticationException();
            } else {
                Log.e(TAG, "Server error in fetching friend status list");
                throw new IOException();
            }
        }
        return tagCursor;
    }
    
    /**
     * Retrieves a list of bookmarks for a Delicious user.
     * 
     * @param username Username of the Delicious user.
     * @param tagName If specified, retrieves only bookmarks for a particular tag.
     * @param limit The number of bookmarks to retrieve, maximum of 100.
     * @return The list of bookmarks received from the server.
     * @throws JSONException If an error was encountered in deserializing the JSON object returned from 
     * the server.
     * @throws IOException If a server error was encountered.
     * @throws AuthenticationException If an authentication error was encountered.
     */
    public static Cursor fetchUserRecent(String username, String tagName)
    	throws IOException,  ParseException {
    	
    	String url = FETCH_FRIEND_BOOKMARKS_URI + username;
    	
    	if(tagName != null && tagName != "")
    		url += "/" + tagName;
    	url += "?count=100";
    	
    	Log.d("fetchUserRecent", url);
    	
        final HttpGet post = new HttpGet(url);
        
        Cursor bookmarkList = null;

        final HttpResponse resp = HttpClientFactory.getThreadSafeClient().execute(post);
        InputStream responseStream = resp.getEntity().getContent();

        if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

        	SaxFeedParser parser = new SaxFeedParser(responseStream);

			bookmarkList = parser.parse();
        } else {
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
                Log.e(TAG, "Authentication exception in fetching friend status list");
                //throw new AuthenticationException();
            } else {
                Log.e(TAG, "Server error in fetching friend status list");
                throw new IOException();
            }
        }
        return bookmarkList;
    }
    
    /**
     * Retrieves a list of recent bookmarks for a Delcious user's network.
     * 
     * @param username Username of the Delicious user.
     * @param limit The number of bookmarks to retrieve, maximum of 100.
     * @return The list of bookmarks received from the server.
     * @throws JSONException If an error was encountered in deserializing the JSON object returned from 
     * the server.
     * @throws IOException If a server error was encountered.
     * @throws AuthenticationException If an authentication error was encountered.
     */
    public static Cursor fetchNetworkRecent(String userName)
    	throws IOException, ParseException {

        final HttpGet post = new HttpGet(FETCH_NETWORK_RECENT_BOOKMARKS_URI + userName + "?count=100");
        
        Cursor bookmarkList = null;

        final HttpResponse resp = HttpClientFactory.getThreadSafeClient().execute(post);
        InputStream responseStream = resp.getEntity().getContent();

        if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        	SaxFeedParser parser = new SaxFeedParser(responseStream);

			bookmarkList = parser.parse();

        } else {
        	Log.e(TAG, "Server error in fetching network recent list");
            throw new IOException();
        }
        
        return bookmarkList;
    }
    
    /**
     * Retrieves a list of popular bookmarks from Delicious.
     * 
     * @param limit The number of bookmarks to retrieve, maximum of 100.
     * @return The list of bookmarks received from the server.
     * @throws JSONException If an error was encountered in deserializing the JSON object returned from 
     * the server.
     * @throws IOException If a server error was encountered.
     * @throws AuthenticationException If an authentication error was encountered.
     */
    public static Cursor fetchRecent()
    	throws IOException, ParseException {

        final HttpGet post = new HttpGet(FETCH_RECENT_BOOKMARKS_URI + "?count=100");
        
        Cursor bookmarkList = null;

        final HttpResponse resp = HttpClientFactory.getThreadSafeClient().execute(post);
        InputStream responseStream = resp.getEntity().getContent();

        if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        	SaxFeedParser parser = new SaxFeedParser(responseStream);

			bookmarkList = parser.parse();

        } else {
        	Log.e(TAG, "Server error in fetching network recent list");
            throw new IOException();
        }
        
        return bookmarkList;
    }
}