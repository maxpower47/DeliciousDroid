package com.deliciousdroid.client;

import java.util.ArrayList;

import com.deliciousdroid.providers.BookmarkContent.Bookmark;

public class GetAllBookmarksResponse {

	private int total;
	private ArrayList<Bookmark> list;
	
	public int getTotal() {
		return total;
	}
	
	public ArrayList<Bookmark> getBookmarkList() {
		return list;
	}
	
	public GetAllBookmarksResponse(ArrayList<Bookmark> bookmarklist, int totalbookmarks) {
		total = totalbookmarks;
		list = bookmarklist;
	}
}
