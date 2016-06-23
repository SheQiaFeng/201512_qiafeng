package cn.ucai.fulicenter.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.HashMap;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.CollectActivity;
import cn.ucai.fulicenter.activity.FuliCenterMain2Activity;
import cn.ucai.fulicenter.activity.SettingsActivity;
import cn.ucai.fulicenter.task.DownloadCollectCountTask;
import cn.ucai.fulicenter.utils.UserUtils;

/**
 * Created by Administrator on 2016/6/20.
 */
public class PersonalCenterFragment extends Fragment {
    FuliCenterMain2Activity mContext;
    MyClickListener listener;
    //资源文件
    private int[] pic_path = {R.drawable.order_list1,
            R.drawable.order_list2,
            R.drawable.order_list3,
            R.drawable.order_list4,
            R.drawable.order_list5
    };
    NetworkImageView mivUserAvarar;
    TextView mtvUserName;
    TextView mtvCollectCount;
    TextView mtvSettings;
    ImageView mivMessage;
    LinearLayout mLayoutCenterCollet;
    RelativeLayout mLayoutCenterUserInfo;

    int mCollectCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = (FuliCenterMain2Activity) getActivity();
        View layout = View.inflate(mContext, R.layout.fragment_personal_center, null);
        initView(layout);
        initData();
        setListener();
        return layout;
    }

    private void setListener() {
        registerCollectCountChangedListener();
        registerUpdateUserReceiver();
        listener = new MyClickListener();
        mtvSettings.setOnClickListener(listener);
        mLayoutCenterUserInfo.setOnClickListener(listener);
        mLayoutCenterCollet.setOnClickListener(listener);
    }

    class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_center_settings:
                case R.id.center_user_info:
                    startActivity(new Intent(mContext, SettingsActivity.class));
                    break;
                case R.id.layout_center_collect:
                    startActivity(new Intent(mContext, CollectActivity.class));
                    break;
            }
        }
    }


    private void initData() {
        mCollectCount = FuLiCenterApplication.getInstance().getCollectCount();
        Log.e("main", "++++++++mCollectCount:" + mCollectCount);
        mtvCollectCount.setText("" + mCollectCount);
        if (FuLiCenterApplication.getInstance().getUser() != null) {
            UserUtils.setCurrentUserAvatar(mivUserAvarar);
            UserUtils.setCurrentUserBeanNick(mtvUserName);
        }
    }

    private void initView(View layout) {
        mivUserAvarar = (NetworkImageView) layout.findViewById(R.id.iv_user_avater);
        mtvUserName = (TextView) layout.findViewById(R.id.tv_user_name);
        mLayoutCenterCollet = (LinearLayout) layout.findViewById(R.id.layout_center_collect);
        mtvCollectCount = (TextView) layout.findViewById(R.id.tv_collect_count);
        mtvSettings = (TextView) layout.findViewById(R.id.tv_center_settings);
        mivMessage = (ImageView) layout.findViewById(R.id.iv_personal_center_msg);
        mLayoutCenterUserInfo = (RelativeLayout) layout.findViewById(R.id.center_user_info);

        initOrderList(layout);
    }

    private void initOrderList(View layout) {
        //显示GidView的界面
        GridView mOrderList = (GridView) layout.findViewById(R.id.center_user_order_list);
        ArrayList<HashMap<String, Object>> imagelist = new ArrayList<HashMap<String, Object>>();
        //使用HasMap将图片添加到一个数组中
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("image", R.drawable.order_list1);
        imagelist.add(map1);
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("image", R.drawable.order_list2);
        imagelist.add(map2);
        HashMap<String, Object> map3 = new HashMap<String, Object>();
        map3.put("image", R.drawable.order_list3);
        imagelist.add(map3);
        HashMap<String, Object> map4 = new HashMap<String, Object>();
        map4.put("image", R.drawable.order_list4);
        imagelist.add(map4);
        HashMap<String, Object> map5 = new HashMap<String, Object>();
        map5.put("image", R.drawable.order_list5);
        imagelist.add(map5);
        SimpleAdapter simpleAdapter = new SimpleAdapter(mContext, imagelist, R.layout.simple_grid_item, new String[]{"image"}, new int[]{R.id.image});
        mOrderList.setAdapter(simpleAdapter);
    }

    class CollectCountChangedReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
       initData();
        }
    }

    CollectCountChangedReceive mReceiver;

    private void registerCollectCountChangedListener() {
        mReceiver = new CollectCountChangedReceive();
        IntentFilter filter = new IntentFilter("update_collect_count");
        mContext.registerReceiver(mReceiver, filter);
    }

    //登录成功下载商品数量
    //下载商品数量
    //登录完之后就登录到个人中心
    class UpdateUserChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            new DownloadCollectCountTask(mContext).execute();
            initData();
        }
    }

    UpdateUserChangedReceiver mUserReceiver;

    private void registerUpdateUserReceiver() {
        mUserReceiver = new UpdateUserChangedReceiver();
        IntentFilter filter = new IntentFilter("update_user");
        mContext.registerReceiver(mUserReceiver, filter);
    }


    //当数量改变的时候，刷新数量
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);

        }
        if (mUserReceiver != null) {
            mContext.unregisterReceiver(mUserReceiver);
        }
    }

}