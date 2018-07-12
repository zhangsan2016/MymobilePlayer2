package example.ldgd.com.mymobileplayer2.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import example.ldgd.com.mymobileplayer2.IMusicPlayerService;
import example.ldgd.com.mymobileplayer2.R;
import example.ldgd.com.mymobileplayer2.service.MusicPlayerService;


/**
 * Created by ldgd on 2018/7/10.
 * 功能：音乐播放界面
 * 说明：
 */

public class AudioPlayerActivity extends Activity {
    private ImageView ivIcon;
    //服务的代理类，通过它可以调用服务的方法
    private IMusicPlayerService service;
    private int position = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        // 初始化view
        initView();
        // 获取数据
        getData();
        // 设置动画
        setAnimation();
        // 绑定service
        bindService();

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
            IMusicPlayerService service = IMusicPlayerService.Stub.asInterface(iBinder);

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

    }

    private void setAnimation() {
        ivIcon.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable rocketAnimation = (AnimationDrawable) ivIcon.getBackground();
        rocketAnimation.start();
    }
}
