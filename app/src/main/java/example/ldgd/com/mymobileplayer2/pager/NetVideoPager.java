package example.ldgd.com.mymobileplayer2.pager;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import example.ldgd.com.mymobileplayer2.R;
import example.ldgd.com.mymobileplayer2.adapter.NetVideoPagerAdapter;
import example.ldgd.com.mymobileplayer2.base.BasePager;
import example.ldgd.com.mymobileplayer2.domain.MediaItem;
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
    private TextView tv_nomedia;

    /**
     * 网络视频数据
     */
    private ArrayList<MediaItem> mediaItems;

    /**
     * NetVideoPager的适配器
     */
    private NetVideoPagerAdapter adapter;


    public NetVideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {

        View view = View.inflate(mContext, R.layout.netvideo_pager, null);
        // 当前view和xUtil3关联
        x.view().inject(NetVideoPager.this, view);

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
                processData(result);
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

    /**
     * 处理数据
     *
     * @param json
     */
    private void processData(String json) {

        mediaItems = parseJson(json);

        // 显示数据到View中
        showData();

    }

    private void showData() {

        if (mediaItems != null && mediaItems.size() > 0) {

            //设置适配器
            adapter = new NetVideoPagerAdapter(mContext, mediaItems);
            mListView.setAdapter(adapter);

        } else {

            // 没有数据显示文本
            tv_nomedia.setVisibility(View.VISIBLE);
        }

        // 隐藏加载框
        pb_loading.setVisibility(View.GONE);


    }

    /**
     * 解析json数据
     *
     * @param json
     * @return
     */
    private ArrayList<MediaItem> parseJson(String json) {
        ArrayList<MediaItem> mediaItems = new ArrayList<>();
        try {

            JSONObject jsoonObject = new JSONObject(json);
            JSONArray jsonArray = jsoonObject.getJSONArray("trailers");


            if (jsonArray != null && jsonArray.length() > 0) {

                for (int i = 0; i < jsonArray.length(); i++) {

                    MediaItem mediaItem = new MediaItem();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String movieName = jsonObject.getString("movieName");  // name
                    mediaItem.setName(movieName);
                    String videoTitle = jsonObject.getString("videoTitle"); // desc
                    mediaItem.setDesc(videoTitle);
                    String hightUrl = jsonObject.getString("hightUrl");  // data
                    mediaItem.setData(hightUrl);
                    String imageUrl = jsonObject.optString("coverImg");//imageUrl
                    mediaItem.setImageUrl(imageUrl);

                    // 添加到集合
                    mediaItems.add(mediaItem);

                    LogUtil.e("movieName = " + movieName + "\n videoTitle = " + videoTitle + "\n hightUrl = " + hightUrl + "\n imageUrl = " + imageUrl);

                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mediaItems;
    }
}
