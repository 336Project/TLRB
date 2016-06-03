package com.ttm.tlrb.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ttm.tlrb.R;
import com.ttm.tlrb.ui.entity.RedBomb;

import java.util.List;

/**
 * Created by Helen on 2016/6/3.
 *
 */
public class RedBombAdapter extends BaseRecyclerAdapter<RedBomb>{

    public RedBombAdapter(List<RedBomb> dataList) {
        super(dataList);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {
        return new RedBombViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_red_bomb,parent,false));
    }

    @Override
    protected void onBindContentViewHolder(RecyclerView.ViewHolder holder, int position) {
        RedBombViewHolder viewHolder = (RedBombViewHolder) holder;
        RedBomb redBomb = getItem(position);
        viewHolder.mTextViewName.setText(redBomb.getName());
        viewHolder.mTextViewCreateTime.setText(redBomb.getCreatedAt());
        viewHolder.mTextViewGift.setText(redBomb.getGift());
        viewHolder.mTextViewTime.setText(redBomb.getTime());
        viewHolder.mTextViewGroupName.setText(redBomb.getCategoryName());
        int target = redBomb.getTarget();
        if(target == 1){
            viewHolder.mTextViewTarget.setText("男方");
        }else  if(target == 2){
            viewHolder.mTextViewTarget.setText("女方");
        }else if(target == 3){
            viewHolder.mTextViewTarget.setText("共同");
        }else {
            viewHolder.mTextViewTarget.setText("");
        }

        int type = redBomb.getType();
        String money;
        if(type == 1){
            money = "+";
        }else {
            money = "-";
        }
        money += redBomb.getMoney();
        viewHolder.mTextViewMoney.setText(money);

    }

    private class RedBombViewHolder extends RecyclerView.ViewHolder{
        TextView mTextViewName;
        TextView mTextViewGroupName;
        TextView mTextViewTarget;
        TextView mTextViewGift;
        TextView mTextViewMoney;
        TextView mTextViewTime;
        TextView mTextViewCreateTime;

        public RedBombViewHolder(View itemView) {
            super(itemView);
            mTextViewName = (TextView) itemView.findViewById(R.id.item_name);
            mTextViewGroupName = (TextView) itemView.findViewById(R.id.item_group_name);
            mTextViewTarget = (TextView) itemView.findViewById(R.id.item_target);
            mTextViewGift = (TextView) itemView.findViewById(R.id.item_gift);
            mTextViewMoney = (TextView) itemView.findViewById(R.id.item_money);
            mTextViewTime = (TextView) itemView.findViewById(R.id.item_time);
            mTextViewCreateTime = (TextView) itemView.findViewById(R.id.item_create_time);
        }
    }
}
