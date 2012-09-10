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

import com.deliciousdroid.fragment.MainSearchResultsFragment;
import com.deliciousdroid.R;
import com.deliciousdroid.action.IntentHelper;

import android.app.SearchManager;
import android.os.Bundle;

public class MainSearchResults extends FragmentBaseActivity implements MainSearchResultsFragment.OnSearchActionListener {

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_search_results);
	}

	public void onBookmarkSearch() {
		startActivity(IntentHelper.SearchBookmarks(getIntent().getStringExtra(SearchManager.QUERY), mAccount.name, this));
	}

	public void onTagSearch() {
		startActivity(IntentHelper.SearchTags(getIntent().getStringExtra(SearchManager.QUERY), mAccount.name, this));
	}

	public void onGlobalTagSearch() {
		startActivity(IntentHelper.SearchGlobalTags(getIntent().getStringExtra(SearchManager.QUERY), mAccount.name, this));
	}
}