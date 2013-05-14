package com.lee.alarmclock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.lee.dao.AlarmDao;
import com.lee.model.DBUtiles.toolutil;
import com.lee.model.ListItems;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private ImageButton btnAdd, btnDelete,btnDeleteTime;
	private ListView listItem;		
	private List<ListItems> itemDate;//用来放数据库的搜索出的数据，避免了多次操作数据库
	private ItemAdapter adapter;
	private AlertDialog.Builder builder;
	private int current_versionCode;
	private final String apk_url = "http://test.com";//这是获取更新的路径，没有服务器，这里可以忽略
	
	private boolean swc=true;		//来变换删除按钮状态
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		
		//启动任务，获取最新版本，由于服务器限制这就不做了
		//new GetVersion().execute(apk_url);
		init();
	}

	/**
	 * 当添加数据后会跳转到主activity,首先会调用这个方法，为了方便就这样更新UI了。
	 * 当然广播和Handler都是可以的
	 * 因为在研究CursorLoader这个东西，尝试用它来实现数据的更新但没有成功
	 * 我想直接使用游标，而不是使用内容提供者，有好办法的就告诉我吧
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//在点击bar上的删除按钮后，如果不执行操作，然后跳转到添加界面后
		//返回主activity时，删除按钮的状态没有改变，listview的数据却重新加载了
		//所以在这里还原下button的状态
		//没有太多考虑，你有好的方法就补全它吧
		swc=true;	
		adapter.setFlag(false);
		itemDate=initListData();
		adapter.setDate(itemDate);
		listItem.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		btnDelete.getBackground().setLevel(0);
	}

	/**
	 * 
	 */
	public void init() {
		btnAdd = (ImageButton) findViewById(R.id.addalarm);
		btnDelete = (ImageButton) findViewById(R.id.deletealarm);
		
		listItem = (ListView) findViewById(R.id.listalarmitem);
		listItem.setDivider(null); //去掉分割线
		itemDate = initListData();	
		
		builder = new AlertDialog.Builder(this);
		adapter = new ItemAdapter(this, itemDate,false);
		listItem.setAdapter(adapter);
		
		btnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, AddActivity.class);
				startActivity(intent);

			}
		});
		
		//这里我也找不到好方法，不知道该怎么做，
		//所以直接给适配器添加了一个状态,来判断是否显示删除按钮
		//如果你有什么好方法就告诉我吧，互相学习
		btnDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//第一次点击后设置level1的背景，将状态改为False,反之
				if (swc) {
					adapter.setFlag(true);
					adapter.notifyDataSetChanged();
					btnDelete.getBackground().setLevel(1);
					swc=false;
				}else {
					adapter.setFlag(false);
					adapter.notifyDataSetChanged();
					btnDelete.getBackground().setLevel(0);
					swc=true;
					
				}
				
				
			}
		});
		

	}
	
	
	/**
	 * 这里用来连接服务器，获取最新版本提示用户下载
	 * 因为没有现成的服务器，这里就不做更新了
	 * 需要的话可以在下载按钮中再启动一个任务来下载
	 * 
	 * 具体就是需要一个意图了，
	 * 
	 * 关键代码，
	 *  Intent intent = new Intent(Intent.ACTION_VIEW);
     *  intent.setDataAndType(uri, "application/vnd.android.package-archive");
     *  startActivity(intent);
     *  相信大家是懂的
	 * @author andylee
	 *
	 */
	public class GetVersion extends AsyncTask<String, Void, Map<String, Object>> {
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
		}
		//我测试的时候服务器放的是一组json数据，这根据你的服务器来定
		@Override
		protected Map<String, Object> doInBackground(String... params) {
			// TODO Auto-generated method stub
			Map<String, Object> map = new HashMap<String, Object>();
			String jsonString =getConnection(apk_url);
			
			try {
				JSONObject jsonObject = new JSONObject(jsonString).getJSONObject("installMessage");
				map.put("versionCode", jsonObject.getInt("versionCode"));
				map.put("apk_url", jsonObject.getString("apk_url"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
					
			return map;
		}
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			if (Integer.parseInt(result.get("versionCode").toString()) > current_versionCode) {
				builder.setTitle("更新信息");
				builder.setMessage("您的本定应用有最新的版本，您需要下载更新吗？");
				builder
				.setPositiveButton("下载", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						//启动一个新任务
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				}).create().show();
			}
			
		}
		/**
		 *  从服务器获取数据
		 * @param path
		 * @return
		 *
		 */
		public String getConnection(String path) {
			HttpPost post=new HttpPost(path);
			HttpClient client=new DefaultHttpClient();
			String result="";
			try {
				HttpResponse response=client.execute(post);
				
				if (response.getStatusLine().getStatusCode()==HttpStatus.SC_OK) {
					result=EntityUtils.toString(response.getEntity(),"utf-8");								
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
			
		}
		
	}
	/**
	 * 获取全部数据的游标，初始化listview的数据
	 * 
	 * @return
	 */
	public List<ListItems> initListData() {
		List<ListItems> itemData = new ArrayList<ListItems>();
		AlarmDao alarmDao = new AlarmDao(this);
		alarmDao.openDB();
		Cursor cursor = alarmDao.findall();
		try {

			while (cursor.moveToNext()) {
				ListItems item = new ListItems();
				item.setHour(cursor.getString(cursor
						.getColumnIndex(toolutil.hour)));
				item.setMinute(cursor.getString(cursor
						.getColumnIndex(toolutil.minute)));
				item.setWeek(cursor.getString(cursor
						.getColumnIndex(toolutil.week)));
				item.setState(cursor.getInt(cursor
						.getColumnIndex(toolutil.state)));
				item.setId(cursor.getInt(cursor.getColumnIndex(toolutil.id)));
				itemData.add(item);
			}
		} catch (Exception e) {
			Log.e("加载数据", e.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			alarmDao.relese();
		}

		return itemData;
	}

	/**
	 * 
	 * 自定义适配器，来盛放时间，与闹钟开关状态
	 * 
	 * @author andylee
	 * 
	 */
	public class ItemAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private List<ListItems> alarmDate;
		private boolean flag=false; //这里来用于显示和隐藏Item上的删除按钮

		public ItemAdapter(Context context, List<ListItems> alarmDate,boolean flag) {
			mInflater = LayoutInflater.from(context);
			this.alarmDate = alarmDate;
			this.flag=flag;
		}
		public void setDate(List<ListItems> alarmDate) {
			this.alarmDate=alarmDate;
		}
		public void setFlag(boolean flag) {
			this.flag=flag;
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
			final Handler handler = new Handler();
			final int necNo=position;
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.itemalarm, null);
				holder = new ViewHolder();
				holder.btnDeleteTime=(ImageButton)convertView.findViewById(R.id.deletetime);
				holder.time = (TextView) convertView.findViewById(R.id.time);
				holder.model = (TextView) convertView.findViewById(R.id.model);
				holder.state = (CheckBox) convertView
						.findViewById(R.id.ckbstate);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			if (flag) {
				holder.btnDeleteTime.setVisibility(View.VISIBLE);
			}else {
				holder.btnDeleteTime.setVisibility(View.GONE);
			}
			
			String strTime = alarmDate.get(position).getHour() + ":"
					+ alarmDate.get(position).getMinute();
			holder.time.setText(strTime);

			holder.model.setText(alarmDate.get(position).getWeek()
					.equals("1111100") ? "工作日" : "");
			holder.state.setChecked(alarmDate.get(position).getState() == 1);
			//这里开始用的是setOnCheckedChangeListener但发现会每次都调用
			holder.state
					.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							CheckBox ckbBox=(CheckBox)v;
							AlarmDao dao = new AlarmDao(MainActivity.this);
							boolean flag;
							try {
								dao.openDB();
								
								if (ckbBox.isChecked()) {
									flag=dao.updatastate(itemDate.get(necNo).getId(), 1);
									itemDate.get(necNo).setState(1);	
									if (flag) {
										Toast.makeText(MainActivity.this, "闹钟打开",
												Toast.LENGTH_SHORT).show();
									}
									else {
										dao.relese();
									}
									
								} else {
									flag=dao.updatastate(itemDate.get(necNo).getId(), 0);
									itemDate.get(necNo).setState(0);
									if (flag) {
										Toast.makeText(MainActivity.this, "闹钟关闭",
												Toast.LENGTH_SHORT).show();
									}else {
										dao.relese();
									}
									
								}
							} catch (Exception e) {
								// TODO: handle exception
								Log.e("状态操作失败", e.getMessage());
							}finally{
								dao.relese();
							}
						}
					});
			holder.btnDeleteTime.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					AlarmDao dao = new AlarmDao(MainActivity.this);
					try {
						dao.openDB();
						dao.deletedata(itemDate.get(necNo).getId());
						itemDate.remove(necNo);
						handler.postDelayed(runnable, 200);
					} catch (Exception e) {
						// TODO: handle exception
						Log.e("删除操作失败", e.getMessage());
					}finally{
						dao.relese();
					}
					
				}
			});
			return convertView;
		}
		Runnable runnable=new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				adapter.notifyDataSetChanged();
			}
		};
		public final class ViewHolder {
			public ImageButton btnDeleteTime=null;
			public TextView time = null;
			public TextView model = null;
			public CheckBox state = null;
		}
		

	}

}
