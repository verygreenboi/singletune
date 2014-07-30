package com.pixel.singletune.app.Widgets;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created by mrsmith on 7/30/14.
 */
public class SingleTuneImageView extends ImageView {

    private ResponseObserver mObserver;
    // Image url
    private String mUrl;
    private int mDefaultImageId;
    private int mErrorImageId;

    // ID of default img
    private ImageLoader mImageLoader;

    // Network fail image id
    private ImageLoader.ImageContainer mImageContainer;

    public SingleTuneImageView(Context context) {
        this(context, null);
    }

    public SingleTuneImageView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }



    public SingleTuneImageView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    public void  setResponseObserver(ResponseObserver observer){
        mObserver = observer;
    }

    public void setImageUrl(String url, ImageLoader imageLoader){
        mUrl = url;
        mImageLoader = imageLoader;
        loadImageIfNecessary(false);
    }

    public void setDefaultImageResId(int defaultImage){
        mDefaultImageId = defaultImage;
    }

    public void setErrorImageResId(int errorImage){
        mErrorImageId = errorImage;
    }

    private void loadImageIfNecessary(final boolean isInLayoutPass){
        final int width = getWidth();
        int height = getHeight();

        boolean isFullyWrapContent = getLayoutParams() != null
                && getLayoutParams().height == LayoutParams.WRAP_CONTENT
                && getLayoutParams().width == LayoutParams.WRAP_CONTENT;

        if (width == 0 && height == 0 && !isFullyWrapContent){
            return;
        }

        if (TextUtils.isEmpty(mUrl)){
            if (mImageContainer != null){
                mImageContainer.cancelRequest();
                mImageContainer = null;
            }

            setDefaultImageOrNull();
            return;

        }

        if (mImageContainer != null && mImageContainer.getRequestUrl() != null){
            if (mImageContainer.getRequestUrl().equals(mUrl)){
                return;
            }
            else {
                mImageContainer.cancelRequest();
                setDefaultImageOrNull();
            }
        }
        ImageLoader.ImageContainer newContainer = mImageLoader.get(mUrl, new ImageLoader.ImageListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (mErrorImageId != 0){
                    setImageResource(mErrorImageId);
                }

                if (mObserver != null){
                    mObserver.onError();
                }

            }

            @Override
            public void onResponse(final ImageLoader.ImageContainer response, boolean isImmediate) {
                if (isImmediate && isInLayoutPass){
                    post(new Runnable() {
                        @Override
                        public void run() {
                            onResponse(response, false);
                        }
                    });
                    return;
                }

                int bWidth = 0, bHeight = 0;
                if (response.getBitmap() != null){
                    setImageBitmap(response.getBitmap());
                    bWidth = response.getBitmap().getWidth();
                    bHeight = response.getBitmap().getHeight();
                    adjustImageAspect(bWidth, bHeight);
                }
                else if (mDefaultImageId != 0){
                    setImageResource(mDefaultImageId);
                }

                if (mObserver != null){
                    mObserver.onSuccess();
                }
            }
        });

        mImageContainer = newContainer;

    }

    private void setDefaultImageOrNull(){
        if (mDefaultImageId != 0){
            setImageResource(mDefaultImageId);
        }
        else {
            setImageBitmap(null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom){
        super.onLayout(changed, left, top, right, bottom);
        loadImageIfNecessary(true);
    }

    @Override
    protected void onDetachedFromWindow(){
        if (mImageContainer != null){
            mImageContainer.cancelRequest();
            setImageBitmap(null);
            mImageContainer = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void drawableStateChanged(){
        super.drawableStateChanged();
        invalidate();
    }

    private void adjustImageAspect(int bWidth, int bHeight){
        RelativeLayout.LayoutParams params = (LayoutParams) getLayoutParams();

        if (bWidth == 0 || bHeight == 0)
            return;

        int swidth = getWidth();
        int new_height = 0;
        new_height = swidth * bHeight / bWidth;
        params.width = swidth;
        setLayoutParams(params);
    }

    public interface  ResponseObserver{
        public  void onError();

        public void onSuccess();
    }

}
