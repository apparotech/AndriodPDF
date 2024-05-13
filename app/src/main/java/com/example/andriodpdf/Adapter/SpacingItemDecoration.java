package com.example.andriodpdf.Adapter;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int spacingPx;
    private boolean includeEdge;

    public SpacingItemDecoration(int spanCount, int spacingPx, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacingPx = spacingPx;
        this.includeEdge = includeEdge;
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount;

        if (includeEdge){
            outRect.left =spacingPx - column * spacingPx / spanCount;
            outRect.right = (column + 1) * spacingPx / spanCount;

            if (position < spanCount) {
                outRect.top = spacingPx;
            }
            outRect.bottom = spacingPx;
        } else {
            outRect.left = column * spacingPx / spanCount;
            outRect.right = spacingPx - (column + 1) * spacingPx / spanCount;
            if (position >= spanCount) {
                outRect.top = spacingPx;
            }
        }
    }
}
