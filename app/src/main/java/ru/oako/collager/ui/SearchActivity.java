package ru.oako.collager.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;

import ru.oako.collager.R;
import ru.oako.collager.util.Parser;
import ru.oako.collager.util.Task;

public class SearchActivity extends ActionBarActivity {
    // Client ID for accessing Instagram endpoints
    public static final String CLIENT_ID = "f82e32a0b6bc4134a60007b629131591";

    private EditText mSearchField;
    private ProgressBar mProgressBar;
    private Button mSearchButton;

    private String mUserId;
    private String mUserName;
    private String[] mImageUrls;
    private String[] mThumbUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mSearchField = (EditText) findViewById(R.id.search_edittext);
        mProgressBar = (ProgressBar) findViewById(R.id.search_progress);
        mSearchButton = (Button) findViewById(R.id.search_button);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserName = mSearchField.getText().toString().trim();
                if (mUserName.equals("")) {
                    Toast.makeText(SearchActivity.this, R.string.search_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                Task.create(new Task.TaskListener() {
                    @Override
                    public void beforeExecute() {
                        mSearchButton.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onExecute() {
                        Uri builtUri = Uri.parse("https://api.instagram.com/v1/users/search?").buildUpon()
                                .appendQueryParameter("q", mUserName)
                                .appendQueryParameter("client_id", CLIENT_ID)
                                .build();

                        try {
                            mUserId = Parser.connect(builtUri).getUserId();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (mUserId == null) return;

                        Uri mediaUri = Uri.parse("https://api.instagram.com/v1/users/").buildUpon()
                                .appendPath(mUserId)
                                .appendPath("media").appendPath("recent")
                                .appendQueryParameter("client_id", SearchActivity.CLIENT_ID)
                                .build();

                        Parser parser = null;
                        try {
                            parser = Parser.connect(mediaUri).getImages();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (parser != null) {
                            mImageUrls = parser.getImageUrls();
                            mThumbUrls = parser.getThumbUrls();
                        }
                    }

                    @Override
                    public void afterExecute() {
                        mSearchButton.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                        if (mUserId == null) {
                            Toast.makeText(SearchActivity.this, R.string.user_not_found, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (mImageUrls == null || mImageUrls.length == 0) {
                            Toast.makeText(SearchActivity.this, R.string.photos_not_found, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Intent intent = new Intent(SearchActivity.this, GalleryActivity.class);
                        intent.putExtra(GalleryActivity.EXTRA_IMAGES, mImageUrls);
                        intent.putExtra(GalleryActivity.EXTRA_THUMBS, mThumbUrls);
                        intent.putExtra(GalleryActivity.EXTRA_USERNAME, mUserName);
                        startActivity(intent);
                    }
                });
            }
        });
    }
}
