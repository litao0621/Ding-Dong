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
	private List<ListItems> itemDate;//���������ݿ�������������ݣ������˶�β������ݿ�
	private ItemAdapter adapter;
	private AlertDialog.Builder builder;
	private int current_versionCode;
	private final String apk_url = "http://test.com";//���ǻ�ȡ���µ�·����û�з�������������Ժ���
	
	private boolean swc=true;		//���任ɾ����ť״̬
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		
		//�������񣬻�ȡ���°汾�����ڷ�����������Ͳ�����
		//new GetVersion().execute(apk_url);
		init();
	}

	/**
	 * ��������ݺ����ת����activity,���Ȼ�������������Ϊ�˷������������UI�ˡ�
	 * ��Ȼ�㲥��Handler���ǿ��Ե�
	 * ��Ϊ���о�CursorLoader�������������������ʵ�����ݵĸ��µ�û�гɹ�
	 * ����ֱ��ʹ���α꣬������ʹ�������ṩ�ߣ��кð취�ľ͸����Ұ�
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//�ڵ��bar�ϵ�ɾ����ť�������ִ�в�����Ȼ����ת����ӽ����
		//������activityʱ��ɾ����ť��״̬û�иı䣬listview������ȴ���¼�����
		//���������ﻹԭ��button��״̬
		//û��̫�࿼�ǣ����кõķ����Ͳ�ȫ����
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
		listItem.setDivider(null); //ȥ���ָ���
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
		
		//������Ҳ�Ҳ����÷�������֪������ô����
		//����ֱ�Ӹ������������һ��״̬,���ж��Ƿ���ʾɾ����ť
		//�������ʲô�÷����͸����Ұɣ�����ѧϰ
		btnDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//��һ�ε��������level1�ı�������״̬��ΪFalse,��֮
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
	 * �����������ӷ���������ȡ���°汾��ʾ�û�����
	 * ��Ϊû���ֳɵķ�����������Ͳ���������
	 * ��Ҫ�Ļ����������ذ�ť��������һ������������
	 * 
	 * ���������Ҫһ����ͼ�ˣ�
	 * 
	 * �ؼ����룬
	 *  Intent intent = new Intent(Intent.ACTION_VIEW);
     *  intent.setDataAndType(uri, "application/vnd.android.package-archive");
     *  startActivity(intent);
     *  ���Ŵ���Ƕ���
	 * @author andylee
	 *
	 */
	public class GetVersion extends AsyncTask<String, Void, Map<String, Object>> {
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
		}
		//�Ҳ��Ե�ʱ��������ŵ���һ��json���ݣ��������ķ���������
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
				builder.setTitle("������Ϣ");
				builder.setMessage("���ı���Ӧ�������µİ汾������Ҫ���ظ�����");
				builder
				.setPositiveButton("����", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						//����һ��������
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				}).create().show();
			}
			
		}
		/**
		 *  �ӷ�������ȡ����
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
	 * ��ȡȫ�����ݵ��α꣬��ʼ��listview������
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
			Log.e("��������", e.getMessage());
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
	 * �Զ�������������ʢ��ʱ�䣬�����ӿ���״̬
	 * 
	 * @author andylee
	 * 
	 */
	public class ItemAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private List<ListItems> alarmDate;
		private boolean flag=false; //������������ʾ������Item�ϵ�ɾ����ť

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
					.equals("1111100") ? "������" : "");
			holder.state.setChecked(alarmDate.get(position).getState() == 1);
			//���￪ʼ�õ���setOnCheckedChangeListener�����ֻ�ÿ�ζ�����
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
										Toast.makeText(MainActivity.this, "���Ӵ�",
												Toast.LENGTH_SHORT).show();
									}
									else {
										dao.relese();
									}
									
								} else {
									flag=dao.updatastate(itemDate.get(necNo).getId(), 0);
									itemDate.get(necNo).setState(0);
									if (flag) {
										Toast.makeText(MainActivity.this, "���ӹر�",
												Toast.LENGTH_SHORT).show();
									}else {
										dao.relese();
									}
									
								}
							} catch (Exception e) {
								// TODO: handle exception
								Log.e("״̬����ʧ��", e.getMessage());
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
						Log.e("ɾ������ʧ��", e.getMessage());
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
