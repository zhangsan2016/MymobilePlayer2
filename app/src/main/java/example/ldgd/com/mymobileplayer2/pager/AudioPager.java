package example.ldgd.com.mymobileplayer2.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import example.ldgd.com.mymobileplayer2.base.BasePager;

/**
 * Created by ldgd on 2018/4/13.
 */

public class AudioPager extends BasePager {


    public AudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {

        TextView tv = new TextView(mContext);
        tv.setTextColor(Color.RED);
        tv.setGravity(Gravity.CENTER);
        tv.setText("AudioPager");

        return tv;
    }


}
