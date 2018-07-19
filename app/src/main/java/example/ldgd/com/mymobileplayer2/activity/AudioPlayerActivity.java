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

import example.ldgd.com.mymobileplayer2.IMusicPlayerService;
import example.ldgd.com.mymobileplayer2.R;
import example.ldgd.com.mymobileplayer2.service.MusicPlayerService;
import example.ldgd.com.mymobileplayer2.util.Utils;


/**
 * Created by ldgd on 2018/7/10.
 * 功能：音乐播放界面
 * 说明：
 */

public class AudioPlayerActivity extends Activity implements View.OnClickListener {
    private static final int PROGRESS = 1;
    private ImageView ivIcon;
    //服务的代理类，通过它可以调用服务的方法
    private IMusicPlayerService service;
    private int position = 0;
    private Utils utils;

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

    private Handler MyHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case PROGRESS:

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

    private void initReceiver() {

        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayerService.OPENAUDIO);
        registerReceiver(receiver, intentFilter);

    }

    private void bindService() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction("com.ldgd.mobileplayer_OPENAUDIO");
        bindService(intent, con, Context.BIND_AUTO_CREATE);
        startService(intent);//不至于实例化多个服务
    }

    /**
     * 得到数据
     */
    private void getData() {
        position = getIntent().getIntExtra("position", 0);
    }

    private ServiceConnection con = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            //服务的代理类，通过它可以调用服务的方法
            service = IMusicPlayerService.Stub.asInterface(iBinder);

            if (service != null) {
                try {
                    service.openAudio(position);
                } catch (RemoteException e) {
                    e.printStackTrace();
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
        if (v == btnAudioPlaymode) {

        } else if (v == btnAudioPre) {  // 播放暂停
        } else if (v == btnAudioStartPause) {

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
        } else if (v == btnAudioNext) {
        } else if (v == btnLyrc) {

        }
    }


    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            showViewData();
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
        super.onDestroy();
        unregisterReceiver(receiver);

        if(con != null){
            unbindService(con);
            con = null;  // 让系统更快回收

        }

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
