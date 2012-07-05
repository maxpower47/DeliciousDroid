/*
 * PinDroid - http://code.google.com/p/PinDroid/
 *
 * Copyright (C) 2010 Matt Schmidt
 *
 * PinDroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * PinDroid is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PinDroid; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package com.deliciousdroid.service;

import java.util.ArrayList;

import com.deliciousdroid.Constants;
import com.deliciousdroid.platform.BookmarkManager;
import com.deliciousdroid.platform.TagManager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.Intent;

public class AccountService extends IntentService {

	private AccountManager mAccountManager;
	
	public AccountService() {
		super("AccountService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		mAccountManager = AccountManager.get(this);
		
		Account[] accounts = mAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
		ArrayList<String> accountsList = new ArrayList<String>();
		for (int i = 0; i < accounts.length; i++) {	
			accountsList.add(accounts[i].name);
		}
		
		BookmarkManager.TruncateBookmarks(accountsList, this, true);
		TagManager.TruncateOldTags(accountsList, this);
	}
}