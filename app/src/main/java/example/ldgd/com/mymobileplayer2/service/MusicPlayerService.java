package example.ldgd.com.mymobileplayer2.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import example.ldgd.com.mymobileplayer2.IMusicPlayerService;
import example.ldgd.com.mymobileplayer2.util.LogUtil;

/**
 * Created by ldgd on 2018/7/10.
 * 功能： 音乐播放服务
 * 说明：
 */

public class MusicPlayerService extends Service {


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
    };


    /**
     * 根据位置打开对应的音频文件，并播放
     */
    private void openAudio(int position) {
        LogUtil.e("service 中openAudio 调用  == 根据位置打开对应的音频文件，并播放");
    }

    /**
     * 播放音乐
     */
    private void start() {

    }

    /**
     * 暂停音乐
     */
    private void pause() {

    }

    /**
     * 停止音乐
     */
    private void stop() {

    }

    /**
     * 得到当前的播放进度
     *
     * @return
     */
    private int getCurrentPosition() {

        return 0;
    }

    /**
     * 得到当前播放的总时长
     *
     * @return
     */
    private int getDuration() {

        return 0;
    }

    /**
     * 得到艺术家
     *
     * @return
     */
    private String getArtist() {

        return null;
    }

    private String getName() {

        return null;
    }

    /**
     * 得到歌曲播放的路径
     *
     * @return
     */
    private String getAudioPath() {

        return null;
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

        return false;
    }


}
