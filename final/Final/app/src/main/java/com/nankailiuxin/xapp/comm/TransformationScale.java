package com.nankailiuxin.xapp.comm;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.request.target.ImageViewTarget;

public class TransformationScale extends ImageViewTarget<Bitmap> {

    private ImageView target;

    public TransformationScale(ImageView target) {
        super(target);
        this.target = target;
    }

    @Override
    protected void setResource(Bitmap resource) {
        view.setImageBitmap(resource);

        if (resource != null) {
            int width = resource.getWidth();
            int height = resource.getHeight();

            int imageViewWidth = target.getWidth();

            float sy = (float) (imageViewWidth * 0.1) / (float) (width * 0.1);

            int imageHeight = (int) (height * sy);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, imageHeight);
            params.bottomMargin = 10;
            target.setLayoutParams(params);
        }
    }
}
