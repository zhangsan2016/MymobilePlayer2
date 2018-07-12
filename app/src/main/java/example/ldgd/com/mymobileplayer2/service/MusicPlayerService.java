package example.ldgd.com.mymobileplayer2.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by ldgd on 2018/7/10.
 * 功能： 音乐播放服务
 * 说明：
 */

public class MusicPlayerService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




}
