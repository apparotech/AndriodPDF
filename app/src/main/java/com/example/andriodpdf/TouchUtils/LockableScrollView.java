package com.example.andriodpdf.TouchUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class LockableScrollView  extends ListView {

    private boolean mScrollable = true;

    public LockableScrollView(Context context) {
        super(context);
    }
    public LockableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LockableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScrollingEnabled(boolean enabled) {
        mScrollable = enabled;
    }
    public boolean isScrollable() {
        return mScrollable;
    }

}
