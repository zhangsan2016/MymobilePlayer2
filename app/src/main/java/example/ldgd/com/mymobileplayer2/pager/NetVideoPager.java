package example.ldgd.com.mymobileplayer2.pager;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import example.ldgd.com.mymobileplayer2.R;
import example.ldgd.com.mymobileplayer2.base.BasePager;
import example.ldgd.com.mymobileplayer2.util.Constants;
import example.ldgd.com.mymobileplayer2.util.LogUtil;

/**
 * Created by ldgd on 2018/4/13.
 */

public class NetVideoPager extends BasePager {

    @ViewInject(R.id.lv_netvideo)
    private ListView mListView;

    @ViewInject(R.id.pb_loading)
    private ProgressBar pb_loading;

    @ViewInject(R.id.tv_nomedia)
    private ProgressBar tv_nomedia;


    public NetVideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {

        View view = View.inflate(mContext, R.layout.netvideo_pager, null);
        // 当前view和xUtil3关联
        x.view().inject(this, view);

        return view;
    }

    @Override
    public void initData() {
        super.initData();

        LogUtil.e("网络视频的数据被初始化了。。。");
        getDataFromNet();


    }

    private void getDataFromNet() {
        // 获取网络视频
        RequestParams params = new RequestParams(Constants.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("联网成功==" + result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("联网失败==" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("联网取消==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("联网结束");

            }
        });

    }
}
