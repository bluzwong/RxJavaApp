package com.jiang.android.rxjavaapp.flux.feature.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import com.github.bluzwong.myflux.lib.FluxResponse;
import com.github.bluzwong.myflux.lib.switchtype.ReceiveType;
import com.jiang.android.rxjavaapp.R;
import com.jiang.android.rxjavaapp.common.CommonString;
import com.jiang.android.rxjavaapp.common.SPKey;
import com.jiang.android.rxjavaapp.flux.feature.main.MainActivity;
import com.jiang.android.rxjavaapp.utils.SharePrefUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import flux.Flux;

import static com.jiang.android.rxjavaapp.flux.feature.launcher.Type.*;
public class LauncherActivity extends AppCompatActivity {


    ImageView mSplash;
    LauncherRequester requester;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        requester = Flux.getRequester(this, LauncherRequester.class);

        mSplash = (ImageView) findViewById(R.id.splash_index);
        ImageLoader.getInstance().displayImage(CommonString.SPLASH_INDEX_URL, mSplash);
        if (SharePrefUtil.getBoolean(this, SPKey.FIRST_ENTER, true)) {
            requester.fillData();
        } else {
            startMainActivity();
        }
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @ReceiveType(type = FILL_DATA_OK)
    public void fillDataOK() {
        SharePrefUtil.saveBoolean(this, SPKey.FIRST_ENTER, false);
        startMainActivity();
    }

    @ReceiveType(type = FILL_DATA_FAIL)
    public void fillDataFAIL(FluxResponse response) {
        Exception exception = response.getOnly();
        Snackbar.make(mSplash, exception.getMessage(), Snackbar.LENGTH_LONG).show();
    }
}
