package com.example.silviabova.mylogin;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by cupia on 18/04/2018.
 */

public class ImageAdapterGridView extends BaseAdapter {

    private Context mContext;
    private ArrayList<Bitmap> imagesIDs;

    public ImageAdapterGridView(Context c, ArrayList<Bitmap> image) {
        super();
        mContext=c;
        imagesIDs=image;
    }

    @Override
    public int getCount() {
        return imagesIDs.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ImageView mImageView;

        if(view== null){
            mImageView= new ImageView(mContext);
            mImageView.setLayoutParams(new GridView.LayoutParams(250,450));
            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImageView.setPadding(16,16,16,16);
        }else{
            mImageView= (ImageView) view;
        }
        mImageView.setImageBitmap(imagesIDs.get(i));
        return mImageView;
    }
}
