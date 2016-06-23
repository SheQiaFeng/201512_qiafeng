package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by Administrator on 2016/5/23 0023.
 */
public class DownloadCartListTask extends BaseActivity {
    private static final String TAG = DownloadCartListTask.class.getName();//log.x需要
    Context mContext;
    String username;
    int pageId;
    int pageSize;
    String path;
    int listSize;
    ArrayList<CartBean> list;


    public DownloadCartListTask(Context mContext, String username, int pageId, int pageSize) {
        this.mContext = mContext;
        this.username = FuLiCenterApplication.getInstance().getUserName();
        this.pageId = pageId;
        this.pageSize = pageSize;
        initPath();
    }

    private void initPath() {
        try {
            path = new ApiParams()
                    .with(I.Cart.USER_NAME, username)
                    .with(I.PAGE_ID, pageId + "")
                    .with(I.PAGE_SIZE, pageSize + "")
                    .getRequestUrl(I.REQUEST_FIND_CARTS);
            execute();
//            Log.i("path","++++++++++"+path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        executeRequest(new GsonRequest<CartBean[]>(path, CartBean[].class
                , responseDownloadCartListListener(), errorListener()));
    }

    private Response.Listener<CartBean[]> responseDownloadCartListListener() {
        return new Response.Listener<CartBean[]>() {
            public void onResponse(CartBean[] response) {
                if (response != null) {
                    list = Utils.array2List(response);
                    try {
                        for (CartBean cart : list) {
                            path = new ApiParams()
                                    .with(D.NewGood.KEY_GOODS_ID, cart.getGoodsId() + "")
                                    .getRequestUrl(I.REQUEST_FIND_GOOD_DETAILS);
                            executeRequest(new GsonRequest<GoodDetailsBean>(path, GoodDetailsBean.class,
                                    responseDownloadGoodDateilListener(cart),errorListener()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mContext.sendBroadcast(new Intent("update_CartBean_list"));

                }
            }


        };


    }

    private Response.Listener<GoodDetailsBean> responseDownloadGoodDateilListener(final CartBean cart) {
        return new Response.Listener<GoodDetailsBean>() {
            @Override
            public void onResponse(GoodDetailsBean goodDetailsBean) {
                listSize++;
                if (goodDetailsBean != null) {
                    cart.setGoods(goodDetailsBean);
                    ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
                    if (!cartList.contains(cart)) {
                        cartList.add(cart);
                    }
                }
                if (listSize == list.size()) {
                    mContext.sendStickyBroadcast(new Intent("update_cart_list"));

                }
            }

        };
    }

}
