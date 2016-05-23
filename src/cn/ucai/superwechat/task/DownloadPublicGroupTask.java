package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;


import com.android.volley.Response;

import java.util.ArrayList;
import java.util.HashMap;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.activity.BaseActivity;
import cn.ucai.superwechat.bean.Contact;
import cn.ucai.superwechat.data.ApiParams;
import cn.ucai.superwechat.data.GsonRequest;
import cn.ucai.superwechat.utils.Utils;

/**
 * Created by Administrator on 2016/5/23 0023.
 */
public class DownloadPublicGroupTask extends BaseActivity {
    private  static  final  String TAG= DownloadPublicGroupTask.class.getName();
    Context mContext;
    String username;
    String path;

    public DownloadPublicGroupTask(Context mContext, String username, int pageIdDefault, int pageSizeDefault) {
        this.mContext = mContext;
        this.username = username;
        initPath();
    }

    private void initPath() {
        try {
            path=new ApiParams()
                    .with(I.Contact.USER_NAME,username)
                    .getRequestUrl(I.REQUEST_FIND_PUBLIC_GROUPS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void execute() {
        executeRequest(new GsonRequest<Contact[]>(path, Contact[].class
                , responseDownloadPublicGroupTaskListener(), errorListener()));
    }

    private Response.Listener<Contact[]> responseDownloadPublicGroupTaskListener() {
        return new Response.Listener<Contact[]>() {
            public void onResponse(Contact[] response) {
                if (response != null) {
                    ArrayList<Contact> contactList =
                            SuperWeChatApplication.getInstance().getContactList();
                    ArrayList<Contact> list = Utils.array2List(response);
                    contactList.clear();
                    contactList.addAll(list);
                    HashMap<String, Contact> userList =
                            SuperWeChatApplication.getInstance().getUserList();
                    userList.clear();
                    for (Contact c : list) {
                        userList.put(c.getMContactCname(), c);
                    }
                }
                mContext.sendBroadcast(new Intent("update_public_group"));

            }
        };

    }
}