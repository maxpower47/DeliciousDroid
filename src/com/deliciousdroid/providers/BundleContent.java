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

import java.util.ArrayList;

import android.net.Uri;
import android.provider.BaseColumns;

public class BundleContent {

	public static class Bundle implements BaseColumns {
		public static final Uri CONTENT_URI = Uri.parse("content://" + 
				BookmarkContentProvider.AUTHORITY + "/bundle");
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.deliciousdroid.bundles";
		
		public static final String Name = "NAME";
		public static final String Tags = "TAGS";
		public static final String Account = "ACCOUNT";
		
        private String mName;
        private int mId = 0;
        private String mTags;
        private String mAccount;

        public int getId(){
        	return mId;
        }
        
        public void setId(int id){
        	mId = id;
        }
        
        public String getName() {
            return mName;
        }
        
        public void setName(String name) {
        	mName = name;
        }
        
        public String getTagString(){
        	return mTags;
        }
        
        public void setTagString(String tags) {
        	mTags = tags;
        }
        
        public ArrayList<String> getTags(){
			ArrayList<String> result = new ArrayList<String>();
			for(String s : this.getTagString().split(" ")) {
				result.add(s);
			}
			
			return result;
        }
        
        public String getAccount(){
        	return mAccount;
        }
        
        public void setAccount(String account) {
        	mAccount = account;
        }
        
        
        public Bundle() {
        	
        }
        
        public Bundle(String name) {
            mName = name;
        }
        
        public Bundle(String name, String tags) {
            mName = name;
            mTags = tags;
        }
        
        public Bundle copy(){
        	Bundle t = new Bundle();
        	t.mId = this.mId;
        	t.mName = this.mName;
        	t.mTags = this.mTags;
        	return t;
        }
	}
}
