package example.ldgd.com.mymobileplayer2.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;

import example.ldgd.com.mymobileplayer2.IMusicPlayerService;
import example.ldgd.com.mymobileplayer2.R;
import example.ldgd.com.mymobileplayer2.activity.AudioPlayerActivity;
import example.ldgd.com.mymobileplayer2.domain.MediaItem;
import example.ldgd.com.mymobileplayer2.util.CacheUtils;

/**
 * Created by ldgd on 2018/7/10.
 * 功能： 音乐播放服务
 * 说明：
 */

public class MusicPlayerService extends Service {
    public static final String OPENAUDIO = "com.ldgd.mobileplayer_OPENAUDIO";
    /**
     * 顺序播放
     */
    public static final int REPEAT_ORDER = 1;  // 播放到最后一首，不会重新从0开始
    /**
     * 单曲循环
     */
    public static final int REPEAT_SINGLE = 2;
    /**
     * 全部循环
     */
    public static final int REPEAT_ALL = 3;
    /**
     * 播放模式
     */
    private int playerMode = REPEAT_ORDER;

    /**
     * 音乐播放器
     */
    private MediaPlayer mediaPlayer;
    /**
     * 本地音频集合
     */
    private ArrayList<MediaItem> mediaItems;
    /**
     * 当前播放位置
     */
    private int position;
    /**
     * 当前播放的音频文件对象
     */
    private MediaItem mediaItem;
    /**
     * 消息管理器
     */
    private NotificationManager notificationManager;


    @Override
    public void onCreate() {
        super.onCreate();

        // 获取播放模式
        this.playerMode = CacheUtils.getPlaymode(this, "playmode");
        // 加载音乐列表
        getDataFromLocal();

    }

    private void getDataFromLocal() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 休眠两秒
                //  SystemClock.sleep(2000);
                ContentResolver resolver = getContentResolver();
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
            }

        }).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    private IMusicPlayerService.Stub stub = new IMusicPlayerService.Stub() {

        MusicPlayerService service = MusicPlayerService.this;


        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }

        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public void stop() throws RemoteException {
            service.stop();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public String getName() throws RemoteException {
            return service.getName();
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return service.getAudioPath();
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        @Override
        public void setPlayMode(int playMode) throws RemoteException {
            service.setPlayMode(playMode);
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return service.getPlayMode();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return service.isPlaying();
        }

        @Override
        public void seeto(int progress) throws RemoteException {
            mediaPlayer.seekTo(progress);
        }

        @Override
        public int getAudioSessionId() throws RemoteException {
            return mediaPlayer.getAudioSessionId();
        }


    };


    /**
     * 根据位置打开对应的音频文件，并播放
     */
    private void openAudio(int position) {
        // 保存当前位置
        this.position = position;
        // 获取当前位置音频对象
        mediaItem = mediaItems.get(position);

        if (mediaItems != null && mediaItems.size() > 0) {
            if (mediaPlayer != null) {
                //    mediaPlayer.release();
                mediaPlayer.reset();
            }
            try {
                mediaPlayer = new MediaPlayer();
                //设置监听：播放出错，播放完成，准备好
                mediaPlayer.setOnPreparedListener(new MyOnPreparedListener()); // 准备完成监听
                mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());  // 播放完成监听
                mediaPlayer.setOnErrorListener(new MyOnErrorListener());  // 播放出错监听

                // 设置资源文件
                mediaPlayer.setDataSource(mediaItem.getData());
                // 异步
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(this, "本地音频列表为空！", Toast.LENGTH_SHORT).show();
        }
    }

    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {

            start();

            // 使用EventBus回调
            EventBus.getDefault().post(new MediaItem());

            // 发送广播
         /*   Intent intent = new Intent(OPENAUDIO);
            sendBroadcast(intent);*/


        }
    }

    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            next();
            return false;
        }
    }

    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            next();
        }
    }


    /**
     * 播放音乐
     */
    private void start() {

        // 播放音乐
        mediaPlayer.start();

        String id = "my_channel_01";
        String name = "我是渠道名字";
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // 使用  NotificationCompat兼容所有版本
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // 8.0以上版本需要创建channel
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);

            Intent intent = new Intent(this, AudioPlayerActivity.class);
            intent.putExtra("notification", true);//标识来自状态拦
            PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
            notification = new NotificationCompat.Builder(this, "PUSH_NOTIFY_ID")
                    .setContentIntent(pi)
                    .setContentTitle("我的音乐")
                    .setContentText("正在播放：" + getName())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setChannelId(id) // channel id
                    .build();
        } else {
            // 使用  NotificationCompat兼容所有版本
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            Intent intent = new Intent(this, AudioPlayerActivity.class);
            intent.putExtra("notification", true);//标识来自状态拦
            PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
            notification = new NotificationCompat.Builder(this, "PUSH_NOTIFY_ID")
                    .setContentIntent(pi)
                    .setContentTitle("我的音乐")
                    .setContentText("正在播放：" + getName())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();

        }
        notificationManager.notify(1, notification);


    }

    /**
     * 暂停音乐
     */
    private void pause() {
        mediaPlayer.pause();
        notificationManager.cancel(1);
    }

    /**
     * 停止音乐
     */
    private void stop() {
        mediaPlayer.stop();
    }

    @Override
    public void onDestroy() {
        notificationManager.cancel(1);
        super.onDestroy();
    }



    /**
     * 得到当前的播放进度
     *
     * @return
     */
    private int getCurrentPosition() {

        return mediaPlayer.getCurrentPosition();
    }

    /**
     * 得到当前播放的总时长
     *
     * @return
     */
    private int getDuration() {

        return mediaPlayer.getDuration();
    }

    /**
     * 得到艺术家
     *
     * @return
     */
    private String getArtist() {

        return mediaItem.getArtist();
    }

    private String getName() {

        return mediaItem.getName();
    }

    /**
     * 得到歌曲播放的路径
     *
     * @return
     */
    private String getAudioPath() {

        return mediaItem.getData();
    }


    /**
     * 播放下一个音频
     */
    private void next() {
        // 根据当前的播放模式设置下一个播放的位置
        // 获取下一个播放位置的位置
        setNextPosition();

        // 根据模式下标播放
        openNextAudio();

    }


    private void setNextPosition() {
        try {
            int playmode = getPlayMode();
            if (playmode == MusicPlayerService.REPEAT_ORDER) {

                position++;

            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                position++;
                if (position >= mediaItems.size()) {
                    position = 0;
                }


            } else if (playmode == MusicPlayerService.REPEAT_ALL) {

                position++;
                if (position >= mediaItems.size()) {
                    position = 0;
                }

            } else {
                position++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void openNextAudio() {
        try {
            int playmode = getPlayMode();
            if (playmode == MusicPlayerService.REPEAT_ORDER) {
                if (position >= mediaItems.size()) {
                    position = mediaItems.size() - 1;
                }
                openAudio(position);

            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {

                openAudio(position);

            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                openAudio(position);
            } else {
                if (position >= mediaItems.size()) {
                    position = 0;
                }
                openAudio(position);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放上一个音频
     */
    private void pre() {

        // 根据当前的播放模式设置上一个播放的位置
        // 获取上一个播放位置的位置
        setPosition();

        // 根据模式上标播放
        openPreAudio();

    }

    private void openPreAudio() {
        try {
            int playmode = getPlayMode();
            if (playmode == MusicPlayerService.REPEAT_ORDER) {
                if (position < 0) {
                    position = 0;
                }
                openAudio(position);

            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {

                openAudio(position);

            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                openAudio(position);
            } else {
                if (position < 0) {
                    position = 0;
                }
                openAudio(position);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPosition() {
        try {
            int playmode = getPlayMode();
            if (playmode == MusicPlayerService.REPEAT_ORDER) {

                position--;

            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                position--;
                if (position < 0) {
                    position = mediaItems.size() - 1;
                }


            } else if (playmode == MusicPlayerService.REPEAT_ALL) {

                position--;
                if (position < 0) {
                    position = mediaItems.size() - 1;
                }

            } else {
                position--;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置播放模式
     */
    private void setPlayMode(int playMode) {

        this.playerMode = playMode;
        // 存储播放模式到sharedPreferences
        CacheUtils.putPlaymode(this, "playmode", playerMode);

        if( this.playerMode==MusicPlayerService.REPEAT_SINGLE){
            //单曲循环播放-不会触发播放完成的回调
            mediaPlayer.setLooping(true);
        }else{
            //不循环播放
            mediaPlayer.setLooping(false);
        }
    }

    /**
     * 得到播放模式
     *
     * @return
     */
    private int getPlayMode() {
        return playerMode;
    }

    /**
     * 是否在播放音频
     *
     * @return
     */
    private boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }


}
