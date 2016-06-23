package cn.ucai.fulicenter.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.FuliCenterMain2Activity;
import cn.ucai.fulicenter.adapter.CartAdapter;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by Administrator on 2016/6/18.
 */
public class CartFragment extends Fragment {
    FuliCenterMain2Activity mContext;
    ArrayList<CartBean> mCartList;
    CartAdapter mAdapter;
    private int action = I.ACTION_DOWNLOAD;
    String path;
    int pageId = 0;
    //下拉刷新控件
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyrlerView;
    TextView mtvHint;
    LinearLayoutManager mLinearLayoutManager;
    TextView mtvNothing;
    TextView mtvRankPrice;
    TextView mtvSavePrice;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = (FuliCenterMain2Activity) getActivity();
        View layout = inflater.inflate( R.layout.fragment_cart,container,false);
        mCartList = new ArrayList<CartBean>();
        initView(layout);
        setListener();
        initData();
        return layout;
    }
    private void setListener() {
        setPullDownRefreshListener();
        setPullUpRefreshListener();
        registerUpdateCartListener();
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
                                getPath();
                                mContext.executeRequest(new GsonRequest<CartBean[]>(path,
                                        CartBean[].class, responseDownloadCartListener(),
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
                        mContext.executeRequest(new GsonRequest<CartBean[]>(path,
                                CartBean[].class, responseDownloadCartListener(),
                                mContext.errorListener()));
                    }
                }
        );
    }

    private void initData() {
        try {
            getPath();
            ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
            mCartList.clear();
            mCartList.addAll(cartList);
            mAdapter.notifyDataSetChanged();
            sumPrice();
            if (mCartList == null || mCartList.size() == 0) {
                mtvNothing.setVisibility(View.VISIBLE);
            } else {
                mtvNothing.setVisibility(View.GONE);
            }
//            mContext.executeRequest(new GsonRequest<CartBean[]>(path,
//                    CartBean[].class, responseDownloadCartListener(),
//                    mContext.errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPath() {
        try {
            path = new ApiParams()
                    .with(I.PAGE_ID,pageId+"")
                    .with(I.PAGE_SIZE,I.PAGE_SIZE_DEFAULT+"")
                    .with(I.Cart.USER_NAME, FuLiCenterApplication.getInstance().getUserName())
                    .getRequestUrl(I.REQUEST_FIND_CARTS);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Response.Listener<CartBean[]> responseDownloadCartListener() {
        return new Response.Listener<CartBean[]>() {
            @Override
            public void onResponse(CartBean[] boutiqueBeen) {
                if (boutiqueBeen != null) {
                    mAdapter.setMore(true);
                    mSwipeRefreshLayout.setRefreshing(false);
                    mtvHint.setVisibility(View.GONE);
                    //将数组转换为集合
                    ArrayList<CartBean> list = Utils.array2List(boutiqueBeen);
                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mAdapter.initItems(list);
                        Log.e("main", "list" + list);
                    } else if (action == I.ACTION_PULL_UP) {
                        mAdapter.addItems(list);
                    }
                    if (boutiqueBeen.length < I.PAGE_SIZE_DEFAULT) {
                        mAdapter.setMore(false);
                    }
                }
            }
        };
    }

    private void initView(View layout) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.sfl_cart);
        mSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.google_blue),
                getResources().getColor(R.color.google_green),
                getResources().getColor(R.color.google_red),
                getResources().getColor(R.color.google_yellow)
        );
        mtvNothing = (TextView) layout.findViewById(R.id.tv_nothing);
        mtvNothing.setVisibility(View.GONE);
        mtvHint = (TextView) layout.findViewById(R.id.tv_refresh_hint);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyrlerView = (RecyclerView) layout.findViewById(R.id.rv_cart);
        mRecyrlerView.setHasFixedSize(true);
        mRecyrlerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new CartAdapter(mContext, mCartList);
        mRecyrlerView.setAdapter(mAdapter);
        mtvRankPrice = (TextView) layout.findViewById(R.id.tvSumprice);
        mtvSavePrice = (TextView) layout.findViewById(R.id.tvSavePrice);
    }
    public void sumPrice() {
        int sumPrice = 0;
        int crrentPrice = 0;
        if (mCartList != null && mCartList.size() > 0) {
            for (CartBean cart : mCartList) {
                GoodDetailsBean goods = cart.getGoods();
                if (goods != null&&cart.isChecked()) {
                    sumPrice +=convertPrice( goods.getCurrencyPrice() )* cart.getCount();
                    crrentPrice += convertPrice(goods.getRankPrice()) * cart.getCount();
                }
            }
        }
        int savePrice = sumPrice - crrentPrice;
        mtvRankPrice.setText("合计：￥" + sumPrice);
        mtvSavePrice.setText("节省：￥"+savePrice);
    }
    private int convertPrice(String price) {
        price = price.substring(price.indexOf("￥") + 1);
        int p1 = Integer.parseInt(price);
        return p1;
    }

    class UpdateCartReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            initData();

        }
    }
        UpdateCartReceiver mReceiver;
        private  void registerUpdateCartListener(){
            mReceiver = new UpdateCartReceiver();
            IntentFilter filter = new IntentFilter("update_cart");
            mContext.registerReceiver(mReceiver,filter);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
    }
}

