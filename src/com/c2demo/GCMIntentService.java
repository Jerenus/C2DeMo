package com.c2demo;

import org.apache.http.message.BasicNameValuePair;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";

	public GCMIntentService() {
		super(Constants.SENDER_ID);
	}

	/**
	 * Method called on device registered
	 * 注册成功后调用这个方法。
	 **/
	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "设备已经注册： regId = " + registrationId);
		Constants.displayMessage(context, "你的设备已经在 GCM上注册");
		Log.d("NAME", MainActivity.mUsername);

		// 按照你的设计定义键值对个数。
		BasicNameValuePair[] parameters = new BasicNameValuePair[3];

		parameters[0] = new BasicNameValuePair("gcm_regid",
				String.valueOf(registrationId));
		parameters[1] = new BasicNameValuePair("username",
				String.valueOf(MainActivity.mUsername));
		parameters[2] = new BasicNameValuePair("password",
				String.valueOf(MainActivity.mPassword));

		ServerUtilities.register(context, parameters);
	}

	/**
	 * Method called on device unregistred
	 * 注销成功后调用这个方法。
	 * */
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "设备注销");
		Constants.displayMessage(context, getString(R.string.gcm_unregistered));
		ServerUtilities.unregister(context, registrationId);
	}

	/**
	 * Method called on Receiving a new message
	 * 接收到新消息后调用这个方法。
	 * */
	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "接收消息");
		String message = intent.getExtras().getString("demo");

		Constants.displayMessage(context, message);
		// 提醒用户
		generateNotification(context, message);
	}

	/**
	 * Method called on receiving a deleted message
	 * 接受删除消息提示后调用这个方法。
	 * */
	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "接收删除消息提示");
		String message = getString(R.string.gcm_deleted, total);
		Constants.displayMessage(context, message);
		// notifies user
		generateNotification(context, message);
	}

	/**
	 * Method called on Error
	 * */
	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "接收错误：" + errorId);
		Constants.displayMessage(context,
				getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		Log.i(TAG, "接收可恢复的错误：" + errorId);
		Constants.displayMessage(context,
				getString(R.string.gcm_recoverable_error, errorId));
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private static void generateNotification(Context context, String message) {
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		String title = context.getString(R.string.app_name);

		Intent notificationIntent = new Intent(context, MainActivity.class);
		// set intent so it does not start a new activity
		// 定义intent，所以它不会启动一个新的activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);

		Notification notification = new Notification.Builder(context)
				.setContentTitle(title).setContentText(message)
				.setContentIntent(intent).setSmallIcon(icon).setWhen(when)
				.build();

		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Play default notification sound
		// 播放默认提醒声音
		notification.defaults |= Notification.DEFAULT_SOUND;

		// notification.sound = Uri.parse("android.resource://" +
		// context.getPackageName() + "your_sound_file_name.mp3");

		// Vibrate if vibrate is enabled
		// 震动激活的情况下触发震动
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);

	}

}
