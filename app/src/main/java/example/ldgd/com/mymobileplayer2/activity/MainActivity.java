package example.ldgd.com.mymobileplayer2.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;

import example.ldgd.com.mymobileplayer2.R;
import example.ldgd.com.mymobileplayer2.base.BasePager;
import example.ldgd.com.mymobileplayer2.pager.AudioPager;
import example.ldgd.com.mymobileplayer2.pager.NetAudioPager;
import example.ldgd.com.mymobileplayer2.pager.NetVideoPager;
import example.ldgd.com.mymobileplayer2.pager.VideoPager;

public class MainActivity extends FragmentActivity {

    private FrameLayout flMainContent;
    private RadioButton rbVideo;
    private RadioButton rbAudio;
    private RadioGroup rgBottomTag;

    /**
     * 当前RadioGroup选中的位置
     */
    private static int position;

    /**
     * pager集合
     */
    private static ArrayList<BasePager> basePagers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flMainContent = (FrameLayout) findViewById(R.id.fl_main_content);
        rbVideo = (RadioButton) findViewById(R.id.rb_video);
        rbAudio = (RadioButton) findViewById(R.id.rb_audio);

        rgBottomTag = findViewById(R.id.rg_bottom_tag);

        basePagers = new ArrayList<>();
        basePagers.add(new VideoPager(this));//添加本地视频页面-0
        basePagers.add(new AudioPager(this));//添加本地音乐页面-1
        basePagers.add(new NetVideoPager(this));//添加网络视频页面-2
        basePagers.add(new NetAudioPager(this));//添加网络音频页面-3


        //设置RadioGroup的监听
        rgBottomTag.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkId) {

                switch (checkId) {
                    default:
                        position = 0;
                        break;
                    case R.id.rb_audio:
                        position = 1;
                        break;
                    case R.id.rb_net_video:
                        position = 2;
                        break;
                    case R.id.rb_net_audio:
                        position = 3;
                        break;
                }

                setFragment();


            }
        });


    }

    /**
     * 把界面显示到Pager中
     */
    private void setFragment() {

        // 得到FragmentManger
        FragmentManager fm = getSupportFragmentManager();
        // 开启事务
        FragmentTransaction ft = fm.beginTransaction();
        // 替换
        ft.replace(R.id.fl_main_content,new ReplaceFragment());
        // 提交事务
        ft.commit();
    }

    public static BasePager getBasePager() {

        BasePager basePager = basePagers.get(position);

        if (basePager != null) {
            basePager.initData();
        }
        return basePager;
    }

    public static class ReplaceFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            BasePager basePager = getBasePager();
            if (basePager != null) {
                //各个页面的视图
                return basePager.rootView;
            }
            return null;
        }
    }

}
