package com.threeglasses.threebox.mylibrary;

import android.content.Context;
import android.net.Uri;

/**
 * Created by shuangwei on 18-12-11.
 */

public class CompareApi {

    public void init(Context context) {
        CompareUtil.getInstance().init(context);
    }

    public int getCompareVideo(Uri uri) {
        return CompareUtil.getInstance().compareVideo(uri);
    }

    public int getCompareImage(Uri uri) {
        return CompareUtil.getInstance().compareImage(uri);
    }
}
