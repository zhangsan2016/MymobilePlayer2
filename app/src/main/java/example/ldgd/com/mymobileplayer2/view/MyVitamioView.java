package example.ldgd.com.mymobileplayer2.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Creted by ldgd on 2018/6/25.
 */

public class MyVitamioView extends io.vov.vitamio.widget.VideoView {

    public MyVitamioView(Context context) {
        super(context);
    }

    public MyVitamioView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyVitamioView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }


    public void setVideoSize(int videoWidth, int videoHeight) {
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = videoWidth;
        params.height = videoHeight;
        setLayoutParams(params);


    }
}
