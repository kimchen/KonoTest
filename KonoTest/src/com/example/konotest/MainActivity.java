package com.example.konotest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity {
	private final String GET_INFO_URL = "http://yteam.thekono.com/KPI2/titles/gq/magazines";
	private final String GET_IMAGE_URL = "http://yteam.thekono.com/KPI2/magazines/%s/image";
	
	private List<MagazineInfo> mgzList = new ArrayList<MagazineInfo>();
	
	private ProgressDialog dialog = null;
	private Button startBtn = null;
	private ListView lv = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		startBtn = (Button)findViewById(R.id.button1);
		lv = (ListView)findViewById(R.id.listView1);
		
		startBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startGetInfo();
			}
		});
		
	}
	private void startGetInfo(){
		DownloadInfoTask dit = new DownloadInfoTask();
		dit.execute("");
	}
	private String getImageURL(String bid){
		return String.format(GET_IMAGE_URL, bid);
	}
	
	private class DownloadInfoTask extends AsyncTask<String, Float, Boolean>{
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(MainActivity.this);
			dialog.setTitle("KONO");
			dialog.setMessage("Now Loading...");
			dialog.setIndeterminate(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.show();
			super.onPreExecute();
		}
		@Override
		protected Boolean doInBackground(String... params) {
			mgzList = new ArrayList<MagazineInfo>();
			
			HttpParams hp = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(hp,3000);
			HttpConnectionParams.setSoTimeout(hp,3000);
			HttpClient hc = new DefaultHttpClient(hp);
			
			try {
				HttpGet get = new HttpGet(GET_INFO_URL);
				HttpResponse res = hc.execute(get);
				String jString = EntityUtils.toString(res.getEntity());
				JSONObject jobj = new JSONObject(jString);
				JSONArray mgzs = jobj.getJSONArray("magazines");
				for(int i=0;i<mgzs.length();i++){
					JSONObject subJobj = mgzs.getJSONObject(i);
					MagazineInfo mgzinfo = new MagazineInfo(subJobj);
					mgzList.add(mgzinfo);
				}
				
				BitmapFactory.Options bmpOpts = new BitmapFactory.Options();
				bmpOpts.inPurgeable = true;
				bmpOpts.inInputShareable = true;
				bmpOpts.inSampleSize = 8;
				
				for(MagazineInfo mgz : mgzList)
				{
					String imgUrl = getImageURL(mgz.bid);
					get = new HttpGet(imgUrl);
					res = hc.execute(get);
					InputStream is = res.getEntity().getContent();
					Bitmap bmp = BitmapFactory.decodeStream(is,null,bmpOpts);
					mgz.img = bmp;
					is.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			dialog.cancel();
			if(result){
//				List<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
//				for(MagazineInfo mi : mgzList){
//					HashMap<String, Object> item = new HashMap<String, Object>();
//					item.put("title", mi.title);
//					item.put("des", mi.description);
//					item.put("img", mi.img);
//					list.add(item);
//				}
//				SimpleAdapter sa = new SimpleAdapter(MainActivity.this, list, R.layout.konolist, new String[]{"title","des","img"}, new int[]{R.id.listtitle,R.id.listdes,R.id.listimage});
//				lv.setAdapter(sa);
				MagazineAdapter ma = new MagazineAdapter(MainActivity.this, mgzList);
				lv.setAdapter(ma);
			}else{
				Builder adb = new AlertDialog.Builder(MainActivity.this);
				adb.setTitle("Error");
				adb.setMessage("Get Info Failed.");
				adb.setCancelable(false);
				adb.setNegativeButton("OK", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				adb.create().show();
			}
			super.onPostExecute(result);
		}
		
	}
}

