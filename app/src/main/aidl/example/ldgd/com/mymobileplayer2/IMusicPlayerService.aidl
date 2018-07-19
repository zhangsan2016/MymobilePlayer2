// IMusicPlayerService.aidl
package example.ldgd.com.mymobileplayer2;

// Declare any non-default types here with import statements

interface IMusicPlayerService {

  /**
     * 根据位置打开对应的音频文件，并播放
     */
     void openAudio(int position);

    /**
     * 播放音乐
     */
     void start();

    /**
     * 暂停音乐
     */
     void pause();

    /**
     * 停止音乐
     */
     void stop();
    /**
     * 得到当前的播放进度
     *
     * @return
     */
     int getCurrentPosition();

    /**
     * 得到当前播放的总时长
     *
     * @return
     */
     int getDuration();
    /**
     * 得到艺术家
     *
     * @return
     */
     String getArtist();

     String getName();

    /**
     * 得到歌曲播放的路径
     *
     * @return
     */
     String getAudioPath();


    /**
     * 播放下一个音频
     */
     void next();

    /**
     * 播放上一个音频
     */
     void pre();

    /**
     * 设置播放模式
     */
     void setPlayMode(int playMode);

    /**
     * 得到播放模式
     *
     * @return
     */
     int getPlayMode();

    /**
     * 是否在播放音频
     *
     * @return
     */
     boolean isPlaying();

     void seeto(int progress);





}
