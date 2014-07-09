package com.example.konotest;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MagazineAdapter extends BaseAdapter{

	private List<MagazineInfo> mList = new ArrayList<MagazineInfo>();
	private LayoutInflater mInflater = null;
	
	public MagazineAdapter(Context c, List<MagazineInfo> list){
		mList = list;
		mInflater = LayoutInflater.from(c);
	}
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutComponent lc = null;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.konolist, null);
			lc = new LayoutComponent((TextView)convertView.findViewById(R.id.listtitle), 
					(TextView)convertView.findViewById(R.id.listdes), 
					(ImageView)convertView.findViewById(R.id.listimage));
			convertView.setTag(lc);
		}else{
			lc = (LayoutComponent)convertView.getTag();
		}
		MagazineInfo mi = mList.get(position);
		//lc.title.setText(mi.title);
		lc.des.setText(mi.description);
		lc.img.setImageBitmap(mi.img);
		
		
		return convertView;
	}
	
	class LayoutComponent{
		public TextView title = null;
		public TextView des = null;
		public ImageView img = null;
		public LayoutComponent(TextView v1,TextView v2,ImageView v3){
			this.title = v1;
			this.des = v2;
			this.img = v3;
		}
	}
	
}
