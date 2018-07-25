package example.ldgd.com.mymobileplayer2.domain;

/**
 * Created by ldgd on 2018/7/23.
 * 功能：歌词实体类
 * 说明：
 */

public class Lyric {

    /**
     *  歌词内容
     */
    private String content;
    /**
     *  时间戳
     */
    private long timePoint;
    /**
     * 高亮显示时间
     */
    private long sleepTime;




    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(long timePoint) {
        this.timePoint = timePoint;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }
}