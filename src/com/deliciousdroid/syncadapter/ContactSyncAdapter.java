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

package com.deliciousdroid.syncadapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.deliciousdroid.Constants;
import com.deliciousdroid.client.DeliciousFeed;
import com.deliciousdroid.client.FeedForbiddenException;
import com.deliciousdroid.client.User;
import com.deliciousdroid.client.User.Status;
import com.deliciousdroid.platform.ContactManager;

import org.apache.http.ParseException;
import org.apache.http.auth.AuthenticationException;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * SyncAdapter implementation for syncing sample SyncAdapter contacts to the
 * platform ContactOperations provider.
 */
public class ContactSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = "SyncAdapter";

    private final AccountManager mAccountManager;
    private final Context mContext;
    
    private String authtoken = null;

    public ContactSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
        mAccountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
        ContentProviderClient provider, SyncResult syncResult) {
        List<User> users;
        List<Status> statuses;
        try {
        	
        	final AccountManager am = AccountManager.get(mContext);
        	
    		authtoken = am.blockingGetAuthToken(account, Constants.AUTHTOKEN_TYPE, false);
        	 
            // fetch updates from the sample service over the cloud
            users = DeliciousFeed.fetchFriendUpdates(account);
            // update platform contacts.
            Log.d(TAG, "Calling contactManager's sync contacts");
            ContactManager.syncContacts(mContext, account.name, users);
            // fetch and update status messages for all the synced users.
            
               
            if (Build.VERSION.SDK_INT >= 15) {
            	ContactManager.insertStreamStatuses(mContext, account.name);
        	} else {
        		statuses = DeliciousFeed.fetchFriendStatuses(account);
        		ContactManager.insertStatuses(mContext, account.name, statuses);
        	}
        } catch (final IOException e) {
            Log.e(TAG, "IOException", e);
            syncResult.stats.numIoExceptions++;
        } catch (final AuthenticationException e) {
            mAccountManager.invalidateAuthToken(Constants.ACCOUNT_TYPE, authtoken);
            syncResult.stats.numAuthExceptions++;
            Log.e(TAG, "AuthenticationException", e);
        } catch (final ParseException e) {
            syncResult.stats.numParseExceptions++;
            Log.e(TAG, "ParseException", e);
        } catch (final JSONException e) {
            syncResult.stats.numParseExceptions++;
            Log.e(TAG, "JSONException", e);
        } catch (final FeedForbiddenException e) {
            Log.e(TAG, "FeedForbiddenException");
        } catch (final AuthenticatorException e) {
        	Log.e(TAG, "AuthenticatorException");
        } catch (final OperationCanceledException e) {
        	Log.e(TAG, "OperationCanceledException");
        }
    }
}
