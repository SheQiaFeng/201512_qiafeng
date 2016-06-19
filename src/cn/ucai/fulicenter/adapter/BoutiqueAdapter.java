package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.BoutiqueChildActivity;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.utils.ImageUtils;

/**
 * Created by Administrator on 2016/6/15.
 */
public class BoutiqueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    ArrayList<BoutiqueBean> mBoutiqueList;

    BoutiqueItemViewHolder boutiqueItemViewHolder;
    FooterViewHolder footerHolder;

    private String footerText;
    private boolean isMore;

    public void setFooterText(String footerText) {
        this.footerText = footerText;
        notifyDataSetChanged();
    }
    public boolean isMore() {
        return isMore;
    }
    public void setMore(boolean more) {
        isMore = more;
    }

    public BoutiqueAdapter(Context mContext, ArrayList<BoutiqueBean> list) {
        this.mContext = mContext;
        this.mBoutiqueList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case I.TYPE_ITEM:
                holder = new BoutiqueItemViewHolder(inflater.inflate(R.layout.item_boutique, parent, false));
                break;
            case I.TYPE_FOOTER:
                holder = new FooterViewHolder(inflater.inflate(R.layout.item_footer, parent, false));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterViewHolder) {
            footerHolder = (FooterViewHolder) holder;
            footerHolder.tvFooter.setText(footerText);
            footerHolder.tvFooter.setVisibility(View.VISIBLE);
        }

        if (holder instanceof BoutiqueItemViewHolder) {
            boutiqueItemViewHolder = (BoutiqueItemViewHolder) holder;
            final BoutiqueBean boutiqueBean = mBoutiqueList.get(position);
            boutiqueItemViewHolder.tvDescription.setText(boutiqueBean.getDescription());
            boutiqueItemViewHolder.tvName.setText(boutiqueBean.getName());
            boutiqueItemViewHolder.tvTitle.setText(boutiqueBean.getTitle());
            ImageUtils.setNewGoodThumb(boutiqueBean.getImageurl(), boutiqueItemViewHolder.iv);

            boutiqueItemViewHolder.layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, BoutiqueChildActivity.class)
                            .putExtra(I.Boutique.NAME, boutiqueBean.getName())
                            .putExtra(I.Boutique.CAT_ID,boutiqueBean.getId()));

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mBoutiqueList == null ? 1 : mBoutiqueList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return I.TYPE_FOOTER;
        } else {
            return I.TYPE_ITEM;
        }
    }

    public void initItems(ArrayList<BoutiqueBean> list) {
        Log.e("main", "list1+++++++++" + list);
        if (mBoutiqueList != null && !mBoutiqueList.isEmpty()) {
            mBoutiqueList.clear();
        }

        mBoutiqueList.addAll(list);

        notifyDataSetChanged();
    }

    public void addItems(ArrayList<BoutiqueBean> list) {
        mBoutiqueList.addAll(list);
        notifyDataSetChanged();

    }
    class BoutiqueItemViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout layoutItem;
        NetworkImageView iv;
        TextView tvTitle;
        TextView tvName;
        TextView tvDescription;

        public BoutiqueItemViewHolder(View itemView) {
            super(itemView);
            layoutItem = (RelativeLayout) itemView.findViewById(R.id.layout_boutique_item);
            iv = (NetworkImageView) itemView.findViewById(R.id.ivBoutiqueImg);
            tvDescription = (TextView) itemView.findViewById(R.id.tvBoutiqueDescription);
            tvTitle = (TextView) itemView.findViewById(R.id.tvBoutiqueTitle);
            tvName = (TextView) itemView.findViewById(R.id.tvBoutiqueName);
        }
    }
}


