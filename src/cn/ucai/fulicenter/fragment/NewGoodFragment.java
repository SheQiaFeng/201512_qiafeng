package cn.ucai.fulicenter.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.FuliCenterMain2Activity;
import cn.ucai.fulicenter.adapter.GoodAdapter;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by Administrator on 2016/6/15.
 */
public class NewGoodFragment extends Fragment {
    FuliCenterMain2Activity mContext;
    ArrayList<NewGoodBean> mGoodList;
    GoodAdapter mAdapter;
    private int pageId = 0;
    private int action = I.ACTION_DOWNLOAD;
    String path;

    //下拉刷新控件
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyrlerView;
    TextView mtvHint;
    GridLayoutManager mGridLayoutManager;

    // int pageSize = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = (FuliCenterMain2Activity) getActivity();
        View layout = View.inflate(mContext, R.layout.fragment_new_good, null);
        mGoodList = new ArrayList<NewGoodBean>();
        initView(layout);
        setListener();
        initData();
        mAdapter = new GoodAdapter(mContext, mGoodList,I.SORT_BY_ADDTIME_DESC);
        return layout;

    }

    private void setListener() {
        setPullDownRefreshListener();
        setPullUpRefreshListener();
    }

    //上拉加载
    private void setPullUpRefreshListener() {

        mRecyrlerView.setOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    int lastItemPosition;

                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                                lastItemPosition == mAdapter.getItemCount() - 1) {
                            if (mAdapter.isMore()) {
                                mSwipeRefreshLayout.setRefreshing(true);
                                action = I.ACTION_PULL_UP;
                                pageId += I.PAGE_SIZE_DEFAULT;
                                getPath(pageId);
                                mContext.executeRequest(new GsonRequest<NewGoodBean[]>(path,
                                        NewGoodBean[].class, responseDownloadNewGoodListener(),
                                        mContext.errorListener()));
                            }
                        }
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        //获取最好列表项的下脚标
                        lastItemPosition = mGridLayoutManager.findLastVisibleItemPosition();

                        //解决RecyclerView和SwipeRefreshLayout共用存在的bug
                        mSwipeRefreshLayout.setEnabled(mGridLayoutManager.findLastVisibleItemPosition() == 0);
                    }
                }
        );

    }

    /*
    *下拉刷新监听
    */
    private void setPullDownRefreshListener() {
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    public void onRefresh() {
                        mtvHint.setVisibility(View.VISIBLE);
                        pageId = 0;
                        action = I.ACTION_PULL_DOWN;
                        getPath(pageId);
                        mContext.executeRequest(new GsonRequest<NewGoodBean[]>(path,
                                NewGoodBean[].class, responseDownloadNewGoodListener(),
                                mContext.errorListener()));
                    }
                }
        );
    }

    private void initData() {
        try {
            getPath(pageId);
            mContext.executeRequest(new GsonRequest<NewGoodBean[]>(path,
                    NewGoodBean[].class, responseDownloadNewGoodListener(),
                    mContext.errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPath(int pageId) {
        try {
            path = new ApiParams()
                    .with(I.NewAndBoutiqueGood.CAT_ID, I.CAT_ID + "")
                    .with(I.PAGE_ID, pageId + "")
                    .with(I.PAGE_SIZE, I.PAGE_SIZE_DEFAULT + "")
                    .getRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS);
            Log.e("color", "++++:" + path);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Response.Listener<NewGoodBean[]> responseDownloadNewGoodListener() {
        return new Response.Listener<NewGoodBean[]>() {
            @Override
            public void onResponse(NewGoodBean[] newGoodBeen) {
                if (newGoodBeen != null) {
//                    ArrayList<NewGoodBean> list = Utils.array2List(newGoodBeen);
//                    mGoodList.addAll(list);
//                    mAdapter.initList(mGoodList);
                    mAdapter.setMore(true);
                    mSwipeRefreshLayout.setRefreshing(false);
                    mtvHint.setVisibility(View.GONE);
                    mAdapter.setFooterText(getResources().getString(R.string.load_more));
                    //将数组转换为集合
                    ArrayList<NewGoodBean> list = Utils.array2List(newGoodBeen);
                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mAdapter.initItems(list);
                    } else if (action == I.ACTION_PULL_UP) {
                        mAdapter.addItems(list);
                    }
                    if (newGoodBeen.length < I.PAGE_SIZE_DEFAULT) {
                        mAdapter.setMore(false);
                        mAdapter.setFooterText(getResources().getString(R.string.no_more));
                    }
                }

            }
        };
    }

    private void initView(View layout) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.sfl_newgood);
        mSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.google_blue),
                getResources().getColor(R.color.google_green),
                getResources().getColor(R.color.google_red),
                getResources().getColor(R.color.google_yellow)
        );
        mtvHint = (TextView) layout.findViewById(R.id.tv_refresh_hint);
        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyrlerView = (RecyclerView) layout.findViewById(R.id.rv_newgood);
        mRecyrlerView.setHasFixedSize(true);
        mRecyrlerView.setLayoutManager(mGridLayoutManager);
        mAdapter = new GoodAdapter(mContext, mGoodList,I.SORT_BY_ADDTIME_DESC);
        mRecyrlerView.setAdapter(mAdapter);
    }
}
