package com.example.konotest;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

public class MagazineInfo {
	public String bid = "";
	public String title = "";
	public String description ="";
	public Bitmap img = null;
	public MagazineInfo(JSONObject jobj){
		try {
			this.bid = jobj.getString("bid");
			this.title = jobj.getString("title");
			this.description = jobj.getString("description");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
