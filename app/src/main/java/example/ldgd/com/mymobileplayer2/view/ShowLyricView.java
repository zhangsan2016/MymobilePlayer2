package example.ldgd.com.mymobileplayer2.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import example.ldgd.com.mymobileplayer2.domain.Lyric;

/**
 * Created by ldgd on 2018/7/23.
 * 功能：显示歌词
 * 说明：实现了歌词同步
 */

public class ShowLyricView extends View {

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
                canvas.drawText(preContent, width / 2, tempY, bluePaint);
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
                canvas.drawText(preContent, width / 2, tempY, bluePaint);
            }


        } else {
            canvas.drawText("没有歌词可显示", width / 2, hight / 2, bluePaint);
        }


    }

    private void initData() {
        bluePaint = new Paint();
        bluePaint.setColor(Color.BLUE);
        bluePaint.setTextSize(textHeight);
        bluePaint.setAntiAlias(true);
        bluePaint.setTextAlign(Paint.Align.CENTER); // 字体设置从中间对齐

        whitePaint = new Paint();
        whitePaint.setColor(Color.BLUE);
        whitePaint.setTextSize(textHeight);
        whitePaint.setAntiAlias(true);

        lyrics = new ArrayList<>();
        // 测试歌词
        for (int i = 0; i < 1000; i++) {
            Lyric lyric = new Lyric();
            lyric.setTimepoint((1500 * i)); // 显示的时间
            lyric.setSleepTime((1000 * i));  // 高亮显示时间
            lyric.setContent("BBBBBBBBBBBBBBBBBBBBBBBBB");
            lyrics.add(lyric);
        }
    }

    /**
     * 根据当前播放的位置，找出该高亮显示哪一句歌词
     */
    public void setShowNextLyric(int position) {

        // 主线程刷新
        invalidate();
        // 非主线程刷新
        // postInvalidate();

    }


}
