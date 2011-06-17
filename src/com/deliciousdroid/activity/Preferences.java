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
 
import com.deliciousdroid.Constants;
import com.deliciousdroid.R;
import com.deliciousdroid.providers.BookmarkContentProvider;
import com.deliciousdroid.util.SyncUtils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.widget.Toast;

public class Preferences extends PreferenceActivity {
	
	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mContext = this;

        Preference synctimePref = (Preference) findPreference("pref_synctime");
        synctimePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object value) {
				long time = Long.parseLong((String)value);
				
				SyncUtils.removePeriodicSync(BookmarkContentProvider.AUTHORITY, Bundle.EMPTY, mContext);
				
				if(time != 0) {
					SyncUtils.addPeriodicSync(BookmarkContentProvider.AUTHORITY, Bundle.EMPTY, time, mContext);
				}
				
				return true;
			}
        });

        
        Preference syncPref = (Preference) findPreference("pref_forcesync");
        syncPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
        		Toast.makeText(mContext, "Syncing...", Toast.LENGTH_LONG).show();
        		ContentResolver.requestSync(null, BookmarkContentProvider.AUTHORITY, Bundle.EMPTY);
        		return true;
        	}
        });
        
        Preference accountPref = (Preference) findPreference("pref_accountsettings");
        accountPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
        		Intent i = new Intent(Settings.ACTION_SYNC_SETTINGS);
        		i.putExtra(Settings.EXTRA_AUTHORITIES, new String[] {BookmarkContentProvider.AUTHORITY});
        		
        		mContext.startActivity(i);
        		return true;
        	}
        });
        
        Preference licensePref = (Preference) findPreference("pref_license");
        licensePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
            	Uri link = Uri.parse("http://www.gnu.org/licenses/gpl-3.0.txt");
        		Intent i = new Intent(Intent.ACTION_VIEW, link);
        		
        		startActivity(i);
            	return true;
            }
        });
        
        Preference helpPref = (Preference) findPreference("pref_help");
        helpPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
            	Uri link = Uri.parse("http://code.google.com/p/deliciousdroid/wiki/Manual");
        		Intent i = new Intent(Intent.ACTION_VIEW, link);
        		
        		startActivity(i);
            	return true;
            }
        });
        
        Preference donatePref = (Preference) findPreference("pref_donate");
        donatePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
            	Uri link = Uri.parse(Constants.DONATION_URL);
        		Intent i = new Intent(Intent.ACTION_VIEW, link);
        		
        		startActivity(i);
            	return true;
            }
        });
        
        setTitle("DeliciousDroid Settings");
    }
}