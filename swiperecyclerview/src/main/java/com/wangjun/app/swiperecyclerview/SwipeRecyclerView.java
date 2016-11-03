package com.wangjun.app.swiperecyclerview;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * 当前类注释：可以下拉刷新和加载更多的recyclerview
 * Author :LeonWang
 * Created  2016/11/3.12:34
 * Description:
 * E-mail:lijiawangjun@gmail.com
 */

public class SwipeRecyclerView extends LinearLayout implements SwipeRefreshLayout.OnRefreshListener {

    private Context mContext;
    private ViewGroup mRefreshLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private SwipeRecyclerViewAdapter mSwipeRecyclerViewAdapter;
    private View mHeaderView;
    private View mLoadView;
    private View mViewHolder;
    private RefreshListener mRefreshListener;
    private int loadMorePosition;
    private boolean isHeader;
    private boolean isLoadingMore = false;
    private boolean isLoadEnable = true;


    public SwipeRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public SwipeRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SwipeRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setOrientation(LinearLayout.VERTICAL);
        isHeader = false;
        View view = LayoutInflater.from(context).inflate(R.layout.swipe_recyclerview_layout, null, false);
        mRefreshLayout = (ViewGroup) view.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_dark);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        setLayoutManager(new LinearLayoutManager(context));
        mLoadView = LayoutInflater.from(context).inflate(R.layout.refresh_loadmore_layout, this, false);
        mViewHolder = LayoutInflater.from(context).inflate(R.layout.network_hime_fail, null, false);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (null != mRefreshListener && isLoadEnable && isLoadingMore && dy > 0) {
                    int lastVisiblePosition = getLastPosition();
                    if (lastVisiblePosition + 1 == mSwipeRecyclerViewAdapter.getItemCount()) {
                        setLoadingMore(true);
                        mRefreshListener.onLoadMore();
                    }
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(this);
        addView(view);
    }


    /*
    * 設置占位圖
    * */
    public void setViewHolder(View view) {
        if (view.getParent() != null) {
            throw new IllegalArgumentException("view shouldn't has a parent view");
        }
        if (mRefreshLayout.indexOfChild(mViewHolder) != -1) {
            mRefreshLayout.removeView(mViewHolder);
            mViewHolder = view;
            mRefreshLayout.addView(mViewHolder);
        } else {
            mViewHolder = view;
        }

    }

    public void setOnRefresh(boolean enable) {
        mSwipeRefreshLayout.setEnabled(enable);
    }

    public void setLoadingMore(boolean loadingMore) {
        isLoadingMore = loadingMore;
    }

    @Override
    public void onRefresh() {
        if (null != mRefreshListener) {
            mRefreshListener.onRefresh();
        }
    }


    public void notifyLoadMoreFinish(boolean hasMore) {
        setLoadEnable(hasMore);
        notifyDataSetChanged();
        mSwipeRecyclerViewAdapter.notifyItemRemoved(loadMorePosition);
        isLoadingMore = false;
    }

    public void notifyRefreshFinish() {
        notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void setLoadEnable(boolean loadEnable) {
        isLoadEnable = loadEnable;
    }

    /*
    * view创建自动进行刷新
    * */
    public void startViewCreateRefresh() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });
    }

    /**
     * 填充adapter
     *
     * @param adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (mSwipeRecyclerViewAdapter == null) {
            mSwipeRecyclerViewAdapter = new SwipeRecyclerViewAdapter(adapter);
            mRecyclerView.setAdapter(mSwipeRecyclerViewAdapter);
        } else mSwipeRecyclerViewAdapter.setAdapter(adapter);
    }


    public int getLastPosition() {
        int lastPosition = 0;
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            lastPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof GridLayoutManager) {
            lastPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager =
                    (StaggeredGridLayoutManager) layoutManager;
            int[] lastPoasitions = staggeredGridLayoutManager.findLastVisibleItemPositions(
                    new int[staggeredGridLayoutManager.getSpanCount()]
            );
            lastPosition = getMaxPosition(lastPoasitions);
        } else {
            lastPosition = layoutManager.getItemCount() - 1;
        }
        loadMorePosition = lastPosition;
        return lastPosition;
    }

    private int getMaxPosition(int[] positions) {
        int size = positions.length;
        int maxPosition = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mRecyclerView.setLayoutManager(layoutManager);
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    switch (mSwipeRecyclerViewAdapter.getItemViewType(position)) {
                        case SwipeRecyclerViewAdapter.HEADER:
                            return gridLayoutManager.getSpanCount();
                        case SwipeRecyclerViewAdapter.LOADMORE:
                            return gridLayoutManager.getSpanCount();
                        default:
                            return 1;
                    }
                }
            });
        }
    }

    public void notifyDataSetChanged() {
        mSwipeRecyclerViewAdapter.notifyDataSetChanged();
        toggleListOrHolder();
    }

    public void toggleListOrHolder() {
        if (mSwipeRecyclerViewAdapter.getAdapter().getItemCount() > 0) {
            if (mRefreshLayout.indexOfChild(mViewHolder) != -1) {
                ViewGroup.LayoutParams layoutParams =
                        mRecyclerView.getLayoutParams();
                mRefreshLayout.removeView(mViewHolder);
                mRefreshLayout.addView(mRecyclerView, layoutParams);
            }
        } else {
            if (mRefreshLayout.indexOfChild(mRecyclerView) != -1) {
                ViewGroup.LayoutParams layoutParams =
                        mViewHolder.getLayoutParams();
                if (layoutParams == null) {
                    layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    mViewHolder.setLayoutParams(layoutParams);
                }
                mRefreshLayout.removeView(mRecyclerView);
                mRefreshLayout.addView(mViewHolder);
            }
        }
    }

    public void setHeaderView(View headerView) {
        this.mHeaderView = headerView;
        setHeaderEnable(true);
    }

    public void setHeaderView(int resId) {
        mHeaderView = LayoutInflater.from(mContext).inflate(resId, null);
        setHeaderEnable(true);
    }

    public void setHeaderEnable(boolean headerEnable) {
        this.isHeader = headerEnable;
    }

    public class SwipeRecyclerViewAdapter extends RecyclerView.Adapter {

        public static final int HEADER = 0x0001;
        public static final int LOADMORE = 0x0002;
        public static final int NORMAL = 0x0003;

        private RecyclerView.Adapter mAdapter;

        public SwipeRecyclerViewAdapter(RecyclerView.Adapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == HEADER) {
                return new HeaderViewHolder(mHeaderView);
            }
            if (viewType == LOADMORE) return new LoadMoreViewHolder(mLoadView);
            return mAdapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof HeaderViewHolder || holder instanceof LoadMoreViewHolder) {
                return;
            }
            if (isHeader && mHeaderView != null) {
                mAdapter.onBindViewHolder(holder, position - 1);
            } else {
                mAdapter.onBindViewHolder(holder, position);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 && isHeader && mHeaderView != null) {
                return HEADER;
            }
            if (position == getItemCount() - 1 && isLoadEnable) {
                return LOADMORE;
            }
            if (isHeader && mHeaderView != null) {
                return mAdapter.getItemViewType(position - 1);
            } else {
                return mAdapter.getItemViewType(position);
            }
        }

        @Override
        public int getItemCount() {
            int count = mAdapter.getItemCount();
            if (isHeader && count != 0) count++;
            if (isLoadEnable && count != 0) count++;
            return count;
        }

        public void setAdapter(RecyclerView.Adapter adapter) {
            this.mAdapter = adapter;
        }

        public RecyclerView.Adapter getAdapter() {
            return mAdapter;
        }

        public class LoadMoreViewHolder extends RecyclerView.ViewHolder {

            public LoadMoreViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class HeaderViewHolder extends RecyclerView.ViewHolder {
            public HeaderViewHolder(View itemView) {
                super(itemView);
            }
        }
    }


    public void setRefreshListener(RefreshListener refreshListener) {
        this.mRefreshListener = refreshListener;
    }

    public interface RefreshListener {
        void onRefresh();

        void onLoadMore();
    }

    public SwipeRecyclerViewAdapter getSwipeRecyclerViewAdapter() {
        return mSwipeRecyclerViewAdapter;
    }

    public void addDividerItemDecoration(RecyclerView.ItemDecoration decoration) {
        mRecyclerView.addItemDecoration(decoration);
    }


}
