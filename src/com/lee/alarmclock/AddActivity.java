package com.lee.alarmclock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.lee.model.WeekDialog;
import com.lee.widget.CustomTimePick;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.lee.widget.NumericTMAdapter;

public class AddActivity extends Activity {
	private TextView txtweek, txtvoice;
	private ImageButton btnsave, btnback;
	private ListView listweek;
	private WeekDialog weekDialog;
	private List<WeekDialog> overalllist;
	private CheckBox ckbModel;
	private CustomTimePick hours, mins; // 自定义timePicker
	private DisListAdapter adapter;

	private String sqlWeekData = "0000000"; // 周期状态码
	private String sqlMusic; // 音乐名称
	private int sqlState; // 提醒模式
	private int sqlHour, sqlMinute; // 时， 分

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addalarm);
		initData(); // 初始化list数据
		init();

	}

	public void init() {

		txtweek = (TextView) findViewById(R.id.setweek);
		txtvoice = (TextView) findViewById(R.id.setvoice);
		btnsave = (ImageButton) findViewById(R.id.save);
		btnback = (ImageButton) findViewById(R.id.back);
		ckbModel = (CheckBox) findViewById(R.id.ckbModel);

		hours = (CustomTimePick) findViewById(R.id.hour);
		hours.setAdapter(new NumericTMAdapter(0, 23));
		mins = (CustomTimePick) findViewById(R.id.mins);
		mins.setAdapter(new NumericTMAdapter(0, 59));
		// 设置滑轮是否循环
		hours.setCyclic(true);
		mins.setCyclic(true);

		Calendar c = Calendar.getInstance();
		int curHours = c.get(Calendar.HOUR_OF_DAY);
		int curMinutes = c.get(Calendar.MINUTE);
		hours.setCurrentItem(curHours);
		mins.setCurrentItem(curMinutes);

		adapter = new DisListAdapter(AddActivity.this, overalllist);

		btnback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		btnsave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveData();
				finish();
			}
		});
		txtweek.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				initWeekDialog();
			}
		});

	}

	/**
	 * 初始化闹钟周期dialog
	 * 
	 * 每次点击保存先清空上一次的数据，要不在用户闹钟未设置完成，但多次点击设置 周期会与上一次数据叠加
	 * 
	 * 最后遍历listview中的checkbox,将数据以01码存入
	 * 
	 * 要注意listview中item与checkbox的关联性，以及后期怎么取出每个Item的值
	 * 注意自定义适配器中给checkbox添加了事件，要不点item会保存状态，但点中checkbox时不会有状态保存
	 */
	public void initWeekDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this,
				R.style.Dialog);

		LayoutInflater inflater = AddActivity.this.getLayoutInflater();
		View view = inflater.inflate(R.layout.setweek, null);

		listweek = (ListView) view.findViewById(R.id.listweek);
		listweek.setAdapter(adapter);
		builder.setView(view)
				.setPositiveButton("保存", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						sqlWeekData = "";

						for (WeekDialog weekdata : overalllist) {
							sqlWeekData += weekdata.isChecked() ? "1" : "0";
						}

						txtweek.setText(sqlWeekData.equals("0000000") ? "仅一次"
								: sqlWeekData.equals("1111100") ? "工作日" : "设置");

					}
				}).create().show();
		listweek.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				CheckBox checkbox = (CheckBox) view
						.findViewById(R.id.dispatch_item_select_state);
				checkbox.setChecked(!checkbox.isChecked());
				overalllist.get(position).setChecked(checkbox.isChecked());
				//

			}
		});
	}

	/**
	 * 
	 * 定义带复选框的listview
	 * 
	 * @author lee
	 * 
	 */
	public class DisListAdapter extends BaseAdapter {

		private Context mContext;
		private List<WeekDialog> list;

		public DisListAdapter(Context context, List<WeekDialog> list) {
			mContext = context;
			this.list = list;

		}

		public void setData(List<WeekDialog> data) {
			this.list = data;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int week) {
			return list.get(week);
		}

		@Override
		public long getItemId(int week) {
			return week;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.itemweek, null);
			}
			TextView tvUserName = (TextView) convertView
					.findViewById(R.id.dispatch_item_select_week);
			final CheckBox ckbItem = (CheckBox) convertView
					.findViewById(R.id.dispatch_item_select_state);
			WeekDialog week = list.get(position);
			tvUserName.setText(week.getWeek());

			ckbItem.setChecked(week.isChecked());
			// 将选择状态保存在overalllist中
			ckbItem.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					overalllist.get(position).setChecked(ckbItem.isChecked());

				}
			});
			week = null;
			return convertView;
		}

	}

	private void saveData() {
		sqlState = ckbModel.isChecked() ? 1 : 0; // 保存闹钟模式状态码
		sqlHour = hours.getCurrentItem();
		sqlMinute = mins.getCurrentItem();

		Toast.makeText(
				AddActivity.this,
				"周期码：" + sqlWeekData + "  状态码" + sqlState + "时间" + sqlHour
						+ ":" + sqlMinute, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 为周期选择初始化数据
	 */
	private void initData() {
		String[] weekstr = { "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日" };
		overalllist = new ArrayList<WeekDialog>();

		for (int i = 0; i < weekstr.length; i++) {
			weekDialog = new WeekDialog();
			weekDialog.setWeek(weekstr[i]);
			weekDialog.setChecked(false);
			overalllist.add(weekDialog);
		}

	}
}
