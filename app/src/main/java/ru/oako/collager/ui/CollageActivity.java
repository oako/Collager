package ru.oako.collager.ui;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import ru.oako.collager.R;
import ru.oako.collager.util.Collage;
import ru.oako.collager.util.Task;

/**
 * Activity that creates a collage and sends it via any app that has ACTION_SEND intent.
 * Created by Alexei on 26.07.2014.
 */
public class CollageActivity extends ActionBarActivity {
    public static final String EXTRA_COLLAGE = "collage";

    private ImageView mCollageView;
    private ProgressBar mProgressBar;
    private Button mShareButton;

    private ArrayList<String> mImageUrls = new ArrayList<String>();
    private ArrayList<Bitmap> mBitmaps = new ArrayList<Bitmap>();
    private Bitmap mCollage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_collage);

        mCollageView = (ImageView) findViewById(R.id.collage);
        mProgressBar = (ProgressBar) findViewById(R.id.collage_progress);
        mShareButton = (Button) findViewById(R.id.share_button);

        mImageUrls = getIntent().getStringArrayListExtra(EXTRA_COLLAGE);
        if (mImageUrls != null) {
            Task.create(new Task.TaskListener() {
                @Override
                public void beforeExecute() {
                    mProgressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onExecute() {
                    for (String imageUrl : mImageUrls) {
                        Bitmap bitmap = null;
                        try {
                            bitmap = Picasso.with(CollageActivity.this).load(imageUrl).get();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        mBitmaps.add(bitmap);
                    }
                    // Create bitmap for collage with calculated height depending on number of pics
                    mCollage = Bitmap.createBitmap(Collage.PIC_LARGE, Collage.calculateCanvasHeight(mBitmaps.size()), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(mCollage);
                    int remainingBitmaps = mBitmaps.size();
                    int startHeight = 0;
                    int startIndex = 0;
                    // Draw a set of images depending on number of remaining images
                    while (remainingBitmaps >= 3) {
                        Collage.drawThreeBitmaps(canvas, mBitmaps, startIndex, startHeight);
                        startIndex += 3;
                        remainingBitmaps -= 3;
                        startHeight += Collage.PIC_THIRD;
                    }
                    if (remainingBitmaps == 2)
                        Collage.drawTwoBitmaps(canvas, mBitmaps, startIndex, startHeight);

                    if (remainingBitmaps == 1)
                        Collage.drawOneBitmap(canvas, mBitmaps.get(startIndex), startHeight);
                }

                @Override
                public void afterExecute() {
                    if (mCollage != null)
                        mCollageView.setImageBitmap(mCollage);
                    mProgressBar.setVisibility(View.GONE);
                    mShareButton.setVisibility(View.VISIBLE);
                }
            });
        }

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCollage != null) {
                    String path = Environment.getExternalStorageDirectory().toString();
                    File file = new File(path, "collage.jpg");
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file);
                        mCollage.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        if (fos != null) {
                            try {
                                fos.close();
                                fos.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    Uri bmpUri = Uri.fromFile(file);
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    emailIntent.setType("image/jpeg");
                    startActivity(emailIntent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (Bitmap bm : mBitmaps)
            bm.recycle();
        mCollage.recycle();
    }
}
