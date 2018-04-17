package example.ldgd.com.mymobileplayer2.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import example.ldgd.com.mymobileplayer2.R;
import example.ldgd.com.mymobileplayer2.domain.MediaItem;

/**
 * Created by ldgd on 2018/4/17.
 */

public class VideoPagerAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<MediaItem> mediaItems;

    public VideoPagerAdapter(Context context, ArrayList<MediaItem> mediaItems) {
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
            convertView = View.inflate(context, R.layout.item_video_pager, null);
            viewHoder.tv_video_name = convertView.findViewById(R.id.tv_video_name);
            viewHoder.tv_size = convertView.findViewById(R.id.tv_size);
            viewHoder.tv_time = convertView.findViewById(R.id.tv_time);
            convertView.setTag(viewHoder);

        } else {
            viewHoder = (ViewHoder) convertView.getTag();
        }

        MediaItem mediaItem = mediaItems.get(position);

        viewHoder.tv_video_name.setText(mediaItem.getName());
        viewHoder.tv_size.setText(mediaItem.getDuration() + "");
        viewHoder.tv_time.setText(mediaItem.getSize() + "");

        return convertView;
    }

    private static class ViewHoder {
        private TextView tv_video_name;
        private TextView tv_time;
        private TextView tv_size;

    }
}
