package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.NetworkImageView;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.AlbumBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.User;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.task.DownloadCollectCountTask;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.DisplayUtils;
import cn.ucai.fulicenter.view.FlowIndicator;
import cn.ucai.fulicenter.view.SlideAutoLoopView;


/**
 * Created by Administrator on 2016/6/16.
 */
public class GoodDetailActivity extends BaseActivity {
    Context mContext;
    GoodDetailsBean mGood;
    int mGoodsId;
    SlideAutoLoopView mSlideAutoLoopView;
    FlowIndicator mFlowIndicator;
   /*  * 显示颜色的容器布局
     */
    LinearLayout mLayoutColors;
    ImageView mivCollect;
    ImageView mivAddCart;
    ImageView mivShare;
    TextView mtvCartCount;
    TextView tvGoodName;
    TextView tvGoodEnglishName;
    TextView tvShopPrice;
    TextView tvCurrencyPrice;
    WebView wvGoodBrief;

    /**
     * 当前颜色值
     **/
    int mCurrentColor;

    boolean isCollect;
    int actionCollect;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_good_details);
        mContext = this;
        initView();
       initData();
        setListener();
    }
    private void setListener() {
        setCollectClickListener();
        setCartClickListener();
        RegisterUpdateCartListener();
    }

    private void setCartClickListener() {
        mivAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Utils.addCart(mContext,mGood);
            }
        });
    }

    private void setCollectClickListener() {
        mivCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = FuLiCenterApplication.getInstance().getUser();
                if (user == null) {
                    //没有登录就跳转到登录页面
                    startActivity(new Intent(GoodDetailActivity.this, LoginActivity.class));
                } else {
                    try {
                        String path;
                        if (isCollect) {
                            actionCollect = I.ACTION_DEL_COLLECT;
                            path = new ApiParams()
                                    .with(I.Collect.USER_NAME, user.getMUserName())
                                    .with(I.Collect.GOODS_ID,mGoodsId+ "")
                                    .getRequestUrl(I.REQUEST_DELETE_COLLECT);
                            Log.e("main","mGoodsId:"+mGoodsId);
                            Log.e("main","UserName:"+user.getMUserName());
                            Log.e("main", "取消收藏:" + path);
                        } else {
                            actionCollect = I.ACTION_ADD_COLLECT;
                            path = new ApiParams()
                                    .with(I.Collect.USER_NAME, user.getMUserName())
                                    .with(I.Collect.GOODS_ID,mGoodsId+ "")
                                    .with(I.Collect.GOODS_NAME, mGood.getGoodsName())
                                    .with(I.Collect.GOODS_ENGLISH_NAME, mGood.getGoodsEnglishName())
                                    .with(I.Collect.GOODS_THUMB, mGood.getGoodsThumb())
                                    .with(I.Collect.GOODS_IMG, mGood.getGoodsImg())
                                    .with(I.Collect.ADD_TIME, mGood.getAddTime() + "")
                                    .getRequestUrl(I.REQUEST_ADD_COLLECT);
                            Log.e("main", "增加收藏:" + path);
                            Log.e("main", "增加收藏(mGoodsId)：" + mGoodsId);
                        }
                        executeRequest(new GsonRequest<MessageBean>(path,MessageBean.class,
                                responseSetCollectListener(),errorListener()));
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            }
        });
    }
    private Response.Listener<MessageBean> responseSetCollectListener() {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean messageBean) {
                if (messageBean.isSuccess()) {
                    if (actionCollect == I.ACTION_ADD_COLLECT) {
                        isCollect = true;
                        Log.e("main", "actionCollect:" + actionCollect);
                        mivCollect.setImageResource(R.drawable.bg_collect_out);
                    } else  if (actionCollect == I.ACTION_DEL_COLLECT) {
                        isCollect = false;
                        mivCollect.setImageResource(R.drawable.bg_collect_in);
                        Log.e("main", "actionCollect:" + actionCollect);
                    }
                    new DownloadCollectCountTask(mContext).execute();
                }
                Utils.showToast(mContext,messageBean.getMsg(),Toast.LENGTH_SHORT);
            }
        };
    }
    private void initData() {
        mGoodsId=getIntent().getIntExtra(D.NewGood.KEY_GOODS_ID,0);
        try {
            String path = new ApiParams()
                    .with(D.NewGood.KEY_GOODS_ID, mGoodsId + "")
                    .getRequestUrl(I.REQUEST_FIND_GOOD_DETAILS);
            executeRequest(new GsonRequest<GoodDetailsBean>(path, GoodDetailsBean.class,
                    responseDownloadGoodDetailsListener(),errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   private Response.Listener<GoodDetailsBean> responseDownloadGoodDetailsListener() {
        return new Response.Listener<GoodDetailsBean>() {
            @Override
            public void onResponse(GoodDetailsBean goodDetailsBean) {
                if (goodDetailsBean != null) {
                    mGood = goodDetailsBean;
                   //设置商品名称，价格，webVIEW的简介
                    DisplayUtils.initBackWithTitle(GoodDetailActivity.this, getResources().getString(R.string.title_good_details));
                 tvCurrencyPrice.setText(mGood.getCurrencyPrice());
                   tvGoodEnglishName.setText(mGood.getGoodsEnglishName());
                  tvGoodName.setText(mGood.getGoodsName());
                  wvGoodBrief.loadDataWithBaseURL(null, mGood.getGoodsBrief().trim(), D.TEXT_HTML, D.UTF_8, null);
                  initColorBanner();
              } else {
                 Utils.showToast(mContext,"商品详情下载失败", Toast.LENGTH_SHORT);
                    finish();
              }
           }
        };
   }
    private void initColorBanner() {
        updateColor(0);
        for (int i=0;i<mGood.getProperties().length;i++) {
            mCurrentColor = i;
            View layout = View.inflate(mContext, R.layout.layout_property_color, null);
            final NetworkImageView ivColor = (NetworkImageView) layout.findViewById(R.id.ivColorItem);
            String colorImg = mGood.getProperties()[i].getColorImg();
            if (colorImg.isEmpty()) {
                continue;

            }
            ImageUtils.setGoodDetailThumb(colorImg, ivColor);
            mLayoutColors.addView(layout);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateColor(mCurrentColor);
                }
            });
        }
    }
    private void updateColor(int i) {
        AlbumBean[] albums = mGood.getProperties()[i].getAlbums();
        String[] albumImgUrl = new String[albums.length];
        for (int j=0;j<albumImgUrl.length;j++) {
            albumImgUrl[j] = albums[j].getImgUrl();

        }
       mSlideAutoLoopView.startPlayLoop(mFlowIndicator,albumImgUrl,albumImgUrl.length);
    }
    private void initView() {
        mivCollect = (ImageView) findViewById(R.id.ivCollect);
        mivAddCart = (ImageView) findViewById(R.id.ivAddCart);
        mivShare = (ImageView) findViewById(R.id.ivShare);
        mtvCartCount = (TextView) findViewById(R.id.tvCartCount);
//
        mSlideAutoLoopView = (SlideAutoLoopView) findViewById(R.id.salv);
       mFlowIndicator = (FlowIndicator) findViewById(R.id.indicator);
        mLayoutColors = (LinearLayout) findViewById(R.id.layoutColorSelector);
        tvCurrencyPrice = (TextView) findViewById(R.id.tvCurrencyPrice);
        tvGoodEnglishName = (TextView) findViewById(R.id.tvGoodEnglishName);
        tvGoodName = (TextView) findViewById(R.id.tvGoodName);
        wvGoodBrief = (WebView) findViewById(R.id.wvGoodBrief);
        WebSettings settings = wvGoodBrief.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setBuiltInZoomControls(true);

    }
    @Override
    protected void onResume() {
        super.onResume();
        initCollectStatus();
        initCartStatus();
    }

    private void initCartStatus() {
        int count = Utils.sumCartCount();
        if (count > 0) {
            mtvCartCount.setVisibility(View.VISIBLE);
            mtvCartCount.setText("" + count);
        } else {
            mtvCartCount.setVisibility(View.GONE);
            mtvCartCount.setText("0");
        }
    }

    private void initCollectStatus() {
        User user = FuLiCenterApplication.getInstance().getUser();
        //判断用户是否登录
        if (user != null) {
            try {
                //查看用户是否被收藏
                Log.e("main", "判断用户是否有收藏:"+user.getMUserName());
                String path = new ApiParams()
                        .with(I.Collect.USER_NAME, FuLiCenterApplication.getInstance().getUserName())
                        .with(I.Collect.GOODS_ID,mGoodsId+ "")
                        .getRequestUrl(I.REQUEST_IS_COLLECT);
                Log.e("main", "商品Id：" + mGoodsId);
                Log.e("main", "收藏path:" + path);
                executeRequest(new GsonRequest<MessageBean>(path,MessageBean.class,
                        responsIsCollectListener(),errorListener()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            isCollect = false;
            mivCollect.setImageResource(R.drawable.bg_collect_in);
        }
    }
//设置背景颜色
    private Response.Listener<MessageBean> responsIsCollectListener() {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean messageBean) {
                Log.e("main", "message=" + messageBean);
                if (messageBean.isSuccess()) {
                    isCollect = true;
                    mivCollect.setImageResource(R.drawable.bg_collect_out);
                }else {
                    isCollect = false;
                    mivCollect.setImageResource(R.drawable.bg_collect_in);
                }
            }
        };
    }

    class UpdateCartReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            initCartStatus();
        }
    }

    UpdateCartReceiver mReceiver;
    private void RegisterUpdateCartListener() {
        mReceiver = new UpdateCartReceiver();
        IntentFilter filter = new IntentFilter("update_cart");
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }
}


