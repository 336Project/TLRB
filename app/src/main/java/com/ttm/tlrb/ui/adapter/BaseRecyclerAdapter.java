package com.ttm.tlrb.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ttm.tlrb.R;

import java.util.List;

/**
 * Created by Helen on 2016/5/18.
 *
 */
public abstract class BaseRecyclerAdapter<D> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<D> mDataList;
    private static final int TYPE_CONTENT = 1;
    private static final int TYPE_FOOTER = 2;

    public final static int STATUS_NORMAL = 0;
    public final static int STATUS_LOADING = 1;
    public final static int STATUS_LOAD_FAIL = 2;
    private int status = STATUS_NORMAL;

    public BaseRecyclerAdapter(List<D> dataList){
        this.mDataList = dataList;
    }

    @Override
    final public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_FOOTER){
            return onCreateFooterViewHolder(parent);
        }else {
            return onCreateContentViewHolder(parent);
        }
    }

    protected abstract RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent);

    protected RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent){
        return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.xlistview_footer, parent, false));
    }

    @Override
    final public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == TYPE_FOOTER){
            onBindFooterViewHolder(holder);
        }else {
            onBindContentViewHolder(holder,position);
        }
    }

    protected void onBindFooterViewHolder(RecyclerView.ViewHolder holder){
        FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
        switch (status){
            case STATUS_LOADING:
                footerViewHolder.mProgressBar.setVisibility(View.VISIBLE);
                footerViewHolder.mTextMore.setText(R.string.loading);
                break;
            case STATUS_LOAD_FAIL:
                footerViewHolder.mProgressBar.setVisibility(View.GONE);
                footerViewHolder.mTextMore.setText(R.string.load_fail_retry);
                break;
            case STATUS_NORMAL:
            default:
                footerViewHolder.mProgressBar.setVisibility(View.VISIBLE);
                footerViewHolder.mTextMore.setText(R.string.loading);
                break;
        }
    }

    protected abstract void onBindContentViewHolder(RecyclerView.ViewHolder holder, int position);

    @Override
    final public int getItemCount() {
        if(mDataList != null && !mDataList.isEmpty()){
            return mDataList.size() +1;
        }
        return 0;
    }

    @Override
    final public int getItemViewType(int position) {
        if(position + 1 == getItemCount()){
            return TYPE_FOOTER;
        }else {
            return TYPE_CONTENT;
        }
    }

    public void notifyDataSetChanged(int status){
        this.status = status;
        super.notifyDataSetChanged();
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder{
        private TextView mTextMore;
        private ProgressBar mProgressBar;
        public FooterViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*if(emptyInterface != null){
                        emptyInterface.doRetry();
                    }*/
                    mProgressBar.setVisibility(View.VISIBLE);
                    mTextMore.setText(R.string.loading);
                }
            });
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            mTextMore = (TextView) itemView.findViewById(R.id.view_more);
        }
    }
}
