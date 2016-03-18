package com.jiang.android.rxjavaapp.flux.action;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class CommonAction {
    public static void shareText(Activity activity, View view) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hi,我正在学习RxJava,推荐你下载这个app一起学习吧 https://github.com/jiang111/RxJavaApp/releases");
        shareIntent.setType("text/plain");
        activity.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }
}
