package example.ldgd.com.mymobileplayer2.pager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import example.ldgd.com.mymobileplayer2.activity.SystemVideoPlayerActivity;
import example.ldgd.com.mymobileplayer2.adapter.NetVideoPagerAdapter;
import example.ldgd.com.mymobileplayer2.base.BasePager;
import example.ldgd.com.mymobileplayer2.domain.MediaItem;
import example.ldgd.com.mymobileplayer2.util.Constants;
import example.ldgd.com.mymobileplayer2.util.LogUtil;
import example.ldgd.com.mymobileplayer2.util.Utils;
import example.ldgd.com.mymobileplayer2.xListView.XListView;

/**
 * Created by ldgd on 2018/4/13.
 */

public class NetVideoPager extends BasePager {

    @ViewInject(R.id.lv_netvideo)
    private XListView mListView;

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
    /**
     * 是否获取更多
     */
    private boolean isLoadMore = false;


    public NetVideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {

        View view = View.inflate(mContext, R.layout.netvideo_pager, null);
        // 当前view和xUtil3关联
        x.view().inject(NetVideoPager.this, view);

        // 初始化XListView
        mListView = (XListView) view.findViewById(R.id.lv_netvideo);
        mListView.setPullLoadEnable(true);
        mListView.setOnItemClickListener(new MyOnItemClickListener());
        mListView.setXListViewListener(new MyIXListViewListener());

        return view;
    }

    private class MyIXListViewListener implements XListView.IXListViewListener {

        @Override
        public void onRefresh() {
            getDataFromNet();
        }

        @Override
        public void onLoadMore() {
            isLoadMore = true;
            getDataFromNet();

        }
    }

    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(Utils.getSysteTime());
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
                isLoadMore = false;
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("联网取消==" + cex.getMessage());
                isLoadMore = false;
            }

            @Override
            public void onFinished() {
                LogUtil.e("联网结束");
                isLoadMore = false;

            }
        });

    }

    /**
     * 处理数据
     *
     * @param json
     */
    private void processData(String json) {

        if (!isLoadMore) {
            mediaItems = parseJson(json);
            showData();

        } else {
            //加载更多
            //要把得到更多的数据，添加到原来的集合中
//            ArrayList<MediaItem> moreDatas = parseJson(json);
            isLoadMore = false;
            mediaItems.addAll(parseJson(json));
            //刷新适配器
            adapter.notifyDataSetChanged();
            onLoad();
        }


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
        onLoad();


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

                    // jsonObject.opt 获取数据不存在不报错
                    MediaItem mediaItem = new MediaItem();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String movieName = jsonObject.optString("movieName");  // name
                    mediaItem.setName(movieName);
                    String videoTitle = jsonObject.optString("videoTitle"); // desc
                    mediaItem.setDesc(videoTitle);
                    String hightUrl = jsonObject.optString("hightUrl");  // data
                    mediaItem.setData(hightUrl);
                    String imageUrl = jsonObject.optString("coverImg");//imageUrl
                    mediaItem.setImageUrl(imageUrl);

                    //     LogUtil.e("movieName = " + movieName + "\n videoTitle = " + videoTitle + "\n hightUrl = " + hightUrl + "\n imageUrl = " + imageUrl);

                    // 添加到集合
                    mediaItems.add(mediaItem);

                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mediaItems;
    }

    private class MyOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            LogUtil.e("onItemClick = " + position);
            // 传递播放列表
            Intent intent = new Intent(mContext, SystemVideoPlayerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist", mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position", (position-1));
            mContext.startActivity(intent);
        }
    }
}
