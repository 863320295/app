package com.nankailiuxin.textx;

import android.widget.ImageView;

public class TextX {
    private static TextX instance;
    private IImageLoader imageLoader;

    public static TextX getInstance(){
        if (instance == null){
            synchronized (TextX.class){
                if (instance == null){
                    instance = new TextX();
                }
            }
        }
        return instance;
    }

    public void setImageLoader(IImageLoader imageLoader){
        this.imageLoader = imageLoader;
    }

    public void loadImage(String imagePath, ImageView imageView, int imageHeight){
        if (imageLoader != null){
            imageLoader.loadImage(imagePath, imageView, imageHeight);
        }
    }
}
