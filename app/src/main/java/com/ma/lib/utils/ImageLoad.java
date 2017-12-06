package com.ma.lib.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.ma.aigou.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;

/**
 * Created by mags on 2017/12/5.
 */

public class ImageLoad {

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
        L.writeLogs(false);
    }

    public static DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.mipmap.ic_default_broken_1)
            .showImageOnFail(R.mipmap.ic_default_broken_1)
            .showImageOnLoading(R.mipmap.ic_default_1)
            .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
            .build();

    public static void load(String url, ImageView view){
        ImageLoader.getInstance().displayImage(url, view, defaultOptions);
    }

}

