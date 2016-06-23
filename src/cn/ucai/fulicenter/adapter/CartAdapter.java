package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.task.UpdateCartTask;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by Administrator on 2016/6/15.
 */
public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    ArrayList<CartBean> mCartList;

    CartItemViewHolder cartViewHolder;
    private boolean isMore;

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public CartAdapter(Context mContext, ArrayList<CartBean> list) {
        this.mContext = mContext;
        this.mCartList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        RecyclerView.ViewHolder holder = new CartItemViewHolder(inflater.inflate(R.layout.item_cart, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        cartViewHolder = (CartItemViewHolder) holder;
        final CartBean cart = mCartList.get(position);
        GoodDetailsBean goods = cart.getGoods();
        if (goods == null) {
            return;
        }
        cartViewHolder.tvGoodName.setText(goods.getGoodsName());
        cartViewHolder.tvCartCount.setText(""+cart.getCount());
        cartViewHolder.mChkCart.setChecked(cart.isChecked());

        cartViewHolder.tvPrice.setText(goods.getRankPrice());
        ImageUtils.setNewGoodThumb(goods.getGoodsThumb(), cartViewHolder.iv);
        AddDelCartClickListener listener = new AddDelCartClickListener(goods);
        cartViewHolder.mivAdd.setOnClickListener(listener);
        cartViewHolder.mivReduce.setOnClickListener(listener);

        cartViewHolder.mChkCart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cart.setChecked(isChecked);
                new UpdateCartTask(mContext,cart).execute();
            }
        });

    }


    @Override
    public int getItemCount() {
        return mCartList == null ? 0 : mCartList.size();
    }


    public void initItems(ArrayList<CartBean> list) {
        Log.e("main", "list1+++++++++" + list);
        if (mCartList != null && !mCartList.isEmpty()) {
            mCartList.clear();
        }

        mCartList.addAll(list);

        notifyDataSetChanged();
    }

    public void addItems(ArrayList<CartBean> list) {
        mCartList.addAll(list);
        notifyDataSetChanged();

    }

    class CartItemViewHolder extends RecyclerView.ViewHolder {
        CheckBox mChkCart;
        NetworkImageView iv;
        TextView tvGoodName;
        TextView tvCartCount;
        TextView tvPrice;
        ImageView mivAdd;
        ImageView mivReduce;


        public CartItemViewHolder(View itemView) {
            super(itemView);
            iv = (NetworkImageView) itemView.findViewById(R.id.ivGoodsThumb);
            tvPrice = (TextView) itemView.findViewById(R.id.tvGoodsPrice);
            tvGoodName = (TextView) itemView.findViewById(R.id.tvGoodsName);
            tvCartCount = (TextView) itemView.findViewById(R.id.tvCartCount);
            mChkCart = (CheckBox) itemView.findViewById(R.id.chkSelect);
            mivAdd = (ImageView) itemView.findViewById(R.id.ivAddCart);
            mivReduce = (ImageView) itemView.findViewById(R.id.ivReduceCart);
        }
    }

    class AddDelCartClickListener implements View.OnClickListener {
        GoodDetailsBean good ;
        public AddDelCartClickListener(GoodDetailsBean good) {
            this.good = good;
        }
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ivAddCart:
                    Utils.addCart(mContext,good);
                    break;
                case R.id.ivReduceCart:
                    Utils.delCart(mContext, good);
                    break;
            }
        }
    }
}


