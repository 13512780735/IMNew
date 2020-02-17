package com.bcr.jianxinIM.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.Touch;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.bcr.jianxinIM.activity.base_util.ToastUtils;
import com.bcr.jianxinIM.model.CircleFriendsModel1;

import javax.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class ClickTextView extends TextView {

    Context context;

    public ClickTextView(Context context) {
        super(context);
        this.context = context;
        setTextSize(14);
        setPadding(0, 5, 0, 5);
        //setBackgroundResource(R.drawable.view_selector);
        setTextColor(Color.parseColor("#2B3A50"));
    }

    public ClickTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ClickTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setReplyText(CircleFriendsModel1.CommentBean replyModel) {
        SpannableString spannableString;
        if (TextUtils.isEmpty(replyModel.getFriuser().getUserId())) {
            spannableString = new SpannableString(replyModel.getMyuser().getUserName() + "：" + replyModel.getContent());
            spannableString.setSpan(new CustomClickableSpan(context, replyModel, 1), 0, replyModel.getMyuser().getUserName().length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
            spannableString = new SpannableString(replyModel.getFriuser().getUserName() + " 回复 " + replyModel.getMyuser().getUserName() + "：" + replyModel.getContent());
            spannableString.setSpan(new CustomClickableSpan(context, replyModel, 2), 0, replyModel.getFriuser().getUserName().length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new CustomClickableSpan(context, replyModel, 1), replyModel.getFriuser().getUserName().length() + 4, replyModel.getFriuser().getUserName().length() + 4 + replyModel.getMyuser().getUserName().length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        setText(spannableString);
        setMovementMethod(CustomLinkMovementMethod.getInstance());
        setHighlightColor(Color.parseColor("#00000000"));
    }


    /**
     * 自定义ClickableSpan
     */
    public static class CustomClickableSpan extends ClickableSpan {

        Context context;
        int color;
        CircleFriendsModel1.CommentBean replyModel;
        int clicktype;

        public CustomClickableSpan(Context context, CircleFriendsModel1.CommentBean replyModel, int clicktype) {
            this.context = context;
            this.replyModel = replyModel;
            this.clicktype = clicktype;
            color = Color.parseColor("#5B6FCA");
        }

        @Override
        public void onClick(View widget) {
            switch (clicktype) {
                case 1://reply
                    ToastUtils.showToast(context, replyModel.getMyuser().getUserName());
                    break;
                case 2://reply to
                    ToastUtils.showToast(context, replyModel.getFriuser().getUserName());
                    break;
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(color);
            ds.setUnderlineText(false);
        }
    }

    public boolean isClick;//内部链接是否被点击

    @Override
    public boolean performClick() {
        if (isClick) {
            return true;
        }
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isClick = false;
       // setBackgroundResource(R.drawable.view_selector);
        return super.onTouchEvent(event);
    }

    /**
     * 自定义LinkMovementMethod
     */
    public static class CustomLinkMovementMethod extends LinkMovementMethod {

        static CustomLinkMovementMethod sInstance;

        @Override
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP ||
                    action == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
                if (link.length != 0) {
                    if (action == MotionEvent.ACTION_UP) {
                        link[0].onClick(widget);
                        buffer.setSpan(new BackgroundColorSpan(Color.parseColor("#00000000")), buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (action == MotionEvent.ACTION_DOWN) {
                        buffer.setSpan(new BackgroundColorSpan(Color.parseColor("#E0E0E0")), buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        Selection.setSelection(buffer,
                                buffer.getSpanStart(link[0]),
                                buffer.getSpanEnd(link[0]));
                    }

                    if (widget instanceof ClickTextView) {
                        ((ClickTextView) widget).isClick = true;
                        widget.setBackground(null);
                    }
                    return true;
                } else {
                    Selection.removeSelection(buffer);
                    super.onTouchEvent(widget, buffer, event);
                    return false;
                }
            }
            return Touch.onTouchEvent(widget, buffer, event);
        }

        public static CustomLinkMovementMethod getInstance() {
            if (sInstance == null) {
                sInstance = new CustomLinkMovementMethod();
            }
            return sInstance;
        }
    }

}