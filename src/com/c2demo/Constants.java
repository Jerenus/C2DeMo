package com.c2demo;

import android.content.Context;
import android.content.Intent;

public final class Constants {
	
	// 定义Server地址以及EndPoint名称。
    public static final String SERVER = ""; 
    public static final String ENDPOINT = ""; 

    // 谷歌项目ID
    public static final String SENDER_ID = ""; 

    /**
     * Log 消息的标签
     */
    public static final String TAG = "GCM DEMO";

    public static final String DISPLAY_MESSAGE_ACTION =
            "com.c2demo.DISPLAY_MESSAGE";

    public static final String EXTRA_MESSAGE = "message";

    /**
     * 通知UI显示一条消息。
     * <p>
     * 这个方法定义在Constants里的原因是它即被用在了UI，也被后台服务所使用。
     *
     * @param context 应用的 context。
     * @param message 需要显示的消息。
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
