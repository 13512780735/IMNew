package com.bcr.jianxinIM.view;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.event.EventBusCarrier;
import com.bcr.jianxinIM.model.CircleFriendsModel1;
import com.bcr.jianxinIM.model.ComUserBean;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.annotation.Nullable;


public class CommentsView extends LinearLayout {

    private Context mContext;
    private List<CircleFriendsModel1.CommentBean> mDatas;
    private onItemClickListener listener;
    private TextView textView;
    private CircleFriendsModel1.CommentBean item;
    private CircleFriendsModel1 circleFriendsModel1;
    private CommentsView commentView;

    public CommentsView(Context context) {
        this(context, null);
    }

    public CommentsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);

        this.mContext = context;
    }

    /**
     * 设置评论列表信息
     *  @param list
     * @param item
     * @param commentViews
     */
    public void setList(List<CircleFriendsModel1.CommentBean> list, CircleFriendsModel1 item, CommentsView commentViews) {
        mDatas = list;
        circleFriendsModel1=item;
        commentView=commentViews;
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }

    public void notifyDataSetChanged() {
        removeAllViews();
        if (mDatas == null || mDatas.size() <= 0) {
            return;
        }
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 10, 0, 10);
        for (int i = 0; i < mDatas.size(); i++) {
            View view = getView(i);
            if (view == null) {
                throw new NullPointerException("listview item layout is null, please check getView()...");
            }
            addView(view, i, layoutParams);
        }
    }

    private View getView(final int position) {
        item = mDatas.get(position);
        String  friuserId= item.getFriuser().getUserId();
        boolean hasReply = false;   // 是否有回复
        if (!TextUtils.isEmpty(friuserId)) {
            hasReply = true;
        }
        textView = new TextView(mContext);
        textView.setTextSize(15);
        textView.setTextColor(0xff686868);

        SpannableStringBuilder builder = new SpannableStringBuilder();
        ComUserBean myUser = item.getMyuser();
        String name = myUser.getUserName();
        if (hasReply) {
            builder.append(setClickableSpan1(name, position));
            builder.append(" 回复 ");
            builder.append(setClickableSpan(item.getFriuser().getUserName(), position));
        } else {
            builder.append(setClickableSpan1(name, position));
        }
        builder.append(" : ");
        builder.append(setClickableSpanContent(item.getContent(), position));
        builder.append(" ");
        textView.setText(builder);
        // 设置点击背景色
        textView.setHighlightColor(getResources().getColor(android.R.color.transparent));
        final CircleMovementMethod method = new CircleMovementMethod(0xffcccccc, 0xffcccccc);
        textView.setMovementMethod(method);
        
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (method.isParseTv()) {
                    if (listener != null) {
                        listener.onItemClick(position, item);
                    }
                }
            }
        });

        return textView;
    }

    /**
     * 设置评论内容点击事件
     *
     * @param items
     * @param position
     * @return
     */
    public SpannableString setClickableSpanContent(final String items, final int position) {
        final SpannableString string = new SpannableString(items);
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // TODO: 2017/9/3 评论内容点击事件
                //Toast.makeText(mContext, "position: " + position + " , content: " + item, Toast.LENGTH_SHORT).show();

                        EventBusCarrier eventBusCarrier = new EventBusCarrier();
                        eventBusCarrier.setPosition(position);
                        eventBusCarrier.setType(1);
                        eventBusCarrier.setCommentBean(mDatas.get(position));
                        eventBusCarrier.setCircleFriendsModel1(circleFriendsModel1);
                        eventBusCarrier.setView(commentView);
                        EventBus.getDefault().post(eventBusCarrier); //普通事件发布
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                // 设置显示的内容文本颜色
                ds.setColor(0xff686868);
                ds.setUnderlineText(false);
            }
        };
        string.setSpan(span, 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return string;
    }

    /**
     * 设置评论用户名字点击事件
     *
     * @param item
     * @return
     */
    public SpannableString setClickableSpan(final String item, final int position) {
        final SpannableString string = new SpannableString(item);
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // TODO: 2017/9/3 评论用户名字点击事件
               //Toast.makeText(mContext, bean.getUserName(), Toast.LENGTH_SHORT).show();
                EventBusCarrier eventBusCarrier = new EventBusCarrier();
                eventBusCarrier.setPosition(position);
                eventBusCarrier.setType(0);
                eventBusCarrier.setCommentBean(mDatas.get(position));
                eventBusCarrier.setCircleFriendsModel1(circleFriendsModel1);
                eventBusCarrier.setView(commentView);
                EventBus.getDefault().post(eventBusCarrier); //普通事件发布
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                // 设置显示的用户名文本颜色
                //ds.setColor(0xff387dcc);
                ds.setColor(getResources().getColor(R.color.theme_color));
                ds.setUnderlineText(false);
            }
        };

        string.setSpan(span, 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return string;
    }
    public SpannableString setClickableSpan1(final String item, final int position) {
        final SpannableString string = new SpannableString(item);
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // TODO: 2017/9/3 评论用户名字点击事件
                //Toast.makeText(mContext, bean.getUserName(), Toast.LENGTH_SHORT).show();
                EventBusCarrier eventBusCarrier = new EventBusCarrier();
                eventBusCarrier.setPosition(position);
                eventBusCarrier.setType(3);
                eventBusCarrier.setCommentBean(mDatas.get(position));
                eventBusCarrier.setCircleFriendsModel1(circleFriendsModel1);
                EventBus.getDefault().post(eventBusCarrier); //普通事件发布
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                // 设置显示的用户名文本颜色
                //ds.setColor(0xff387dcc);
                ds.setColor(getResources().getColor(R.color.theme_color));
                ds.setUnderlineText(false);
            }
        };

        string.setSpan(span, 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return string;
    }
    /**
     * 定义一个用于回调的接口
     */
    public interface onItemClickListener {
        void onItemClick(int position, CircleFriendsModel1.CommentBean bean);
    }

}
