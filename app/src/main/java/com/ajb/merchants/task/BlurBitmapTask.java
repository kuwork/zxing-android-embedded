package com.ajb.merchants.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;

import com.ajb.merchants.util.FastBlur;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerry on 16/4/25.
 */
public class BlurBitmapTask extends AsyncTask<Bitmap, Integer, List<Bitmap>> {

    private Context context;

    public BlurBitmapTask(Context c) {
        this.context = c;
    }

    @Override
    protected List<Bitmap> doInBackground(Bitmap... params) {
        List<Bitmap> list = new ArrayList<>();
        try {
            for (Bitmap b : params) {
                if (isCancelled()) {
                    break;
                }
                list.add(blur(b, 15));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private Bitmap blur(Bitmap bkg, int radius) {
        long startMs = System.currentTimeMillis();
        Bitmap overlay = FastBlur.doBlur(bkg, (int) radius, false);
        System.out.println(System.currentTimeMillis() - startMs + "ms");
        return overlay;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onCancelled(List<Bitmap> list) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.onCancelled(list);
        }
    }

    @Override
    protected void onPostExecute(List<Bitmap> list) {
        super.onPostExecute(list);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
}
