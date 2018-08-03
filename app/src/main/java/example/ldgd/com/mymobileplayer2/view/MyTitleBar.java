package example.ldgd.com.mymobileplayer2.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import example.ldgd.com.mymobileplayer2.R;
import example.ldgd.com.mymobileplayer2.activity.SearchActivity;

/**
 * Created by ldgd on 2018/4/16.
 * 自定义标题栏
 */

public class MyTitleBar extends LinearLayout implements View.OnClickListener {
    private Context context;

    public MyTitleBar(Context context) {
        this(context, null);
    }

    public MyTitleBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

    }


    /**
     * 当布局文件加载完成的时候回调这个方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // 得到当前孩子实例
        TextView search = (TextView) this.getChildAt(1);
        RelativeLayout game = (RelativeLayout) this.getChildAt(2);
        ImageView record = (ImageView) this.getChildAt(3);

        // 设置点击事件
        search.setOnClickListener(this);
        game.setOnClickListener(this);
        record.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_search:  // 搜索
                Intent intent = new Intent(context, SearchActivity.class);
                context.startActivity(intent);
                break;
            case R.id.rl_game:    // 游戏
                Log.e("xx", "游戏");
                break;
            case R.id.iv_record:  // 记录
                Log.e("xx", "记录");
                break;
        }


    }
}
