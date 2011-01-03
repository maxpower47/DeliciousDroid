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

package com.deliciousdroid.providers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.deliciousdroid.providers.TagContent.Tag;
import com.deliciousdroid.util.DateParser;

import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class BookmarkContent {

	public static class Bookmark implements BaseColumns {
		public static final Uri CONTENT_URI = Uri.parse("content://" + 
				BookmarkContentProvider.AUTHORITY + "/bookmark");
		
		public static final  String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.deliciousdroid.bookmarks";
		
		public static final String Account = "ACCOUNT";
		public static final String Description = "DESCRIPTION";
		public static final String Url = "URL";
		public static final String Notes = "NOTES";
		public static final String Tags = "TAGS";
		public static final String Hash = "HASH";
		public static final String Meta = "META";
		public static final String Time = "TIME";
		public static final String LastUpdate = "LASTUPDATE";
		
		private int mId = 0;
		private String mAccount = null;
        private String mUrl = null;
        private String mDescription = null;
        private String mNotes = null;
        private String mTags = null;
        private String mHash = null;
        private String mMeta = null;
        private Boolean mPrivate = false;
        private long mTime = 0;
        private long mLastUpdate = 0;

        public int getId(){
        	return mId;
        }
        
        public String getUrl() {
            return mUrl;
        }
        
        public void setUrl(String url) {
        	mUrl = url;
        }

        public String getDescription() {
            return mDescription;
        }
        
        public void setDescription(String desc) {
        	mDescription = desc;
        }
        
        public String getNotes(){
        	return mNotes;
        }
        
        public void setNotes(String notes) {
        	mNotes = notes;
        }
        
        public String getTagString(){
        	return mTags;
        }
        
        public void setTagString(String tags) {
        	mTags = tags;
        }
        
        public ArrayList<Tag> getTags(){
			ArrayList<Tag> result = new ArrayList<Tag>();
			for(String s : this.getTagString().split(" ")) {
				result.add(new Tag(s));
			}
			
			return result;
        }
        
        public String getHash(){
        	return mHash;
        }

        public void setHash(String hash) {
        	mHash = hash;
        }
        
        public String getMeta(){
        	return mMeta;
        }
        
        public void setMeta(String meta) {
        	mMeta = meta;
        }
        
        public long getTime(){
        	return mTime;
        }
        
        public void setTime(long time) {
        	mTime = time;
        }
        
        public long getLastUpdate(){
        	return mLastUpdate;
        }
        
        public boolean getPrivate(){
        	return mPrivate;
        }
        
        public String getAccount(){
        	return mAccount;
        }
        
        public Bookmark() {
        }
        
        public Bookmark(int id) {
        	mId = id;
        }
        
        public Bookmark(String url) {
            mUrl = url;
        }
        
        public Bookmark(String url, String description, String notes, String tags, String account, long time) {
            mUrl = url;
            mDescription = description;
            mNotes = notes;
            mTags = tags;
            mAccount = account;
            mTime = time;
        }
        
        public Bookmark(String url, String description, String notes, String tags, boolean priv, long time) {
            mUrl = url;
            mDescription = description;
            mNotes = notes;
            mTags = tags;
            mPrivate = priv;
            mTime = time;
        }
        
        public Bookmark(int id, String account, String url, String description, String notes, String tags, String hash, String meta, long time) {
            mId = id;
        	mUrl = url;
            mDescription = description;
            mNotes = notes;
            mTags = tags;
            mHash = hash;
            mMeta = meta;
            mTime = time;
            mAccount = account;
        }
        
        public Bookmark copy() {
        	Bookmark b = new Bookmark();
        	b.mAccount = this.mAccount;
        	b.mDescription = this.mDescription;
        	b.mHash = this.mHash;
        	b.mId = this.mId;
        	b.mLastUpdate = this.mLastUpdate;
        	b.mMeta = this.mMeta;
        	b.mNotes = this.mNotes;
        	b.mPrivate = this.mPrivate;
        	b.mTags = this.mTags;
        	b.mTime = this.mTime;
        	b.mUrl = this.mUrl;
        	
        	return b;
        }
        
        public static Bookmark valueOf(JSONObject userBookmark) {
            try {
                final String url = userBookmark.getString("u");
                final String description = userBookmark.getString("d");
                final JSONArray tags = userBookmark.getJSONArray("t");
                final String stime = userBookmark.getString("dt");
                
                String notes = "";
                String account = "";
                
                if(userBookmark.has("n")) {
                	notes = userBookmark.getString("n");
                }
                if(userBookmark.has("a")) {
                	account = userBookmark.getString("a");
                }
                
				Date d = new Date(0);
				if(stime != null && stime != ""){
					try {
						d = DateParser.parse(stime);
					} catch (ParseException e) {
						Log.d("Parse error", stime);
						e.printStackTrace();
					}
				}

                return new Bookmark(url, description, notes, tags.join(" ").replace("\"", ""), account, d.getTime());
            } catch (final Exception ex) {
                Log.i("User.Bookmark", "Error parsing JSON user object");
            }
            return null;
        }
	}
}
