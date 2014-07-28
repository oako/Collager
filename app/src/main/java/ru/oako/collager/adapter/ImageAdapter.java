package ru.oako.collager.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ru.oako.collager.view.CheckableImageView;

/**
 * Photo picker adapter
 * Created by Alexei on 24.07.2014.
 */
public class ImageAdapter extends BaseAdapter {

    private final Context mContext;
    private int mItemHeight = 0;
    private int mNumColumns = 0;
    private GridView.LayoutParams mImageViewLayoutParams;

    private String[] mImageUrls;
    private SparseBooleanArray mCheckedItems;

    public ImageAdapter(Context context) {
        super();
        mContext = context;
        mImageViewLayoutParams = new GridView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    }

    @Override
    public int getCount() {
        // If columns have yet to be determined, return no items
        if (getNumColumns() == 0) {
            return 0;
        }

        // Size + number of columns for top empty row
        return mImageUrls.length;
    }

    public void add(String[] urls) {
        mImageUrls = urls;
        mCheckedItems = new SparseBooleanArray(mImageUrls.length);
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position < mNumColumns ? 0 : position - mNumColumns;
    }

    @Override
    public int getItemViewType(int position) {
        return (position < mNumColumns) ? 1 : 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        // Now handle the main ImageView thumbnails
        CheckableImageView imageView;
        if (convertView == null) { // if it's not recycled, instantiate and initialize
            imageView = new CheckableImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(mImageViewLayoutParams);
        } else { // Otherwise re-use the converted view
            imageView = (CheckableImageView) convertView;
        }

        // Check the height matches our calculated column width
        if (imageView.getLayoutParams().height != mItemHeight) {
            imageView.setLayoutParams(mImageViewLayoutParams);
        }

        imageView.setChecked(mCheckedItems.get(position, false));

        Picasso.with(mContext).load(mImageUrls[position]).into(imageView);
        return imageView;
    }

    /**
     * Sets the item height. Useful for when we know the column width so the height can be set
     * to match.
     */
    public void setItemHeight(int height) {
        if (height == mItemHeight) {
            return;
        }
        mItemHeight = height;
        mImageViewLayoutParams =
                new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
        notifyDataSetChanged();
    }

    public void setNumColumns(int numColumns) {
        mNumColumns = numColumns;
    }

    public int getNumColumns() {
        return mNumColumns;
    }

    public void setChecked(int position, boolean checked) {
        mCheckedItems.put(position, checked);
        notifyDataSetChanged();
    }

    public void toggle(int position) {
        setChecked(position, !isChecked(position));
    }

    public boolean isChecked(int position) {
        return mCheckedItems.get(position, false);
    }
}
