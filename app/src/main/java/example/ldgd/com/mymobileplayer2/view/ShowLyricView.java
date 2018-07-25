package example.ldgd.com.mymobileplayer2.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.util.List;

import example.ldgd.com.mymobileplayer2.domain.Lyric;

/**
 * Created by ldgd on 2018/7/23.
 * 功能：显示歌词
 * 说明：实现了歌词同步
 */

public class ShowLyricView extends android.support.v7.widget.AppCompatTextView {

    /**
     * 画笔
     */
    private Paint bluePaint, whitePaint;
    /**
     * 屏幕宽高
     */
    private float width, hight;

    /**
     * 歌词列表
     */
    private List<Lyric> lyrics;
    /**
     * 当前歌词位置
     */
    private int index;
    /**
     * 歌词字体的高度
     */
    private float textHeight = 20;
    /**
     * 当前播放的进度
     */
    private int currentPosition;
    /**
     * 时间戳，什么时刻到高亮哪句歌词
     */
    private long timepoint;
    /**
     * 高亮显示的时间或者休眠时间
     */
    private long sleepTime;


    public ShowLyricView(Context context) {
        this(context, null);

    }

    public ShowLyricView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowLyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        hight = h;
    }

    /**
     *  设置歌词列表
     * @param lyrics
     */
    public void setLyrics(List<Lyric> lyrics) {
        this.lyrics = lyrics;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (lyrics != null && lyrics.size() > 0) {
            // 绘制歌词中间位置，蓝色画笔
            String currentText = lyrics.get(index).getContent();
            canvas.drawText(currentText, width / 2, hight / 2, bluePaint);

            // 绘制歌曲上部分，使用白色画笔
            float tempY = hight / 2;//Y轴的中间坐标
            for (int i = index - 1; i >= 0; i--) {
                //每一句歌词
                String preContent = lyrics.get(i).getContent();
                tempY = tempY - textHeight;
                if (tempY < 0) {
                    break;
                }
                canvas.drawText(preContent, width / 2, tempY, whitePaint);
            }


            // 绘制歌曲下部分，使用白色画笔
            tempY = hight / 2; //Y轴的中间坐标
            for (int i = index + 1; i < lyrics.size(); i++) {
                //每一句歌词
                String preContent = lyrics.get(i).getContent();
                tempY = tempY + textHeight;
                if (tempY > hight) {
                    break;
                }
                canvas.drawText(preContent, width / 2, tempY, whitePaint);
            }


        } else {
            canvas.drawText("没有歌词可显示", width / 2, hight / 2, bluePaint);
        }


    }

    private void initData() {
        bluePaint = new Paint();
        bluePaint.setColor(Color.GREEN);
        bluePaint.setTextSize(textHeight);
        bluePaint.setAntiAlias(true);
        bluePaint.setTextAlign(Paint.Align.CENTER); // 字体设置从中间对齐

        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setTextSize(textHeight);
        whitePaint.setTextAlign(Paint.Align.CENTER); // 字体设置从中间对齐
        whitePaint.setAntiAlias(true);


    /*    lyrics = new ArrayList<>();
        // 测试歌词
        for (int i = 0; i < 1000; i++) {
            Lyric lyric = new Lyric();
            lyric.setTimePoint((1500 * i)); // 显示的时间
            lyric.setSleepTime((1000 * i));  // 高亮显示时间
            lyric.setContent("BBBBBBBBBBBBBBBBBBBBBBBBB");
            lyrics.add(lyric);
        }*/
    }

    /**
     * 根据当前播放的位置，找出该高亮显示哪一句歌词
     *
     * @param currentPosition
     */
    public void setShowNextLyric(int currentPosition) {

        this.currentPosition = currentPosition;

        if (lyrics == null && lyrics.size() == 0) {
            return;
        }

        for (int i = 1; i < lyrics.size(); i++) {
            if (lyrics.get(i).getTimePoint() > currentPosition) {
                int tempIndex = i - 1;
                if (lyrics.get(tempIndex).getTimePoint() <= currentPosition) {
                    index = tempIndex;
                    timepoint = lyrics.get(index).getTimePoint();
                    sleepTime = lyrics.get(index).getSleepTime();
                }

            }

        }

        // 主线程刷新
        invalidate();
        // 非主线程刷新
        // postInvalidate();

    }


}
