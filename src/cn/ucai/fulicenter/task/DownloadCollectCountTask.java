package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.User;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;

/**
 * Created by Administrator on 2016/6/20.
 */
public class DownloadCollectCountTask extends BaseActivity {
    Context mContext;
    String path;

    public DownloadCollectCountTask(Context mContext) {
        this.mContext = mContext;
        initPath();

    }

    private void initPath() {
        try {
            User user = FuLiCenterApplication.getInstance().getUser();
            if (user != null) {
                path = new ApiParams()
                        .with(I.Collect.USER_NAME, user.getMUserName())
                        .getRequestUrl(I.REQUEST_FIND_COLLECT_COUNT);
                Log.e("main", "userName:" + user.getMUserName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        if (path == null || path.isEmpty()) return;
        executeRequest(new GsonRequest<MessageBean>(path, MessageBean.class,
                responseDownloadCollectCountListener(), errorListener()));
    }

    private Response.Listener<MessageBean> responseDownloadCollectCountListener() {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean messageBean) {
                if (messageBean.isSuccess()) {
                    String count = messageBean.getMsg();
                    FuLiCenterApplication.getInstance().setCollectCount(Integer.parseInt(count));
                    Log.e("main", "count:" + count);
                    Log.e("main", "Msg:" + messageBean.getMsg());
                } else {
                    FuLiCenterApplication.getInstance().setCollectCount(0);
                    Log.e("main", "Msg:" + messageBean.getMsg());
                    Log.e("main", "++count=false++");
                }
                Intent intent = new Intent("update_collect_count");
                mContext.sendStickyBroadcast(intent);
            }
        };
    }

}
