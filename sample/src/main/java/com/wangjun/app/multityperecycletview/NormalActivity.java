package com.wangjun.app.multityperecycletview;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.Toast;

import com.wangjun.app.multityperecycletview.adapter.NormalAdapter;
import com.wangjun.app.swiperecyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NormalActivity extends AppCompatActivity implements SwipeRecyclerView.RefreshListener {

    private SwipeRecyclerView mSwipeRecyclerView;
    private Context mContext;
    private NormalAdapter mAdapter;
    private List<String> mData;
    private List<String> mLastData;
    private boolean isLoadMore = true;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    mData = new ArrayList<>();
                    for (int i = 0; i < 20; i++) {
                        mData.add("一大波鬼畜来袭" + i);
                    }
                    mAdapter.addData(mData);
                    mAdapter.notifyDataSetChanged();
                    mSwipeRecyclerView.notifyRefreshFinish();
                    break;
                case 200:
                    mLastData = new ArrayList<>();
                    for (int i = 0; i < 10; i++) {
                        mLastData.add("我是第二波鬼畜" + i);
                    }
                    if (isLoadMore) {
                        mAdapter.addLoadMore(mLastData);
                        mAdapter.notifyDataSetChanged();
                        isLoadMore = false;
                        mSwipeRecyclerView.notifyLoadMoreFinish(true);
                        Log.d("TAG","我被加载更多了");
                    } else {
                        Log.d("TAG","我没有数据了");
                        Toast.makeText(mContext, "没有更多数据", Toast.LENGTH_SHORT).show();
                        mSwipeRecyclerView.notifyLoadMoreFinish(false);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal);
        mContext = this;
        init();
    }

    private void init() {
        mSwipeRecyclerView = (SwipeRecyclerView) this.findViewById(R.id.refresh_view);
        mSwipeRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mSwipeRecyclerView.addDividerItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        mSwipeRecyclerView.setHeaderEnable(false);
        mSwipeRecyclerView.setRefreshListener(this);
        mSwipeRecyclerView.setLoadEnable(true);
        mSwipeRecyclerView.setLoadingMore(true);
        mAdapter = new NormalAdapter(mContext);
        mSwipeRecyclerView.setAdapter(mAdapter);
        mSwipeRecyclerView.startViewCreateRefresh();
    }

    @Override
    public void onRefresh() {
        mSwipeRecyclerView.setLoadEnable(true);
        mSwipeRecyclerView.setLoadingMore(true);
        isLoadMore = true;
        Message msg = Message.obtain();
        msg.what = 100;
        mHandler.sendMessageDelayed(msg, 2000);
    }

    @Override
    public void onLoadMore() {
        Message msg = Message.obtain();
        msg.what = 200;
        mHandler.sendMessageDelayed(msg, 2000);
    }
}
