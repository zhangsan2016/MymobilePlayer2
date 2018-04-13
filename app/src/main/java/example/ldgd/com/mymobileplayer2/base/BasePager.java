package example.ldgd.com.mymobileplayer2.base;

import android.content.Context;
import android.view.View;

/**
 * Created by ldgd on 2018/4/13.
 * 作用：基类，公共类，
 * 都继承该类
 * 实现初始化方法
 */

public abstract class BasePager {

    /**
     * 上下文
     */
    public final Context mContext;

    /**
     * 接收各个页面的实例
     */
    public View rootView;

    /**
     * 石否初始化数据
     */
    public boolean isInitData = true;


    public BasePager(Context context) {
        this.mContext = context;
        rootView = initView();
    }

    /**
     * 强制子页面实现该方法，实现想要的特定的效果
     */
    public abstract View initView();


    /**
     * 当子页面，需要绑定数据，或者联网请求数据并且绑定的时候，重写该方法
     */
    public void initData() {

    }


}
