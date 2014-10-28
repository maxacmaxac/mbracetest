package mbrace.android.com.mbracelabs;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import java.util.ArrayList;

import mbrace.android.com.mbracelabs.model.FlickrImage;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private int defaultItemBackground;
    private ArrayList<FlickrImage> imageContener;

    public ArrayList<FlickrImage> getImageContener() {
        return imageContener;
    }

    public void setImageContener(ArrayList<FlickrImage> imageContener) {
        this.imageContener = imageContener;
    }

    public ImageAdapter(Context c, ArrayList<FlickrImage> imageContener) {
        mContext = c;
        this.imageContener = imageContener;
        TypedArray styleAttrs = c.obtainStyledAttributes(R.styleable.PicGallery);
        styleAttrs.getResourceId(R.styleable.PicGallery_android_galleryItemBackground, 0);
        defaultItemBackground = styleAttrs.getResourceId(
                R.styleable.PicGallery_android_galleryItemBackground, 0);
        styleAttrs.recycle();
    }

    public int getCount() {
        return imageContener.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView i = new ImageView(mContext);
        if (imageContener.get(position).getThumb() != null) {
            i.setImageBitmap(imageContener.get(position).getThumb());
            i.setLayoutParams(new Gallery.LayoutParams(75, 75));
            i.setBackgroundResource(defaultItemBackground);
        } else
            i.setImageDrawable(mContext.getResources().getDrawable(android.R.color.black));
        return i;
    }

}
