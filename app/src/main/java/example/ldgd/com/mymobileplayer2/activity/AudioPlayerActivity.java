package example.ldgd.com.mymobileplayer2.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import example.ldgd.com.mymobileplayer2.IMusicPlayerService;
import example.ldgd.com.mymobileplayer2.R;
import example.ldgd.com.mymobileplayer2.domain.MediaItem;
import example.ldgd.com.mymobileplayer2.service.MusicPlayerService;
import example.ldgd.com.mymobileplayer2.util.LogUtil;
import example.ldgd.com.mymobileplayer2.util.LyricUtils;
import example.ldgd.com.mymobileplayer2.util.Utils;
import example.ldgd.com.mymobileplayer2.view.ShowLyricView;


/**
 * Created by ldgd on 2018/7/10.
 * 功能：音乐播放界面
 * 说明：
 */

public class AudioPlayerActivity extends Activity implements View.OnClickListener {
    private static final int PROGRESS = 1;
    private static final int SHOW_LYRIC = 2;
    private ImageView ivIcon;
    //服务的代理类，通过它可以调用服务的方法
    private IMusicPlayerService service;
    private int position = 0;
    private Utils utils;
    /**
     * 判断是否从notification进入音乐播放界面
     */
    private boolean notification;

    private TextView tvArtist;
    private TextView tvName;
    private LinearLayout llBottom;
    private Button btnAudioPlaymode;
    private Button btnAudioPre;
    private Button btnAudioStartPause;
    private Button btnAudioNext;
    private Button btnLyrc;
    private MyReceiver receiver;
    private SeekBar seekbarVoice;
    private TextView tvTime;
    /**
     * 自定义歌词类
     */
    private ShowLyricView showlyricview;
    /**
     *  服务Intent
     */
    private Intent musicPlayerServiceIntent;

    private Handler MyHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SHOW_LYRIC:  // 显示歌词

                    try {

                        // 得到当前进度
                        int progress = service.getCurrentPosition();
                        // 把进度传递到showLyricView控件，计算歌词显示位置
                        showlyricview.setShowNextLyric(progress);
                        // 实时更新歌词
                        MyHander.removeMessages(SHOW_LYRIC);
                        MyHander.sendEmptyMessage(SHOW_LYRIC);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
                case PROGRESS:  // 跟新播放界面

                    try {
                        // 设置当前进度
                        int progress = service.getCurrentPosition();
                        seekbarVoice.setProgress(progress);

                        // 设置当前进度时间
                        tvTime.setText(utils.stringForTime(service.getCurrentPosition()) + "/" + utils.stringForTime(service.getDuration()));

                        // 每秒更新
                        MyHander.sendEmptyMessageDelayed(PROGRESS, 1000);


                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }


                    break;
            }


        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        // 初始化view
        initView();
        // 初始化广播
        initReceiver();
        // 获取数据
        getData();
        // 设置动画
        setAnimation();
        // 绑定service
        bindService();

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false, priority = 0)
    public void onMessageEvent(MediaItem mediaItem) {

        // 显示当前播放信息
        showViewData();
        //发消息开始歌词同步
        showLyric();
        // 检测播放状态
        checkPlayMode();

    }

    private void showLyric() {

        LyricUtils lyricUtils = new LyricUtils();

        try {
            String path = service.getAudioPath();
            // 路径去掉“.mp3”
            path = path.substring(0, path.lastIndexOf("."));

            File file = new File(path + ".lrc");
            if (!file.exists()) {
                file = new File(path + ".txt");
            }
            lyricUtils.readLyricFile(file);

            // 把当前解析好的歌词集合设置到自定义控件中
            showlyricview.setLyrics(lyricUtils.getLyrics());

        } catch (RemoteException e) {
            e.printStackTrace();
        }


        // 存在歌词才刷新
        if (lyricUtils.isExistsLyric()) {
            MyHander.sendEmptyMessage(SHOW_LYRIC);
        }

    }

    private void initReceiver() {

        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayerService.OPENAUDIO);
        registerReceiver(receiver, intentFilter);

    }

    private void bindService() {
         musicPlayerServiceIntent = new Intent(this, MusicPlayerService.class);
        musicPlayerServiceIntent.setAction("com.ldgd.mobileplayer_OPENAUDIO");
        bindService(musicPlayerServiceIntent, con, Context.BIND_AUTO_CREATE);
        startService(musicPlayerServiceIntent);//不至于实例化多个服务
    }

    /**
     * 得到数据
     */
    private void getData() {
        position = getIntent().getIntExtra("position", 0);
        notification = getIntent().getBooleanExtra("notification", false);
    }

    private ServiceConnection con = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            //服务的代理类，通过它可以调用服务的方法
            service = IMusicPlayerService.Stub.asInterface(iBinder);

            if (!notification) {
                if (service != null) {
                    try {
                        service.openAudio(position);
                        // 检查当前播放模式
                        checkPlayMode();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            try {
                if (service != null) {
                    service.stop();
                    service = null;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private void checkPlayMode() {
        try {
            int playmode = service.getPlayMode();
            if (playmode == MusicPlayerService.REPEAT_ORDER) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);

            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);

            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);

            } else {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            }

            // 检测播放状态显示对应的播放暂停背景
            if (service.isPlaying()) {
                btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
            } else {
                btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    private void initView() {
        ivIcon = this.findViewById(R.id.iv_icon);
        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvName = (TextView) findViewById(R.id.tv_name);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        btnAudioPlaymode = (Button) findViewById(R.id.btn_audio_playmode);
        btnAudioPre = (Button) findViewById(R.id.btn_audio_pre);
        btnAudioStartPause = (Button) findViewById(R.id.btn_audio_start_pause);
        btnAudioNext = (Button) findViewById(R.id.btn_audio_next);
        btnLyrc = (Button) findViewById(R.id.btn_lyrc);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        tvTime = findViewById(R.id.tv_time);
        showlyricview = this.findViewById(R.id.showlyricview);

        btnAudioPlaymode.setOnClickListener(this);
        btnAudioPre.setOnClickListener(this);
        btnAudioStartPause.setOnClickListener(this);
        btnAudioNext.setOnClickListener(this);
        btnLyrc.setOnClickListener(this);
        seekbarVoice.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());

        utils = new Utils();


    }

    private void setAnimation() {
        ivIcon.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable rocketAnimation = (AnimationDrawable) ivIcon.getBackground();
        rocketAnimation.start();
    }


    @Override
    public void onClick(View v) {
        if (v == btnAudioPlaymode) {  // 播放模式

            setPlaymode();

        } else if (v == btnAudioPre) {  // 上一首

            try {
                service.pre();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        } else if (v == btnAudioStartPause) {// 播放暂停

            try {
                if (service.isPlaying()) {
                    btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                    service.pause();

                } else {
                    btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                    service.start();
                }

            } catch (RemoteException e) {

                e.printStackTrace();
            }
        } else if (v == btnAudioNext) { // 下一首

            try {
                service.next();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        } else if (v == btnLyrc) {

        }
    }

    /**
     * 设置播放模式
     */
    private void setPlaymode() {

        try {
            int playmode = service.getPlayMode();
            if (playmode == MusicPlayerService.REPEAT_ORDER) {
                //  btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
                playmode = MusicPlayerService.REPEAT_SINGLE;

            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {

                //btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
                playmode = MusicPlayerService.REPEAT_ALL;

            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                //  btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
                playmode = MusicPlayerService.REPEAT_ORDER;
            } else {
                playmode = MusicPlayerService.REPEAT_ORDER;
            }

            service.setPlayMode(playmode);

            //设置图片
            showPlaymode();

        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    private void showPlaymode() {
        try {
            int playmode = service.getPlayMode();
            if (playmode == MusicPlayerService.REPEAT_ORDER) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
                Toast.makeText(this, "顺序播放", Toast.LENGTH_SHORT).show();

            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
                Toast.makeText(this, "单曲循环", Toast.LENGTH_SHORT).show();

            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
                Toast.makeText(this, "全部循环", Toast.LENGTH_SHORT).show();
            } else {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
                Toast.makeText(this, "顺序播放", Toast.LENGTH_SHORT).show();
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            //  showViewData();
        }
    }

    private void showViewData() {

        try {
            // 设置当前播放音乐的名字和艺术家
            tvName.setText(service.getName());
            tvArtist.setText(service.getArtist());

            // 设置当前播放音乐的最大进度
            seekbarVoice.setMax(service.getDuration());

            // 通知hander每秒更新界面
            MyHander.sendEmptyMessage(PROGRESS);


        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {

        LogUtil.e("plays Activity onDestroy");

        MyHander.removeCallbacksAndMessages(null);

        // 取消广播接收者
        unregisterReceiver(receiver);
        // EventBus取消注册
        EventBus.getDefault().unregister(this);

        // 解绑服务
        if (con != null) {
            unbindService(con);
            stopService(musicPlayerServiceIntent);
            con = null;  // 让系统更快回收
        }
        super.onDestroy();


    }

    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                try {
                    service.seeto(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }


}
