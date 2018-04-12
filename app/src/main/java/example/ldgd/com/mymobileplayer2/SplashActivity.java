package example.ldgd.com.mymobileplayer2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

public class SplashActivity extends Activity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                startMainActivity();
                Log.e(TAG, Thread.currentThread().getName());

            }

        }, 2000);

    }


    private boolean isStartMain = false;

    /**
     * 跳转到主页面，并且把当前页面关闭掉
     */
    private void startMainActivity() {

        if (!isStartMain) {

            isStartMain = true;
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            //关闭当前页面
            this.finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.e(TAG,"onTouchEvent==Action"+event.getAction());
        startMainActivity();

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        //把所有的消息和回调移除
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
