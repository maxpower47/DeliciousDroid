package com.deliciousdroid.action;

import android.os.AsyncTask;

import com.deliciousdroid.client.NetworkUtilities;
import com.deliciousdroid.providers.ArticleContent.Article;

public class GetArticleTextTask extends AsyncTask<String, Integer, Article>{
	private String url;
	
	@Override
	protected Article doInBackground(String... args) {
		
		if(args.length > 0 && args[0] != null && args[0] != "") {
    		url = args[0];
	
    		return NetworkUtilities.getArticleText(url);
		} else return null;
		
	}
}
