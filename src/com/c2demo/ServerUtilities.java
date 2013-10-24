package com.c2demo;

import static com.c2demo.Constants.TAG;
import static com.c2demo.Constants.displayMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Random;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.JsonReader;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.insready.drupalcloud.RESTServerClient;
import com.insready.drupalcloud.ServiceNotAvailableException;

public final class ServerUtilities {
	private static final int MAX_ATTEMPTS = 5;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();
	public static SharedPreferences mSp;

	public static String mUid;
	public static String mUsername;
	public static String mPassword;
	public static String mSession;
	public static String mToken;

	/**
	 * Register this account/device pair within the server.
	 * 注册这个用户/设备到我们的服务器。
	 */
	static void register(final Context context, BasicNameValuePair[] parameters) {
		Log.i(Constants.TAG,
				"registering device (regId = " + parameters[0].getValue() + ")");

		mUsername = parameters[1].getValue();
		mPassword = parameters[2].getValue();
		mSp = context.getSharedPreferences("UserInfo", 0);
		RESTServerClient client = new RESTServerClient(
				"http://demo.queuedom.com/", "landedeng/");
		JsonReader jsr;
		BufferedReader tokenRd;
		
		try {
			jsr = client.userLogin(mUsername, mPassword);

			jsr.beginObject();

			while (jsr.hasNext()) {
				String name = jsr.nextName();

				if (name.equals("session_name")) {
					mSession = jsr.nextString() + "=" + mSession;
				} else if (name.equals("sessid")) {
					mSession = jsr.nextString();
				} else if (name.equals("user")) {
					jsr.beginObject();
					while (jsr.hasNext()) {
						name = jsr.nextName();
						if (name.equals("uid")) {
							mUid = jsr.nextString();
						} else {
							jsr.skipValue();
						}
					}
					jsr.endObject();
				} else {
					jsr.skipValue();
				}
			}
			jsr.endObject();

		} catch (ServiceNotAvailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//把Session记下来， 并SharedPreferences 中， 每一次登录更换一次。 
		client.setSession(mSession);
		mSp.edit().putString("session", mSession).commit();
		
		try {
			tokenRd = new BufferedReader(client.callGet(client.mSERVER + "services/session/token"));
			mToken = tokenRd.readLine();
			tokenRd.close();
		} catch (ClientProtocolException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (ServiceNotAvailableException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		//把Token记下来， 并SharedPreferences 中， 每一次登录更换一次。 
		client.setToken(mToken);
		mSp.edit().putString("token", mToken).commit();

		
		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

		for (int i = 1; i <= MAX_ATTEMPTS; i++) {
			Log.d(TAG, "Attempt #" + i + " to register");
			try {
				displayMessage(context, context.getString(
						R.string.server_registering, i, MAX_ATTEMPTS));

				BasicNameValuePair[] C2DMParameters = new BasicNameValuePair[2];
				C2DMParameters[0] = new BasicNameValuePair("gcm_regid", parameters[0].getValue());
				C2DMParameters[1] = new BasicNameValuePair("uid", mUid);
				
				BufferedReader rd = new BufferedReader(client.callPost(
						Constants.SERVER + Constants.ENDPOINT + "c2dm_gcm", C2DMParameters));
				String returnValue = rd.readLine();
				returnValue.toString();
				rd.close();

				GCMRegistrar.setRegisteredOnServer(context, true);
				String message = context.getString(R.string.server_registered);
				Constants.displayMessage(context, message);
				return;
			} catch (IOException e) {

				Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
				if (i == MAX_ATTEMPTS) {
					break;
				}
				try {
					Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
					Thread.sleep(backoff);
				} catch (InterruptedException e1) {
					// 在我们结束之前将Activity结束。
					Log.d(TAG, "Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return;
				}

				backoff *= 2;
			} catch (ServiceNotAvailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String message = context.getString(R.string.server_register_error,
				MAX_ATTEMPTS);
		Constants.displayMessage(context, message);
	}

	/**
	 * Unregister this account/device pair within the server.
	 * 注销这个用户/设备在服务器。
	 */
	static void unregister(final Context context, final String regId) {
		 Log.i(Constants.TAG, "注销设备 (regId = " + regId + ")");
//		 把用戶數據（包括regID）從服務器刪除即可。
//		 GCMRegistrar.setRegisteredOnServer(context, false);
//		 String message = context.getString(R.string.server_unregistered);
//		 Constants.displayMessage(context, message);
	}
}
