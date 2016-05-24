package cn.ucai.superwechat.utils;

import android.content.Context;
import android.hardware.usb.UsbRequest;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.activity.MainActivity;
import cn.ucai.superwechat.applib.controller.HXSDKHelper;
import cn.ucai.superwechat.DemoHXSDKHelper;
import cn.ucai.superwechat.bean.Contact;
import cn.ucai.superwechat.bean.User;
import cn.ucai.superwechat.data.RequestManager;
import cn.ucai.superwechat.domain.EMUser;

import com.android.volley.toolbox.NetworkImageView;
import com.squareup.picasso.Picasso;

public class UserUtils {
    public static final String TAG = "UserUtils";
    /**
     * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
     * @param username
     * @return
     */
    public static EMUser getUserInfo(String username){
        EMUser user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(username);
        if(user == null){
            user = new EMUser(username);
        }
            
        if(user != null){
            //demo没有这些数据，临时填充
        	if(TextUtils.isEmpty(user.getNick()))
                user.setNick(username);
        }
        return user;
    }

    public static Contact getUserBeanInfo(String  username){
        Contact contact = SuperWeChatApplication.getInstance().getUserList().get(username);
        return contact;
    }



    /**
     * 设置用户头像
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
    	EMUser EMUser = getUserInfo(username);
        if(EMUser != null && EMUser.getAvatar() != null){
            Picasso.with(context).load(EMUser.getAvatar()).placeholder(cn.ucai.superwechat.R.drawable.default_avatar).into(imageView);
        }else{
            Picasso.with(context).load(cn.ucai.superwechat.R.drawable.default_avatar).into(imageView);
        }
    }
    //设置真实的用户头像  仿写
    public static void setUserBeanAvatar(String username,NetworkImageView imageView){
        Contact contact=getUserBeanInfo(username);
        Log.e(TAG,"contact="+contact);
        if (contact!=null&&contact.getMContactCname()!=null){
            setUserAvatar(getAvatarPath(username),imageView);
             }
    }
    private  static void setUserAvatar(String url,NetworkImageView imageView){
        Log.e(TAG,"url:"+url);
        if (url==null||url.isEmpty())return;
        imageView.setDefaultImageResId(R.drawable.default_avatar);
        imageView.setImageUrl(url, RequestManager.getImageLoader());
        imageView.setErrorImageResId(R.drawable.default_avatar);
    }
    //新加的方法
    private static String getAvatarPath(String username) {
        Log.i("main","username:  "+username);
        if (username==null || username.isEmpty()) return null;

        return I.REQUEST_DOWNLOAD_AVATAR_USER + username;
    }

    /**
     * 设置当前用户头像
     */
    public static void setCurrentUserAvatar(Context context, ImageView imageView) {
        EMUser user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
        if (user != null && user.getAvatar() != null) {
            Picasso.with(context).load(user.getAvatar()).placeholder(cn.ucai.superwechat.R.drawable.default_avatar).into(imageView);
        } else {
            Picasso.with(context).load(cn.ucai.superwechat.R.drawable.default_avatar).into(imageView);
        }
    }
    //仿写设置当前头像
    public static  void setCurrentUserAvatar(NetworkImageView imageView){
        User user=SuperWeChatApplication.getInstance().getUser();
        if (user!=null){
            setUserAvatar(getAvatarPath(user.getMUserName()),imageView);
        }
    }
    
    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username,TextView textView){
    	EMUser EMUser = getUserInfo(username);
    	if(EMUser != null){
    		textView.setText(EMUser.getNick());
    	}else{
    		textView.setText(username);
    	}
    }

    //设置UserBean的nick
    public static void setUserBeanNick(String username,TextView textView){
        Contact contact = getUserBeanInfo(username);
        if (contact!=null) {
            if (contact.getMUserNick() != null) {
                textView.setText(contact.getMUserNick());

            } else if (contact.getMContactCname() != null) {
                textView.setText(contact.getMContactCid());
            }
        }else {
            textView.setText(username);
        }

    }
    
    /**
     * 设置当前用户昵称
     */
    public static void setCurrentUserNick(TextView textView){
    	EMUser EMUser = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
    	if(textView != null){
    		textView.setText(EMUser.getNick());
    	}
    }
    
    /**
     * 保存或更新某个用户
     *
     */
	public static void saveUserInfo(EMUser newEMUser) {
		if (newEMUser == null || newEMUser.getUsername() == null) {
			return;
		}
		((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveContact(newEMUser);
	}
    
}
