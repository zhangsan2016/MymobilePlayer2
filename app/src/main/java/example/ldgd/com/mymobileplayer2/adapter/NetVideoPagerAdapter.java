package example.ldgd.com.mymobileplayer2.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import example.ldgd.com.mymobileplayer2.R;
import example.ldgd.com.mymobileplayer2.domain.MediaItem;

/**
 * Created by ldgd on 2018/4/17.
 */

public class NetVideoPagerAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<MediaItem> mediaItems;

    public NetVideoPagerAdapter(Context context, ArrayList<MediaItem> mediaItems) {
        this.context = context;
        this.mediaItems = mediaItems;
    }

    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public MediaItem getItem(int location) {
        return mediaItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHoder viewHoder;

        if (convertView == null) {
            viewHoder = new ViewHoder();
            convertView = View.inflate(context, R.layout.item_netvideo_pager, null);
            viewHoder.tv_video_name = convertView.findViewById(R.id.tv_name);
            viewHoder.iv_ico = convertView.findViewById(R.id.iv_ico);
            viewHoder.tv_desc = convertView.findViewById(R.id.tv_desc);
            convertView.setTag(viewHoder);

        } else {
            viewHoder = (ViewHoder) convertView.getTag();
        }

        MediaItem mediaItem = mediaItems.get(position);

        viewHoder.tv_video_name.setText(mediaItem.getName());
        viewHoder.tv_desc.setText(mediaItem.getDesc());

        // 1.使用 Xutil3加载图片
    //    x.image().bind(viewHoder.iv_ico, mediaItem.getImageUrl());

        // 2.使用 Glide 加载图片
//        Glide.with(context).load(mediaItem.getImageUrl())
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(R.drawable.video_default)
//                .error(R.drawable.video_default)
//                .into(viewHoder.iv_ico);

        // 3.使用 Picasso 加载图片
        Picasso.with(context).load(mediaItem.getImageUrl())
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.video_default)
                .error(R.drawable.video_default)
                .fit()
                .centerCrop()
                .into(viewHoder.iv_ico);


        return convertView;
    }

    private static class ViewHoder {
        private TextView tv_video_name;
        private ImageView iv_ico;
        private TextView tv_desc;

    }
}
