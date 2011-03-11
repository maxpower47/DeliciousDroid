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

import android.accounts.Account;
import android.content.Context;

import com.deliciousdroid.providers.BundleContent.Bundle;

public class BundleTaskArgs{
	private Bundle bundle;
	private Account account;
	private Context context;
	
	public Bundle getBundle(){
		return bundle;
	}
	
	public Account getAccount(){
		return account;
	}
	
	public Context getContext(){
		return context;
	}
	
	public BundleTaskArgs(Bundle b, Account a, Context c){
		bundle = b;
		account = a;
		context = c;
	}
}