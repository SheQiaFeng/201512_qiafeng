package cn.ucai.fulicenter.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.squareup.okhttp.internal.Util;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.MainActivity;
import cn.ucai.fulicenter.adapter.GoodAdapter;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by Administrator on 2016/6/15.
 */
public class NewGoodFragment extends Fragment {
    Activity mContext;
    GoodAdapter mAdapter;
    ArrayList<NewGoodBean> mGoodList;
    int pageId = 0;
    String path;

    // int pageSize = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_good, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        initView();
        initData();
        mGoodList = new ArrayList<NewGoodBean>();
        mAdapter = new GoodAdapter(mContext, mGoodList);
        setListener();
    }

    private void setListener() {

        //上拉刷新下拉刷新
    }

    private void initData() {
        try {
            path = getPath(pageId);
            mContext.executeRequest(new GsonRequest<NewGoodBean[]>(path,
                    NewGoodBean[].class, responseDownloadNewGoodListener(),
                    errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPath(int pageId) {
        try {
            String path = new ApiParams()
                    .with(I.NewAndBoutiqueGood.CAT_ID, I.CAT_ID + "")
                    .with(I.PAGE_ID, pageId + "")
                    .with(I.PAGE_SIZE, I.PAGE_SIZE_DEFAULT + "")
                    .getRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<NewGoodBean[]> responseDownloadNewGoodListener() {
        return new Response.Listener<NewGoodBean[]>() {
            @Override
            public void onResponse(NewGoodBean[] newGoodBeen) {
                if (newGoodBeen != null) {
                    ArrayList<NewGoodBean> list = Utils.array2List(newGoodBeen);
                    mGoodList.addAll(list);
                    mAdapter.initList(mGoodList);
                }

            }
        };
    }

    private void initView() {


    }
}
