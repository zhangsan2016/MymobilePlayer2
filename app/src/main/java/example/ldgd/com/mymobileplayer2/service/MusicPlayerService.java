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

import java.io.IOException;
import java.util.ArrayList;

import example.ldgd.com.mymobileplayer2.IMusicPlayerService;
import example.ldgd.com.mymobileplayer2.R;
import example.ldgd.com.mymobileplayer2.activity.AudioPlayerActivity;
import example.ldgd.com.mymobileplayer2.domain.MediaItem;

/**
 * Created by ldgd on 2018/7/10.
 * 功能： 音乐播放服务
 * 说明：
 */

public class MusicPlayerService extends Service {
    public static final String OPENAUDIO = "com.ldgd.mobileplayer_OPENAUDIO";
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

            // 发送广播
            Intent intent = new Intent(OPENAUDIO);
            sendBroadcast(intent);


        }
    }

    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }
    }

    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {

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

        return mediaItems.get(position).getData();
    }


    /**
     * 播放下一个音频
     */
    private void next() {

    }

    /**
     * 播放上一个音频
     */
    private void pre() {

    }

    /**
     * 设置播放模式
     */
    private void setPlayMode(int playMode) {

    }

    /**
     * 得到播放模式
     *
     * @return
     */
    private int getPlayMode() {

        return 0;
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
