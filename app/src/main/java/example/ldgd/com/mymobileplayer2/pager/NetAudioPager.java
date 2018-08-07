package example.ldgd.com.mymobileplayer2.pager;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import example.ldgd.com.mymobileplayer2.R;
import example.ldgd.com.mymobileplayer2.adapter.NetAudioPagerAdapter;
import example.ldgd.com.mymobileplayer2.base.BasePager;
import example.ldgd.com.mymobileplayer2.domain.NetAudioPagerData;
import example.ldgd.com.mymobileplayer2.util.CacheUtils;
import example.ldgd.com.mymobileplayer2.util.Constants;
import example.ldgd.com.mymobileplayer2.util.LogUtil;


/**
 * Created by ldgd on 2018/4/13.
 */

public class NetAudioPager extends BasePager {

    @ViewInject(R.id.listview)
    private ListView mListView;


    @ViewInject(R.id.tv_nonet)
    private TextView tv_nonet;


    @ViewInject(R.id.pb_loading)
    private ProgressBar pb_loading;

    /**
     * 页面的数据
     */
    private List<NetAudioPagerData.ListBean> datas;
    private NetAudioPagerAdapter netAudioPagerAdapter;

    public NetAudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {

        View view = View.inflate(mContext, R.layout.netaudio_pager, null);
        x.view().inject(NetAudioPager.this, view);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络音频的数据被初始化了。。。");

        String savaJson = CacheUtils.getString(mContext, Constants.ALL_RES_URL);
        if (!TextUtils.isEmpty(savaJson)) {
            // 解析数据
            processData(savaJson);
        }
        // 联网获取数据
        getDataFromNet();

    }

    private void processData(String json) {
        // 解析数据
        NetAudioPagerData data = parsedJson(json);
        datas =  data.getList();

        if(datas != null && datas.size() > 0){
            // 有数据
            tv_nonet.setVisibility(View.GONE);
            // 设置适配器
             netAudioPagerAdapter = new NetAudioPagerAdapter(mContext,datas);
            mListView.setAdapter(netAudioPagerAdapter);

        }else{
            tv_nonet.setText("没有对应的数据....");
            //没有数据
            tv_nonet.setVisibility(View.VISIBLE);
        }
        pb_loading.setVisibility(View.GONE);
    }
    /**
     * Gson解析数据
     * @param json
     * @return
     */
    private NetAudioPagerData parsedJson(String json) {
        return new Gson().fromJson(json,NetAudioPagerData.class);
    }

    public void getDataFromNet() {
        RequestParams params = new RequestParams(Constants.ALL_RES_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                LogUtil.e("请求数据成功==" + result);
                //保持数据
                CacheUtils.putString(mContext, Constants.ALL_RES_URL, result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("请求数据失败==" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished");
            }
        });

    }
}
