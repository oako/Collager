package ru.oako.collager.util;

import android.os.AsyncTask;
import java.util.EventListener;

/**
 * Created by Alexei on 24.07.2014.
 */
public class Task extends AsyncTask<Void, Void, Void> {
    private TaskListener mTaskListener;



    public Task(TaskListener listener) {
        mTaskListener = listener;
    }


    private Task() {
    }

    @Override
    protected void onPreExecute() {
        if (mTaskListener != null) {
            mTaskListener.beforeExecute();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (mTaskListener != null) {
            mTaskListener.onExecute();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (mTaskListener != null)
            mTaskListener.afterExecute();

    }

    public static void create(TaskListener listener) {
        new Task(listener).execute();
    }

    public static interface TaskListener extends EventListener {
        void beforeExecute();
        void onExecute();
        void afterExecute();
    }

}
