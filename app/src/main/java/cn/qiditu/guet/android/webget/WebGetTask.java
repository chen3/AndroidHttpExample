package cn.qiditu.guet.android.webget;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.qiditu.signalslot.signals.Signal1;

class WebGetTask extends AsyncTask<URL, Void, WebGetTask.Result> {

    final Signal1<Result> taskFinished = new Signal1<>(this);

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    @WorkerThread
    protected Result doInBackground(URL... params) {
        Result result = new Result();
        if(params.length != 1) {
            result.isError = true;
            result.error = Application.getGlobalApplicationContext()
                                        .getString(R.string.params_length_not_is_one);
            return result;
        }
        HttpURLConnection urlConnection = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            urlConnection = (HttpURLConnection)params[0].openConnection();
            bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());
            int count;
            byte[] bytes = new byte[10 * 1024];
            StringBuilder stringBuilder = new StringBuilder();
            while ((count = bufferedInputStream.read(bytes)) != -1) {
                stringBuilder.append(new String(bytes, 0, count));
            }
            if(stringBuilder.length() == 0) {
                result.isError = true;
                result.error = Application.getGlobalApplicationContext()
                        .getString(R.string.receive_content_is_empty);
            }
            else {
                result.result = stringBuilder.toString();
//                final int partLength = 1024;
//                int startPoint = 0;
//                int endPoint;
//                int remainderLength = stringBuilder.length();
//                while (remainderLength > 0) {
//                    if(remainderLength < partLength) {
//                        endPoint = startPoint + remainderLength;
//                        remainderLength = 0;
//                    }
//                    else {
//                        endPoint = startPoint + partLength;
//                        remainderLength -= partLength;
//                    }
//                    result.result.offer(stringBuilder.substring(startPoint, endPoint));
//                    startPoint = endPoint;
//                }
            }
        } catch (IOException e) {
            result.isError = true;
            result.error = e.getMessage();
        } finally {
            if(bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    Log.e("Task", e.getMessage());
                }
            }
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

    @Override
    public void onPostExecute(Result result) {
        taskFinished.emit(result);
    }

    class Result {
        boolean isError;
        String error;
//        Queue<String> result = new LinkedList<>();
        String result;
    }
}
