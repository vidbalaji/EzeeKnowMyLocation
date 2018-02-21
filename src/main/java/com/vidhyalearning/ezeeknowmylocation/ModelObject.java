package com.vidhyalearning.ezeeknowmylocation;

/**
 * Created by user on 09-Jan-18.
 */

public enum ModelObject {

    RED(R.string.red, R.layout.view_weather),
    BLUE(R.string.blue, R.layout.view_placedetails);

    private int mTitleResId;
    private int mLayoutResId;

    ModelObject(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

}