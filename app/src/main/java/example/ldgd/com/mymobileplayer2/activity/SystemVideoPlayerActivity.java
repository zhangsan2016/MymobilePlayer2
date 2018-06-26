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
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import example.ldgd.com.mymobileplayer2.R;
import example.ldgd.com.mymobileplayer2.domain.MediaItem;
import example.ldgd.com.mymobileplayer2.util.Utils;
import example.ldgd.com.mymobileplayer2.view.MyVideoView;

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
     * 全屏
     */
    private static final int FULL_SCREEN = 11;
    /**
     * 默认
     */
    private static final int DEFAULT_SCREEN = 12;
    /**
     * 隐藏控制面板
     */
    private static final int HIDE_MEDIACONTROLLER = 13;

    /**
     * 当前播放器
     */
    private MyVideoView videoView;
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
    /**
     * 视屏控制面板
     */
    private RelativeLayout media_controller;

    /**
     * 传递过来的播放列表
     */
    private ArrayList<MediaItem> videolist;

    /**
     * 传递过来的播放位置
     */
    private int position;

    /**
     * 手势识别器
     */
    private GestureDetector gestureDetector;
    /**
     * 是否全屏
     */
    private boolean isFullScreen = false;
    /**
     * 屏幕宽
     */
    private int screenWidth = 0;
    /**
     * 屏幕高
     */
    private int screenHeight = 0;
    /**
     * video显示的宽
     */
    private int videoWidth;
    /**
     * video显示的高
     */
    private int videoHeight;
    /**
     * 是否显示控制面板
     */
    private boolean isshowMediaController = false;


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
                case HIDE_MEDIACONTROLLER:   // 隐藏播放器控制面板
                    hideMediaController();
                    break;
            }


        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video_player);

        // 初始化数据
        initData();

        // 设置监听
        setListener();

        getData();

        setData();


    }


    private String getSysteTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(System.currentTimeMillis());
    }

    private void initData() {

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
        media_controller = this.findViewById(R.id.media_controller);


        videoView = this.findViewById(videoview);

        utils = new Utils();

        // 获取当前屏幕宽高
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

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

        gestureDetector = new GestureDetector(this, new MyOnGestureListener());

        // 设置控制器
        //    videoView.setMediaController(new MediaController(this));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void getData() {

        //得到播放地址
        uri = getIntent().getData();
        // 获取播放列表
        videolist = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        // 获取播放位置
        position = getIntent().getIntExtra("position", 0);


    }

    private void setData() {

        if (videolist != null && videolist.size() > 0) {
            MediaItem mediaItem = videolist.get(position);
            videoView.setVideoPath(mediaItem.getData());
            //设置视频的名称
            tvVideoName.setText(mediaItem.getName());

        } else if (uri != null) {
            videoView.setVideoURI(uri);
            //设置视频的名称
            tvVideoName.setText(uri.toString());
        } else {
            Toast.makeText(this, "播放地址为NULL！", Toast.LENGTH_SHORT).show();
        }

        setButtonState();

    }

    /**
     * 设置播放按钮状态
     */
    private void setButtonState() {

        if (videolist != null && videolist.size() > 0) {

            if (videolist.size() == 1) {
                setEnable(false);
            } else if (videolist.size() == 2) {
                if (position == 0) {
                    // 在播放列表长度为2时，position在第1个视屏时上一个点击按钮不能被点击，下一个可点击
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);
                } else if (position == 1) {
                    // 在播放列表长度为2时，position在第2个视屏时上一个点击按钮能点击，下一个不可点击
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                    btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    btnVideoPre.setEnabled(true);
                }
            } else {

                // 判断是否最后一个
                if (position == videolist.size() - 1) {
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                } else if (position == 0) {
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);

                } else {
                    //两个按钮设置可点击
                    setEnable(true);
                }

            }

        } else if (uri != null) {
            //两个按钮设置灰色
            setEnable(false);
        }

    }

    private void setEnable(boolean isEnable) {

        if (isEnable) {
            btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
            btnVideoPre.setEnabled(true);
            btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
            btnVideoNext.setEnabled(true);
        } else {
            //两个按钮设置灰色
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoPre.setEnabled(false);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            btnVideoNext.setEnabled(false);
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

            //  videoView.setVideoSize(200, 200);

            // 获取屏幕宽高
            videoWidth = mediaPlayer.getVideoWidth();
            videoHeight = mediaPlayer.getVideoHeight();

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
        } else if (v == btnVideoPre) { // 上一个

            playPreVideo();

        } else if (v == btnVideoStartPause) {  // 播放暂停
            setStartPause();

        } else if (v == btnVideoNext) {  // 下一个
            playNextVideo();
        } else if (v == btnVideoSiwchScreen) { // 切换屏幕大小（默认、全屏）
            setFullScreenAndDefault();
        }

        //发消息隐藏视频控制面板
        myHandler.removeMessages(HIDE_MEDIACONTROLLER);
        myHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
    }

    private void setStartPause() {
        if (videoView.isPlaying()) {
            // 视屏在播放 -- 设置暂停
            videoView.pause();
            // 按钮状态设置为播放
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
        } else {
            // 视屏播放
            videoView.start();
            // 按钮状态设置为暂停
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    /**
     * 播放上一个视屏
     */
    private void playPreVideo() {

        if (videolist != null && videolist.size() > 0) {
            position--;
            if (position >= 0) {
                MediaItem mediaItem = videolist.get(position);
                videoView.setVideoPath(mediaItem.getData());
                tvVideoName.setText(mediaItem.getName());
                setButtonState();
            }
        } else if (uri != null) {
            // 上一个下一个按钮设置为灰色，并且不可点击
            setButtonState();

        }
    }

    /**
     * 播放下一个视屏
     */
    private void playNextVideo() {

        if (videolist != null && videolist.size() > 0) {

            position++;
            if (position < videolist.size()) {
                MediaItem mediaItem = videolist.get(position);
                videoView.setVideoPath(mediaItem.getData());
                tvVideoName.setText(mediaItem.getName());
                setButtonState();
            }


        } else if (uri != null) {

            // 上一个下一个按钮设置为灰色，并且不可点击
            setButtonState();

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

            myHandler.removeMessages(HIDE_MEDIACONTROLLER);

        }

        /**
         * 当手指离开的时候回调这个方法
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            myHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
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
        } else if (level <= 20) {
            tvBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            tvBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            tvBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            tvBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level < 100) {
            tvBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            tvBattery.setImageResource(R.drawable.ic_battery_10);
        }


    }

    private class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            if (isshowMediaController) {
                hideMediaController();
                //把隐藏消息移除
                myHandler.removeMessages(HIDE_MEDIACONTROLLER);

            } else {

                //显示
                showMediaController();
                //发消息隐藏
                myHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
            }

            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            // 播放暂停
            setStartPause();

        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            setFullScreenAndDefault();
            return super.onDoubleTapEvent(e);

        }
    }

    /**
     * 隐藏控制面板
     */
    private void hideMediaController() {

        media_controller.setVisibility(View.GONE);
        isshowMediaController = false;
    }

    /**
     * 显示控制面板
     */
    private void showMediaController() {
        media_controller.setVisibility(View.VISIBLE);
        isshowMediaController = true;
    }


    /**
     * 设置全屏或者默认
     */
    private void setFullScreenAndDefault() {

        if (!isFullScreen) {
            setVideoType(FULL_SCREEN);
        } else {
            setVideoType(DEFAULT_SCREEN);
        }

    }

    private void setVideoType(int defaultScreen) {

        switch (defaultScreen) {
            case FULL_SCREEN:
                // 设置视屏画面大小-屏幕有多大就显示多大
                videoView.setVideoSize(screenWidth, screenHeight);
                //设置按钮状态为默认
                btnVideoSiwchScreen.setBackgroundResource(R.drawable.btn_video_siwch_screen_default_selector);
                isFullScreen = true;

                break;
            case DEFAULT_SCREEN:

                // 当前屏幕的宽高
                int width = screenWidth;
                int height = screenHeight;

                // 当前播放视屏的宽高
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;

                // for compatibility, we adjust size based on aspect ratio
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }

                videoView.setVideoSize(width, height);
                // 设置按钮状态为全屏
                btnVideoSiwchScreen.setBackgroundResource(R.drawable.btn_video_siwch_screen_full_selector);
                isFullScreen = false;
                break;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}