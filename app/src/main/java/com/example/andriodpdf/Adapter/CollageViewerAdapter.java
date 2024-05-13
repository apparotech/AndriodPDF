package com.example.andriodpdf.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.andriodpdf.PDFCreater;

public class CollageViewerAdapter extends BaseAdapter {


    PDFCreater mainActivity;

    public  CollageViewerAdapter(PDFCreater creater) {
        mainActivity = creater;
    }

   public int getCount(){
        return mainActivity.getDocument().getPageCount();
   }

    @Override
    public Object getItem(int position) {
        return mainActivity.getDocument().getDatasets().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        return mainActivity.getDocument().getView(position);
    }
}
