package example.ldgd.com.mymobileplayer2.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.text.SimpleDateFormat;

import example.ldgd.com.mymobileplayer2.R;
import example.ldgd.com.mymobileplayer2.util.Utils;

import static example.ldgd.com.mymobileplayer2.R.id.iv_system_time;
import static example.ldgd.com.mymobileplayer2.R.id.tv_battery;
import static example.ldgd.com.mymobileplayer2.R.id.tv_current_time;
import static example.ldgd.com.mymobileplayer2.R.id.tv_duration;
import static example.ldgd.com.mymobileplayer2.R.id.videoview;


/**
 * Created by ldgd on 2018/4/17.
 * 系统播放器
 */
public class SystemVideoPlayerActivity extends Activity implements View.OnClickListener {
    private static final int PROGRESS = 10;
    /**
     * 当前播放器
     */
    private VideoView videoView;
    private Utils utils;
    private Uri uri;

    private TextView tvVideoName;
    private ImageView tvBattery;
    private TextView ivSystemTime;
    private ImageButton btnVoice;
    private SeekBar seekbarVoice;
    private ImageButton btnSwichPlayer;
    private TextView tvCurrentTime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnVideoPre;
    private Button btnVideoStartPause;
    private Button btnVideoNext;
    private Button btnVideoSiwchScreen;


    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case PROGRESS: // 更新进度


                    // 设置视频当前进度
                    int currentPosition = videoView.getCurrentPosition();
                    seekbarVideo.setProgress(currentPosition);

                    //  更新文本播放进度
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));

                    // 更新当前系统时间
                    ivSystemTime.setText(getSysteTime());


                    // 每秒更新一次
                    myHandler.removeMessages(PROGRESS);
                    myHandler.sendEmptyMessageDelayed(PROGRESS, 1000);


                    break;
            }


        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // 初始化数据
        initData();

        // 设置监听
        setListener();

        getData();

        //
        setData();


    }

    private void setData() {
        //设置视频的名称
        tvVideoName.setText(uri.toString());//设置视频的名称
    }

    private String getSysteTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(System.currentTimeMillis());
    }

    private void initData() {
        setContentView(R.layout.activity_system_video_player);
        tvVideoName = (TextView) findViewById(R.id.tv_video_name);
        tvBattery = (ImageView) findViewById(tv_battery);
        ivSystemTime = (TextView) findViewById(iv_system_time);
        btnVoice = (ImageButton) findViewById(R.id.btn_voice);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        btnSwichPlayer = (ImageButton) findViewById(R.id.btn_swich_player);
        tvCurrentTime = (TextView) findViewById(tv_current_time);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(tv_duration);
        btnExit = (Button) findViewById(R.id.btn_exit);
        btnVideoPre = (Button) findViewById(R.id.btn_video_pre);
        btnVideoStartPause = (Button) findViewById(R.id.btn_video_start_pause);
        btnVideoNext = (Button) findViewById(R.id.btn_video_next);
        btnVideoSiwchScreen = (Button) findViewById(R.id.btn_video_siwch_screen);


        videoView = this.findViewById(videoview);

        utils = new Utils();

    }

    private void setListener() {
        //准备好的监听
        videoView.setOnPreparedListener(new MyOnPreparedListener());
        //播放完成了的监听
        videoView.setOnCompletionListener(new MyOnCompletionListener());
        // 出错的监听
        videoView.setOnErrorListener(new MyOnErrorListener());
        //设置SeeKbar状态变化的监听
        seekbarVideo.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());

        // 设置电量变化监听
        BatteryReceiver batteryReceiver = new BatteryReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, intentFilter);


        btnVoice.setOnClickListener(this);
        btnSwichPlayer.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnVideoPre.setOnClickListener(this);
        btnVideoStartPause.setOnClickListener(this);
        btnVideoNext.setOnClickListener(this);
        btnVideoSiwchScreen.setOnClickListener(this);
        seekbarVideo.setOnClickListener(this);


        // 设置控制器
        //    videoView.setMediaController(new MediaController(this));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void getData() {

        //得到播放地址
        uri = getIntent().getData();//文件夹，图片浏览器，QQ空间

        if (uri != null) {
            videoView.setVideoURI(uri);
        }
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
     * 准备好监听
     */
    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {

            //视频播放
            videoView.start();

            // 给seekbar设置视频总长度
            seekbarVideo.setMax(videoView.getDuration());
            // 给文本设置总时长
            tvDuration.setText(utils.stringForTime(videoView.getDuration()));
            // 启动Handler更新
            myHandler.sendEmptyMessage(PROGRESS);

        }
    }

    /**
     * 出错的监听
     */
    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            return false;
        }
    }


    @Override
    public void onClick(View v) {
        if (v == btnVoice) {
            // Handle clicks for btnVoice
        } else if (v == btnSwichPlayer) {
            // Handle clicks for btnSwichPlayer
        } else if (v == btnExit) {
            this.finish();
        } else if (v == btnVideoPre) {
        } else if (v == btnVideoStartPause) {
            // Handle clicks for btnVideoStartPause
        } else if (v == btnVideoNext) {
            // Handle clicks for btnVideoNext
        } else if (v == btnVideoSiwchScreen) {
            // Handle clicks for btnVideoSiwchScreen
        }
    }


    class VideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        /**
         * 当手指滑动的时候，会引起SeekBar进度变化，会回调这个方法
         *
         * @param seekBar
         * @param progress
         * @param fromUser 如果是用户引起的true,不是用户引起的false
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                videoView.seekTo(progress);
            }
        }

        /**
         * 当手指触碰的时候回调这个方法
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        /**
         * 当手指离开的时候回调这个方法
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    @Override
    protected void onDestroy() {

        //移除所有的消息
        myHandler.removeCallbacksAndMessages(null);

        super.onDestroy();

    }

    private class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            int level = intent.getIntExtra("level", 0);

            // 设置电池电量
            setBattery(level);

        }
    }

    /**
     * 设置电池电量
     *
     * @param level
     */
    private void setBattery(int level) {

        if (level <= 10) {
            tvBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <=  20) {
            tvBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <=  40) {
            tvBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <=  60) {
            tvBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <=  80) {
            tvBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level < 100) {
            tvBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            tvBattery.setImageResource(R.drawable.ic_battery_10);
        }


    }
}
