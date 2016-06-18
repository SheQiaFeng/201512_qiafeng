package cn.ucai.fulicenter.data;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader;

public class BitmapLruCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {
	public BitmapLruCache(int maxSize) {
		super(maxSize);
	}

	@Override
	protected int sizeOf(String key, Bitmap bitmap) {
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

	@Override
	public Bitmap getBitmap(String url) {
		Bitmap bitmap = get(url);
		//如果没有在内存中找到则去磁盘缓存中查找
		if (bitmap == null) {
			bitmap = RequestManager.getBitmap(url);
			//如果磁盘中找到，就添加到内存缓存中
			if (bitmap != null) {
				putBitmap(url, bitmap);
			}
		}
			return bitmap;
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		Log.v("BitmapLurCache", "Added item to Mem Cache");
		put(url, bitmap);
		RequestManager.putBitmap(url, bitmap);

	}
}
