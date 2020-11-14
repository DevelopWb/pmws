package com.leng.hiddencamera;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.leng.hiddencamera.util.AESTool;
import com.leng.hiddencamera.util.DCPubic;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SuperManagerActivity extends Activity implements OnClickListener {

	TextView txtZcm, txtImei, txtUseTime;
	Button btnSave, btnCancle;
	static public String IMEI;

	private static final int SHOW_DATAPICK = 0;
	private static final int DATE_DIALOG_ID = 1;

	private int mYear;
	private int mMonth;
	private int mDay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_super_manager);
		txtZcm = (TextView) findViewById(R.id.txt_zcm);
		txtImei = (TextView) findViewById(R.id.txt_imei);
		txtUseTime = (TextView) findViewById(R.id.usetime);
		txtUseTime.setOnClickListener(this);
		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(this);
		btnCancle = (Button) findViewById(R.id.btnCancle);
		btnCancle.setOnClickListener(this);
		getIMEI();
		String strreg = null;
		try {
			strreg = AESTool.decrypt("lyx123456ybf", DCPubic.getDataFromSp(
					SuperManagerActivity.this, "REG", "OBJREG", ""));
		} catch (Exception e) {
			e.printStackTrace();
		}
		txtZcm.setText(strreg);
		txtImei.setText(IMEI);

		String jzsj = "";
		try {
			jzsj = AESTool.decrypt("lyx123456ybf", DCPubic.getDataFromSp(
					SuperManagerActivity.this, "REG", "jzsj", ""));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!jzsj.equals("")) {
			mYear = Integer.parseInt(jzsj.substring(0, 4));
			mMonth = Integer.parseInt(jzsj.substring(5, 7));
			mDay = Integer.parseInt(jzsj.substring(8, 10));
			// Toast.makeText(this, "时间间隔：=" + mYear + mMonth + mDay ,
			// 1).show();
			txtUseTime.setText(jzsj);
			// updateDateDisplay();
		} else {
			final Calendar c = Calendar.getInstance();
			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH);
			mDay = c.get(Calendar.DAY_OF_MONTH);
			updateDateDisplay();
		}

	}

	private void getIMEI() {
		TelephonyManager telephonyManager = (TelephonyManager) SuperManagerActivity.this
				.getSystemService(Context.TELEPHONY_SERVICE);
		IMEI = telephonyManager.getDeviceId();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSave:
			Date currentDate = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			int value = compare_date(df.format(currentDate), txtUseTime
					.getText().toString());
			if (value == 1) {
				Toast.makeText(this, "选择日期不能小于当前日期，请重新选择！", 1).show();
				updateDateDisplay();
				return;
			}
			SharedPreferences startauto = this.getSharedPreferences("REG", 0);
			SharedPreferences.Editor editor = startauto.edit();
			try {
				editor.putString("jzsj", AESTool.encrypt("lyx123456ybf",
						txtUseTime.getText().toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			editor.commit();
			Toast.makeText(this, "截止时间保存成功！", 1).show();
			break;
		case R.id.btnCancle:
			this.finish();
			break;
		case R.id.usetime:
			Message msg = new Message();
			msg.what = SuperManagerActivity.SHOW_DATAPICK;
			this.dateandtimeHandler.sendMessage(msg);
			break;
		default:
			break;
		}
	}

	public static int compare_date(String DATE1, String DATE2) {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() > dt2.getTime()) {
				System.out.println("dt1 在dt2前");
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				System.out.println("dt1在dt2后");
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	/**
	 * 更新日期显示
	 */
	private void updateDateDisplay() {
		txtUseTime.setText(new StringBuilder().append(mYear).append("-")
				.append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
				.append("-").append((mDay < 10) ? "0" + mDay : mDay));
	}

	/**
	 * 日期控件的事件
	 */
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;

			updateDateDisplay();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}

		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DATE_DIALOG_ID:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		}
	}

	/**
	 * 处理日期和时间控件的Handler
	 */
	Handler dateandtimeHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SuperManagerActivity.SHOW_DATAPICK:
				showDialog(DATE_DIALOG_ID);
				break;
			}
		}

	};
}
