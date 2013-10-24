package com.c2demo;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity {

	TextView lblMessage;

	AsyncTask<Void, Void, Void> mRegisterTask;

	// 提示框申明
	AlertDialogManager alert = new AlertDialogManager();

	public static String mUsername;
	public static String mPassword;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 从intent中获取 姓名和邮箱
		Intent i = getIntent();
		SharedPreferences sp = getSharedPreferences("UserInfo", 0);
		String session = sp.getString("session", null);

		if (session != null && session.length() > 0) {

		} else {
			mUsername = i.getStringExtra("username");
			mPassword = i.getStringExtra("password");	
		}
	

		// 确保该设备具有适当的配置。
		GCMRegistrar.checkDevice(this);

		// 确保 manifest中的权限已经完全正确
		GCMRegistrar.checkManifest(this);

		lblMessage = (TextView) findViewById(R.id.lblMessage);

		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				Constants.DISPLAY_MESSAGE_ACTION));

		// 获取 GCM registration id
		final String regId = GCMRegistrar.getRegistrationId(this);

		// 检查是否 regid 已经存在
		if (regId.equals("")) {
			// 如果 present 现在通过GCMRegistrar注册一个
			GCMRegistrar.register(this, Constants.SENDER_ID);
		} else {
			// 设备已经在GCM上注册
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// 跳过注册环节
				Toast.makeText(getApplicationContext(), "已经在 GCM 上注册",
						Toast.LENGTH_LONG).show();
			} else {
				// 尝试重新注册, 但是没有提供UI线程。
				// 这里需要在 onDestroy()方法中把申明的线程杀掉，
				// 因此用AsyncTask来代替原始线程。
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {	
						
						// 按照你的设计定义键值对个数
						BasicNameValuePair[] parameters = new BasicNameValuePair[3];

						parameters[0] = new BasicNameValuePair("gcm_regid",
								String.valueOf(regId));
						parameters[1] = new BasicNameValuePair("username",
								String.valueOf(mUsername));
						parameters[2] = new BasicNameValuePair("password",
								String.valueOf(mPassword));

						// 在服务器上注册
						// 在服务器上创建新的用户
						ServerUtilities.register(getApplicationContext(),
								parameters);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}
		}
	}

	/**
	 * Receiving push messages
	 * */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(
					Constants.EXTRA_MESSAGE);
			// 把手机从睡眠状态中唤醒
			WakeLocker.acquire(getApplicationContext());

			// 显示接收到的消息
			lblMessage.append(newMessage + "\n");
			Toast.makeText(getApplicationContext(), "新的消息：" + newMessage,
					Toast.LENGTH_LONG).show();

			// 释放唤醒锁
			WakeLocker.release();
		}
	};

	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		try {
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			Log.e("注销接收发生错误", "> " + e.getMessage());
		}
		super.onDestroy();
	}
}
