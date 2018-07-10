package example.ldgd.com.mymobileplayer2.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import example.ldgd.com.mymobileplayer2.R;
import example.ldgd.com.mymobileplayer2.activity.SystemVideoPlayerActivity;
import example.ldgd.com.mymobileplayer2.adapter.VideoPagerAdapter;
import example.ldgd.com.mymobileplayer2.base.BasePager;
import example.ldgd.com.mymobileplayer2.domain.MediaItem;

/**
 * Created by ldgd on 2018/4/13.
 *  本地音频pager
 */

public class AudioPager extends BasePager {


    /**
     * 本地视屏集合
     */
    private ArrayList<MediaItem> mediaItems;
    /**
     * 获取所有video完成
     */
    private final static int GET_VIDEO_FINISH = 10;

    private ListView listview;
    private TextView tv_nomedia;
    private ProgressBar pb_loading;

    private VideoPagerAdapter videoPagerAdapter;

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case GET_VIDEO_FINISH:

                    if (mediaItems != null && mediaItems.size() > 0) {

                        // 设置adapter
                        videoPagerAdapter = new VideoPagerAdapter(mContext, mediaItems,false);
                        listview.setAdapter(videoPagerAdapter);

                    } else {
                        // 显示textview提示本地视屏为空
                        tv_nomedia.setVisibility(View.VISIBLE);
                    }
                    // 隐藏加载框
                    pb_loading.setVisibility(View.GONE);

                    break;
            }

        }
    };


    public AudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {

        View view = View.inflate(mContext, R.layout.audio_pager, null);

        listview = view.findViewById(R.id.lv_audio);
        tv_nomedia = view.findViewById(R.id.tv_nomedia);
        pb_loading = view.findViewById(R.id.pb_loading);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

           /*     // 调用android系统播放器
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(mediaItems.get(i).getData()),"video/mp4");
                mContext.startActivity(intent);*/

                /*// 调用自己的播放器
                Intent intent = new Intent(mContext,SystemVideoPlayerActivity.class);
                intent.setDataAndType(Uri.parse(mediaItems.get(i).getData()),"video/mp4");
                mContext.startActivity(intent);*/

                // 传递播放列表
                Intent intent = new Intent(mContext, SystemVideoPlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("videolist", mediaItems);
                intent.putExtras(bundle);
                intent.putExtra("position", position);
                mContext.startActivity(intent);

            }
        });

        return view;
    }


    @Override
    public void initData() {
        super.initData();

        getVideo();

    }

    public void getVideo() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 休眠两秒
                //  SystemClock.sleep(2000);
                ContentResolver resolver = mContext.getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//音频文件在sdcard中的名字
                        MediaStore.Audio.Media.DURATION,  //音频总时长
                        MediaStore.Audio.Media.SIZE,   //音频的文件大小
                        MediaStore.Audio.Media.DATA,   //音频播放地址
                        MediaStore.Audio.Media.ARTIST,  //歌曲的演唱者
                };

                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    mediaItems = new ArrayList<>();
                    while (cursor.moveToNext()) {

                        MediaItem mediaItem = new MediaItem();

                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                        mediaItem.setName(name);

                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                        mediaItem.setDuration(duration);

                        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                        mediaItem.setSize(size);

                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        mediaItem.setData(data);

                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ARTIST));
                        mediaItem.setArtist(artist);

                        mediaItems.add(mediaItem);
                    }
                    cursor.close();
                }
                //Handler发消息
                myHandler.sendEmptyMessage(GET_VIDEO_FINISH);
            }

        }).start();


    }
}
