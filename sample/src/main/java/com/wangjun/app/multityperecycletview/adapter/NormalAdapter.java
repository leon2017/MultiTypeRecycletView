package com.wangjun.app.multityperecycletview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wangjun.app.multityperecycletview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 当前类注释：
 * Author :LeonWang
 * Created  2016/11/3.14:50
 * Description:
 * E-mail:lijiawangjun@gmail.com
 */

public class NormalAdapter extends RecyclerView.Adapter<NormalAdapter.NormalViewHolder> {


    private Context mContext;
    private List<String> mData = new ArrayList<>();

    public NormalAdapter(Context context) {
        this.mContext = context;


    }

    public void addData(List<String> data) {
        if (null != mData) {
            mData.clear();
        }
        this.mData.addAll(data);
    }

    public void addLoadMore(List<String> data) {
        int lastPosition = mData.size();
        this.mData.addAll(lastPosition,data);
    }

    @Override
    public NormalAdapter.NormalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutInflater = LayoutInflater.from(mContext).inflate(R.layout.normal_item_layout,parent,false);
        NormalViewHolder holder = new NormalViewHolder(layoutInflater);
        return holder;
    }

    @Override
    public void onBindViewHolder(NormalAdapter.NormalViewHolder holder, int position) {
        holder.tvNormal.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class NormalViewHolder extends RecyclerView.ViewHolder {

        TextView tvNormal;

        public NormalViewHolder(View itemView) {
            super(itemView);
            tvNormal = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }

}
