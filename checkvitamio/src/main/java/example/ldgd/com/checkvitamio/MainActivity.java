package example.ldgd.com.checkvitamio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import io.vov.vitamio.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVideoView = (VideoView) this.findViewById(R.id.videoView);

    }

    public void openView(View view) {
        Toast.makeText(this, "MainActivity#openView ", Toast.LENGTH_SHORT).show();
        String path = "http://192.168.24.1/赌侠马华.rmvb";


        if (!TextUtils.isEmpty(path)) {
            mVideoView.setVideoPath(path);
        }

      /*  mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // optional need Vitamio 4.0
                mediaPlayer.setPlaybackSpeed(1.0f);
            }
        });
*/

    }
}
