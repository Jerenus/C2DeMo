package com.c2demo;

import android.app.AlertDialog;
import android.content.Context;

public class AlertDialogManager {
	/**
	 * 提供一个简单的提示框
	 * @param context - 应用的 context
	 * @param title - 弹窗提醒标题
	 * @param message - 弹窗提醒内容消息
	 * @param status - 成功/失败（用来设置小图标）
	 * 				 - 如果你不需要就把这个参数留空
	 * */
	public void showAlertDialog(Context context, String title, String message,
			Boolean status) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		// 设定弹窗标题
		alertDialog.setTitle(title);

		// 设定弹窗内容
		alertDialog.setMessage(message);

		if(status != null)
			// 设定弹窗小图标
			alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

		// 显示弹窗
		alertDialog.show();
	}
}
