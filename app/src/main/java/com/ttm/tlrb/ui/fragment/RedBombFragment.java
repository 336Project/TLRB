package com.ttm.tlrb.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.ui.activity.AddRedBombActivity;
import com.ttm.tlrb.ui.adapter.BaseRecyclerAdapter;
import com.ttm.tlrb.ui.adapter.RedBombAdapter;
import com.ttm.tlrb.ui.application.Constant;
import com.ttm.tlrb.ui.entity.RedBomb;
import com.ttm.tlrb.view.EmptyEmbeddedContainer;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * Created by Helen on 2016/4/29.
 *
 */
public class RedBombFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,EmptyEmbeddedContainer.EmptyInterface,RedBombAdapter.MyItemClickListener {
    public static final int GO_ADD_RED_BOMB=1001;//去添加红包界面
    private List<RedBomb> mRedBombs = new ArrayList<>();
    private RedBombAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private EmptyEmbeddedContainer mEmptyContainer;
    private int page = 1;
    private boolean hasMore = true;
    private int type = 0;
    //private String mPageName;


    public static RedBombFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt("type",type);
        RedBombFragment fragment = new RedBombFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list_red_bomb,container,false);
        initView(view);
        page = 1;
        requestData();
        return view;
    }

    private void initView(View view){
        type = getArguments().getInt("type");
        /*if(type == 1){
            mPageName = getString(R.string.income);
        }else if(type == 2){
            mPageName = getString(R.string.action_out);
        }else {
            mPageName = getString(R.string.action_out);
        }*/
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new RedBombAdapter(mRedBombs);
        mAdapter.setEmptyInterface(this);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastVisibleItem = -1;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (hasMore && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mAdapter.getItemCount()) {
                    page += 1;
                    requestData();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //HLog.d("onScrolled",String.format("x:%1s,y:%2s",dx,dy));
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });

        mEmptyContainer = (EmptyEmbeddedContainer) view.findViewById(R.id.empty_container);
        mEmptyContainer.setEmptyInterface(this);
        mEmptyContainer.setType(EmptyEmbeddedContainer.EmptyStyle.EmptyStyle_LOADING_WITH_VIEW);

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        //转圈颜色
        mRefreshLayout.setColorSchemeResources(R.color.color_refresh_loading);
        //背景
        mRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.color_refresh_progress);
        mRefreshLayout.setOnRefreshListener(this);
//        IntentFilter counterActionFilter = new IntentFilter();
//        counterActionFilter.addAction(RedBombFragment.REFRESH_INFORM);
//        getActivity().registerReceiver(mReceiver, counterActionFilter);
    }


    private Subscriber<List<RedBomb>> subscriber;

    private void requestData() {
        if(mRefreshLayout.isRefreshing()){
            page = 1;
        }
        if(subscriber == null || subscriber.isUnsubscribed()){
            subscriber = new Subscriber<List<RedBomb>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    mRefreshLayout.setRefreshing(false);
                    if (mRedBombs.isEmpty()) {
                        mRefreshLayout.setEnabled(false);
                        mEmptyContainer.setType(EmptyEmbeddedContainer.EmptyStyle.EmptyStyle_RETRY);
                    }
                }

                @Override
                public void onNext(List<RedBomb> redBombs) {
                    mRefreshLayout.setEnabled(true);
                    mRefreshLayout.setRefreshing(false);
                    mEmptyContainer.setType(EmptyEmbeddedContainer.EmptyStyle.EmptyStyle_NORMAL);
                    if (page == 1) {
                        mRedBombs.clear();
                    }
                    int status = BaseRecyclerAdapter.STATUS_NORMAL;
                    if (redBombs != null && !redBombs.isEmpty()) {
                        status = redBombs.size() == Constant.PAGE_SIZE?BaseRecyclerAdapter.STATUS_LOADING : BaseRecyclerAdapter.STATUS_NORMAL;
                        mRedBombs.addAll(redBombs);
                    }
                    if (mRedBombs.isEmpty()) {
                        mEmptyContainer.setType(EmptyEmbeddedContainer.EmptyStyle.EmptyStyle_NODATA);
                    }
                    hasMore = BaseRecyclerAdapter.STATUS_LOADING == status;
                    mAdapter.notifyDataSetChanged(status);
                }
            };
        }
        APIManager.getInstance().getRedBombList(type, page, Constant.PAGE_SIZE, subscriber);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(subscriber != null && !subscriber.isUnsubscribed()){
            subscriber.unsubscribe();
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        requestData();
    }

    @Override
    public void doRetry() {
        mEmptyContainer.setType(EmptyEmbeddedContainer.EmptyStyle.EmptyStyle_LOADING);
        requestData();
    }

    @Override
    public void onItemClick(View view, int postion) {
        MobclickAgent.onEvent(getActivity(), Constant.Event.EVENT_ID_BOMB_LOOK);
        Intent intent=new Intent(getActivity(), AddRedBombActivity.class);
        intent.putExtra("redBomb", mRedBombs.get(postion));
        getActivity().startActivityForResult(intent, GO_ADD_RED_BOMB);
    }

    //添加红包数据后，进行更新数据
    public void addNewInform(RedBomb redBomb){
        mRedBombs.add(0,redBomb);
        mAdapter.notifyDataSetChanged();
    }

    /*private BroadcastReceiver mReceiver=new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(RedBombFragment.REFRESH_INFORM))
            {
                requestData();
            }
        }
    };*/


    /*@Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
    }*/
}
