package com.c2demo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends Activity {
	// 提示框管理
	AlertDialogManager alert = new AlertDialogManager();

	// 界面元素
	EditText txtUsername;
	EditText txtPassword;

	Button btnRegister;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		SharedPreferences sp = getSharedPreferences("UserInfo", 0);
		String session = sp.getString("session", null);

		if (session != null && session.length() > 0) {
			// GCM 发送的 id / 服务端 url is 不存在
			alert.showAlertDialog(RegisterActivity.this, "注册过的用户!",
					"你已经注册过，所以不需要重复登录！", false);

			Intent i = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(i);
			finish();
		}

		// 检查GCM配置是否已经填写
		if (Constants.SERVER == null || Constants.SENDER_ID == null
				|| Constants.SERVER.length() == 0
				|| Constants.SENDER_ID.length() == 0) {
			// GCM 发送的 id / 服务端 url is 不存在
			alert.showAlertDialog(RegisterActivity.this, "配置错误!",
					"请设置你的服务段URL 以及 GCM 发送者ID", false);
			// 停止执行代码的回调
			return;
		}

		txtUsername = (EditText) findViewById(R.id.txtUsername);
		txtPassword = (EditText) findViewById(R.id.txtPassword);
		btnRegister = (Button) findViewById(R.id.btnRegister);

		/*
		 * 点击注册事件
		 */
		btnRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				String username = txtUsername.getText().toString();
				String password = txtPassword.getText().toString();

				// 检查用户提交的内容
				if (username.trim().length() > 0
						&& password.trim().length() > 0) {
					// 发送到 MainActivity
					Intent i = new Intent(getApplicationContext(),
							MainActivity.class);

					// 发送注册信息给 MainActivity
					i.putExtra("username", username);
					i.putExtra("password", password);
					startActivity(i);
					finish();
				} else {
					// 用户没有填写满表单
					// 让用户继续填写表单
					alert.showAlertDialog(RegisterActivity.this, "注册失败!",
							"请填写你的细节", false);
				}
			}
		});
	}
}
