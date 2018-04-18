package example.ldgd.com.mymobileplayer2.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.VideoView;

import example.ldgd.com.mymobileplayer2.R;

/**
 * Created by ldgd on 2018/4/17.
 * 系统播放器
 */
public class SystemVideoPlayerActivity extends Activity {
    /**
     * 当前播放器
     */
    private VideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // 初始化数据
        initData();

        // 设置监听
        setListener();




    }

    private void initData() {
        setContentView(R.layout.activity_system_video_player);

        videoView = this.findViewById(R.id.videoview);

        //得到播放地址
        Uri uri = getIntent().getData();//文件夹，图片浏览器，QQ空间

        if(uri != null){
            videoView.setVideoURI(uri);
        }
    }

    private void setListener() {
        //准备好的监听
        videoView.setOnPreparedListener(new MyOnPreparedListener());
        //播放完成了的监听
        videoView.setOnCompletionListener(new MyOnCompletionListener());
        // 出错的监听
        videoView.setOnErrorListener(new MyOnErrorListener());

        // 设置控制器
    //    videoView.setMediaController(new MediaController(this));
    }


    /**
     * 播放完成监听
     */
    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {

        }
    }

    /**
     *  准备好监听
     */
    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {

            //视频播放
            videoView.start();

        }
    }

    /**
     *  出错的监听
     */
    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            return false;
        }
    }


}
