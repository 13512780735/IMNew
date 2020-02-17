package com.bcr.jianxinIM.adapter;

import android.widget.ImageView;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.util.ImageLoaderUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class DynamicAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public DynamicAdapter(int layoutResId, List<String> data) {
        super(R.layout.dynamic_layout_items, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
      //  Glide.with(mContext).load(item.toString()).into((ImageView) helper.getView(R.id.iv_header));

        //ImageLoaderUtil.displayImage(mContext,helper.getView(R.id.iv_header),item,ImageLoaderUtil.getPhotoImageOption1());
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.color.white)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(mContext)
                .load(item)
                .apply(options)
                .into((ImageView) helper.getView(R.id.iv_header));


    }
}
