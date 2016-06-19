package cn.ucai.fulicenter.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.GoodAdapter;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.ColorBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.CatChildFilterButton;
import cn.ucai.fulicenter.view.ColorFilterButton;
import cn.ucai.fulicenter.view.DisplayUtils;

/**
 * Created by Administrator on 2016/6/15.
 */
public class CategoryChildActivity extends BaseActivity {
    CategoryChildActivity mContext;
    GoodAdapter mAdapter;
    ArrayList<NewGoodBean> mGoodList;
    private int pageId = 0;
    private int action = I.ACTION_DOWNLOAD;
    String path;
    int catId;

    //下拉刷新控件
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyrlerView;
    TextView mtvHint;
    GridLayoutManager mGridLayoutManager;
    // int pageSize = 0;
    /*按照价格排序*/
    Button mbtnPriceSort;
    /**按照时间排序**/
    Button mbtnAddTimeSort;
    boolean mSortByPriceAsc;
    boolean mSortByAddTimeAsc;

    CatChildFilterButton mCatChildFilterButton;
    String groupName;
    ArrayList<CategoryChildBean> mChildList;
    private int sortBy;

    ColorFilterButton mColorFilterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_child);
        mContext = this;
        mGoodList = new ArrayList<NewGoodBean>();
        sortBy = I.SORT_BY_ADDTIME_DESC;
        mChildList = new ArrayList<CategoryChildBean>();
        initView();
        initData();

        setListener();
    }

    private void setListener() {
        setPullDownRefreshListener();
        setPullUpRefreshListener();
        SortStateChangedListener mSortStateChangedListener = new SortStateChangedListener();
        mbtnPriceSort.setOnClickListener(mSortStateChangedListener);
        mbtnAddTimeSort.setOnClickListener(mSortStateChangedListener);
        mCatChildFilterButton.setOnCatFilterClickListener(groupName,mChildList);

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
            mChildList = (ArrayList<CategoryChildBean>) getIntent().getSerializableExtra("childList");
            getPath(pageId);
            mContext.executeRequest(new GsonRequest<NewGoodBean[]>(path,
                    NewGoodBean[].class, responseDownloadNewGoodListener(),
                    mContext.errorListener()));
            String colorPath = new ApiParams()
                    .with(I.Color.CAT_ID, catId + "")
                    .getRequestUrl(I.REQUEST_FIND_COLOR_LIST);
            mContext.executeRequest(new GsonRequest<ColorBean[]>(colorPath,ColorBean[].class,
                    responseDownloadColorListener(),mContext.errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<ColorBean[]> responseDownloadColorListener() {
        return new Response.Listener<ColorBean[]>() {
            @Override
            public void onResponse(ColorBean[] colorBeen) {
                if (colorBeen != null) {
                    mColorFilterButton.setVisibility(View.VISIBLE);
                    ArrayList<ColorBean> list = Utils.array2List(colorBeen);
                    mColorFilterButton.setOnColorFilterClickListener(groupName,mChildList,list);
                }
            }
        };
    }


    private String getPath(int pageId) {
        try {
             catId= getIntent().getIntExtra(I.CategoryChild.CAT_ID, 0);
            path = new ApiParams()
                    .with(I.NewAndBoutiqueGood.CAT_ID, catId + "")
                    .with(I.PAGE_ID, pageId + "")
                    .with(I.PAGE_SIZE, I.PAGE_SIZE_DEFAULT + "")
                    .getRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS);
            Log.e("main", "NewPath" + path);
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

    private void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.sfl_category_child);
        mSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.google_blue),
                getResources().getColor(R.color.google_green),
                getResources().getColor(R.color.google_red),
                getResources().getColor(R.color.google_yellow)
        );
        mtvHint = (TextView) findViewById(R.id.tv_refresh_hint);
        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyrlerView = (RecyclerView)findViewById(R.id.rv_category_child);
        mRecyrlerView.setHasFixedSize(true);
        mRecyrlerView.setLayoutManager(mGridLayoutManager);
        mAdapter = new GoodAdapter(mContext, mGoodList,sortBy);
        mRecyrlerView.setAdapter(mAdapter);
        groupName = getIntent().getStringExtra(I.Boutique.NAME);
        groupName = getIntent().getStringExtra(I.CategoryGroup.NAME);
        DisplayUtils.initBack(mContext);
        mbtnPriceSort = (Button) findViewById(R.id.btn_price_sort);
        mbtnAddTimeSort = (Button) findViewById(R.id.btn_add_time_sort);
        mCatChildFilterButton = (CatChildFilterButton) findViewById(R.id.btnCatChildFilter);
        mCatChildFilterButton.setText(groupName);
        mColorFilterButton = (ColorFilterButton) findViewById(R.id.btnColorFilter);
    }

     class SortStateChangedListener implements View.OnClickListener {
         @Override
         public void onClick(View v) {
             Drawable right = null;
             int resId;
             switch (v.getId()) {
                 case R.id.btn_price_sort:
                     if (mSortByPriceAsc) {
                         sortBy = I.SORT_BY_PRICE_ASC;
                         right = mContext.getResources().getDrawable(R.drawable.arrow_order_up);
                         resId = R.drawable.arrow_order_up;
                     } else {
                         sortBy = I.SORT_BY_PRICE_DESC;
                         right = mContext.getResources().getDrawable(R.drawable.arrow_order_down);
                         resId = R.drawable.arrow_order_down;
                     }
                     mSortByPriceAsc =! mSortByPriceAsc;
                     right.setBounds(0, 0, ImageUtils.getDrawableWidth(mContext, resId), ImageUtils.getDrawableHeight(mContext, resId));
                     mbtnPriceSort.setCompoundDrawables(null, null, right, null);
                     break;
                 case R.id.btn_add_time_sort:
                     if (mSortByAddTimeAsc) {
                         sortBy = I.SORT_BY_ADDTIME_ASC;
                         right = mContext.getResources().getDrawable(R.drawable.arrow_order_up);
                         resId = R.drawable.arrow_order_up;
                     } else {
                         sortBy = I.SORT_BY_ADDTIME_DESC;
                         right = mContext.getResources().getDrawable(R.drawable.arrow_order_down);
                         resId = R.drawable.arrow_order_down;
                     }
                     mSortByAddTimeAsc =! mSortByAddTimeAsc;
                     right.setBounds(0, 0, ImageUtils.getDrawableWidth(mContext, resId), ImageUtils.getDrawableHeight(mContext, resId));
                     mbtnAddTimeSort.setCompoundDrawables(null, null, right, null);
                     break;
             }
             mAdapter.setSortBy(sortBy);
         }
     }
}
