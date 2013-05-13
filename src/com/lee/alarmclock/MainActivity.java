package com.lee.alarmclock;

import java.util.ArrayList;
import java.util.List;

import com.lee.dao.AlarmDao;
import com.lee.model.DBUtiles;
import com.lee.model.DBUtiles.toolutil;
import com.lee.model.ListItems;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MainActivity extends Activity {
	private ImageButton btnAdd, btnDelete;
	private ListView listItem;
	private List<ListItems> itemDate;
	private ItemAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		init();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	/**
	 * 当添加数据后会跳转到主activity,首先会调用这个方法，为了方便就这样更新UI了。
	 * 当然广播和Handler都是可以的
	 */	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		init();
	}
	/**
	 * 
	 */
	public void init() {
		btnAdd = (ImageButton) findViewById(R.id.addalarm);
		btnDelete = (ImageButton) findViewById(R.id.deletealarm);
		listItem = (ListView) findViewById(R.id.listalarmitem);
		itemDate=initListData();
		
		adapter=new ItemAdapter(this, itemDate);
		listItem.setAdapter(adapter);
		
		btnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, AddActivity.class);
				startActivity(intent);

			}
		});

	}
	
	/**
	 * 获取全部数据的游标，初始化listview的数据
	 * @return
	 */
	public List<ListItems> initListData() {
		List<ListItems> itemData=new ArrayList<ListItems>();
		AlarmDao alarmDao=new AlarmDao(this);
		alarmDao.openDB();
		Cursor cursor=alarmDao.findall();
		try {
			
		 
		while (cursor.moveToNext()) {
			ListItems item=new ListItems();
			item.setHour(cursor.getString(cursor.getColumnIndex(toolutil.hour)));
			item.setMinute(cursor.getString(cursor.getColumnIndex(toolutil.minute)));
			item.setWeek(cursor.getString(cursor.getColumnIndex(toolutil.week)));
			item.setState(cursor.getInt(cursor.getColumnIndex(toolutil.state)));
			itemData.add(item);			
		}
		}
		catch (Exception e) {
			Log.e("加载数据", e.getMessage());
		}finally{
			if (cursor!=null) {
				cursor.close();
			}
			alarmDao.relese();
		}
		
		
		return itemData;
	}

	/**
	 * 
	 * 自定义适配器，来盛放时间，与闹钟开关状态
	 * @author lee
	 *
	 */
	public class ItemAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private List<ListItems> alarmDate;

		public ItemAdapter(Context context, List<ListItems> alarmDate) {
			mInflater = LayoutInflater.from(context);
			this.alarmDate = alarmDate;

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return alarmDate.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return alarmDate.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.itemalarm, null);
				holder = new ViewHolder();
				holder.time = (TextView) convertView.findViewById(R.id.time);
				holder.model = (TextView) convertView.findViewById(R.id.model);
				holder.state = (CheckBox) convertView
						.findViewById(R.id.ckbstate);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			String strTime = alarmDate.get(position).getHour() + ":"
					+ alarmDate.get(position).getMinute();
			holder.time.setText(strTime);
			
			holder.model.setText(alarmDate.get(position).getWeek()
					.equals("1111100") ? "工作日" : "");
			holder.state.setChecked(alarmDate.get(position).getState()==1);
			return convertView;
		}

		public final class ViewHolder {

			public TextView time = null;
			public TextView model = null;
			public CheckBox state = null;
		}

	}

}
