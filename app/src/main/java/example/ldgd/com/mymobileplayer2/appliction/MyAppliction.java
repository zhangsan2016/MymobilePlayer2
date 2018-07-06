package example.ldgd.com.mymobileplayer2.appliction;

import android.app.Application;

import org.xutils.BuildConfig;
import org.xutils.x;


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


    }
}
