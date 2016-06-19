package cn.ucai.fulicenter.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import cn.ucai.fulicenter.adapter.BoutiqueAdapter;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by Administrator on 2016/6/18.
 */
public class BoutiqueFragment extends Fragment {
    FuliCenterMain2Activity mContext;
    ArrayList<BoutiqueBean> mBoutiqueList;
    BoutiqueAdapter mAdapter;
    private int action = I.ACTION_DOWNLOAD;
    String path;
    //下拉刷新控件
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyrlerView;
    TextView mtvHint;
    LinearLayoutManager mLinearLayoutManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = (FuliCenterMain2Activity) getActivity();
        View layout = inflater.inflate( R.layout.fragment_boutique,container,false);
        mBoutiqueList = new ArrayList<BoutiqueBean>();
        initView(layout);
        setListener();
        initData();
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
                                getPath();
                                mContext.executeRequest(new GsonRequest<BoutiqueBean[]>(path,
                                        BoutiqueBean[].class, responseDownloadBoutiqueListener(),
                                        mContext.errorListener()));
                            }
                        }
                    }
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        //获取最好列表项的下脚标
                        lastItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
                        //解决RecyclerView和SwipeRefreshLayout共用存在的bug
                        mSwipeRefreshLayout.setEnabled(mLinearLayoutManager
                                .findFirstCompletelyVisibleItemPosition()== 0);
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
                        action = I.ACTION_PULL_DOWN;
                        getPath();
                        mContext.executeRequest(new GsonRequest<BoutiqueBean[]>(path,
                                BoutiqueBean[].class, responseDownloadBoutiqueListener(),
                                mContext.errorListener()));
                    }
                }
        );
    }

    private void initData() {
        try {
            getPath();
            mContext.executeRequest(new GsonRequest<BoutiqueBean[]>(path,
                    BoutiqueBean[].class, responseDownloadBoutiqueListener(),
                    mContext.errorListener()));
            Log.e("main", "path:" + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPath() {
        try {
            path = new ApiParams()
                    .getRequestUrl(I.REQUEST_FIND_BOUTIQUES);
            Log.e("main","path"+path);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Response.Listener<BoutiqueBean[]> responseDownloadBoutiqueListener() {
        return new Response.Listener<BoutiqueBean[]>() {
            @Override
            public void onResponse(BoutiqueBean[] boutiqueBeen) {
                if (boutiqueBeen != null) {
                    mAdapter.setMore(true);
                    mSwipeRefreshLayout.setRefreshing(false);
                    mtvHint.setVisibility(View.GONE);
                    mAdapter.setFooterText(getResources().getString(R.string.load_more));
                    //将数组转换为集合
                    ArrayList<BoutiqueBean> list = Utils.array2List(boutiqueBeen);
                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mAdapter.initItems(list);
                        Log.e("main", "list" + list);
                    } else if (action == I.ACTION_PULL_UP) {
                        mAdapter.addItems(list);
                    }
                    if (boutiqueBeen.length < I.PAGE_SIZE_DEFAULT) {
                        mAdapter.setMore(false);
                        mAdapter.setFooterText(getResources().getString(R.string.no_more));
                    }
                }
            }
        };
    }

    private void initView(View layout) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.sfl_boutique);
        mSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.google_blue),
                getResources().getColor(R.color.google_green),
                getResources().getColor(R.color.google_red),
                getResources().getColor(R.color.google_yellow)
        );
        mtvHint = (TextView) layout.findViewById(R.id.tv_refresh_hint);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyrlerView = (RecyclerView) layout.findViewById(R.id.rv_boutique);
        mRecyrlerView.setHasFixedSize(true);
        mRecyrlerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new BoutiqueAdapter(mContext, mBoutiqueList);
        mRecyrlerView.setAdapter(mAdapter);
    }
}

