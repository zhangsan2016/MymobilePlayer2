package example.ldgd.com.mymobileplayer2.activity;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import example.ldgd.com.mymobileplayer2.R;


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

        ivIcon = this.findViewById(R.id.iv_icon);
        ivIcon.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable rocketAnimation = (AnimationDrawable) ivIcon.getBackground();
        rocketAnimation.start();


    }
}
