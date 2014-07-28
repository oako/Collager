package ru.oako.collager.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import java.util.ArrayList;

import ru.oako.collager.BuildConfig;
import ru.oako.collager.R;
import ru.oako.collager.adapter.ImageAdapter;

public class GalleryActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    private static final String LOG_TAG = GalleryActivity.class.getSimpleName();
    public static final String EXTRA_USERNAME = "extra_username";
    public static final String EXTRA_IMAGES = "extra_images";
    public static final String EXTRA_THUMBS = "extra_thumbs";

    private int mImageThumbSize;
    private int mImageThumbSpacing;

    private GridView mGridView;
    private Button mCreateButton;
    private ImageAdapter mAdapter;

    private String[] mImageUrls;
    private ArrayList<String> mSelectedItems = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        setTitle(getIntent().getStringExtra(EXTRA_USERNAME));

        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        mAdapter = new ImageAdapter(this);

        mGridView = (GridView) findViewById(R.id.gallery_gridview);
        mCreateButton = (Button) findViewById(R.id.gallery_create_button);

        mImageUrls = getIntent().getStringArrayExtra(EXTRA_IMAGES);
        final String[] thumbUrls = getIntent().getStringArrayExtra(EXTRA_THUMBS);

        mAdapter.add(thumbUrls);

        mGridView.setAdapter(mAdapter);

        // This listener is used to get the final width of the GridView and then calculate the
        // number of columns and the width of each column. The width of each column is variable
        // as the GridView has stretchMode=columnWidth. The column width is used to set the height
        // of each view so we get nice square thumbnails.
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        if (mAdapter.getNumColumns() == 0) {
                            final int numColumns = (int) Math.floor(
                                    mGridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                            if (numColumns > 0) {
                                final int columnWidth =
                                        (mGridView.getWidth() / numColumns) - mImageThumbSpacing;
                                mAdapter.setNumColumns(numColumns);
                                mAdapter.setItemHeight(columnWidth);
                                if (Build.VERSION.SDK_INT >= 16) {
                                    mGridView.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                } else {
                                    mGridView.getViewTreeObserver()
                                            .removeGlobalOnLayoutListener(this);
                                }
                            }
                        }
                    }
                }
        );

        mGridView.setOnItemClickListener(this);

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent collageIntent = new Intent(GalleryActivity.this, CollageActivity.class);
                collageIntent.putStringArrayListExtra(CollageActivity.EXTRA_COLLAGE, mSelectedItems);

                startActivity(collageIntent);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        mAdapter.toggle(position);
        // Based on whether view is checked or not, add/removes item from list of checked views
        if (mAdapter.isChecked(position)) {
            mSelectedItems.add(mImageUrls[position]);
        } else {
            mSelectedItems.remove(mImageUrls[position]);
        }
        if (mSelectedItems.size() > 0) {
            mCreateButton.setVisibility(View.VISIBLE);
        } else {
            mCreateButton.setVisibility(View.GONE);
        }
    }
}
