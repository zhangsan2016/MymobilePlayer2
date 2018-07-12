package example.ldgd.com.mymobileplayer2.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import example.ldgd.com.mymobileplayer2.R;
import example.ldgd.com.mymobileplayer2.service.MusicPlayerService;


/**
 * Created by ldgd on 2018/7/10.
 * 功能：音乐播放界面
 * 说明：
 */

public class AudioPlayerActivity extends Activity {
    private ImageView ivIcon;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        initView();
        setAnimation();
        bindService();

    }

    private void bindService() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        bindService(intent, con, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection con = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private void initView() {
        ivIcon = this.findViewById(R.id.iv_icon);

    }

    private void setAnimation() {
        ivIcon.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable rocketAnimation = (AnimationDrawable) ivIcon.getBackground();
        rocketAnimation.start();
    }
}
