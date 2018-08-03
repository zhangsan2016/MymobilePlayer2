package example.ldgd.com.mymobileplayer2.appliction;

import android.app.Application;

import com.iflytek.cloud.SpeechUtility;

import org.xutils.BuildConfig;
import org.xutils.x;

import example.ldgd.com.mymobileplayer2.R;


/**
 * Created by ldgd on 2018/7/6.
 * 功能：
 * 说明：
 */

public class MyAppliction extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        // XUtils3 初始化
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.

        // 讯飞语音听写初始化
        // 注意： appid 必须和下载的SDK保持一致，否则会出现10407错误
        SpeechUtility.createUtility(MyAppliction.this, "appid=" + getString(R.string.app_id));


    }
}
