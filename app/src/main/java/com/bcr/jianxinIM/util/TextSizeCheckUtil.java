package com.bcr.jianxinIM.util;

import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;


import com.bcr.jianxinIM.listener.IEditTextsChangeListener;

import java.util.ArrayList;
import java.util.Arrays;

public class TextSizeCheckUtil  {
    public TextView button;
    private ArrayList<EditText> editTexts = new ArrayList<>();
    private ArrayList<DedindTextWatcher> watchers = new ArrayList<>();

    //回调
    IEditTextsChangeListener mChangeListener;

    private TextSizeCheckUtil() {
    }

    public static TextSizeCheckUtil getInstance() {
        return new TextSizeCheckUtil();
    }

    /**
     * 设置按钮变化的回调
     *
     * @param changeListener
     * @return
     */
    public TextSizeCheckUtil setChangeListener(IEditTextsChangeListener changeListener) {
        mChangeListener = changeListener;
        return this;
    }

    /**
     * 设置需要变化的按钮
     *
     * @param button
     * @return
     */
    public TextSizeCheckUtil setBtn(TextView button) {
        this.button = button;
        return this;
    }

    /**
     * 设置需要监听非空状态的editext
     *
     * @param editTexts
     * @return
     */
    public TextSizeCheckUtil addAllEditText(EditText... editTexts) {
        this.editTexts.addAll(Arrays.asList(editTexts));
        initEditListener();
        return this;
    }

    /**
     * 这里为每一个editext添加watcher
     */
    private void initEditListener() {
        Log.i("TAG", "调用了遍历editext的方法");
        for (int i = 0; i < editTexts.size(); i++) {
            EditText editText = editTexts.get(i);
            MyTextChange watcher = new MyTextChange();
            editText.addTextChangedListener(watcher);
            watchers.add(watcher);
        }
    }

    /**
     * 这里移除所有的watcher
     */
    public void removeWatcher() {
        if (editTexts != null && watchers != null && editTexts.size() == watchers.size()) {
            for (int i = 0; i < editTexts.size(); i++) {
                if (editTexts.get(i) != null && watchers.get(i) != null)
                    editTexts.get(i).removeTextChangedListener(watchers.get(i));
            }
        }
    }


    /**
     * edit输入的变化来改变按钮的是否点击
     */
    private class MyTextChange extends DedindTextWatcher {
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            changeButtonState(checkAllEdit());
        }

    }

    /**
     * 检查所有的edit是否输入了数据
     *
     * @return
     */
    public boolean checkAllEdit() {
        for (EditText editText : editTexts) {
            if (!TextUtils.isEmpty(editText.getText() + "")) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 更改状态
     *
     * @param hasContent
     */
    public void changeButtonState(boolean hasContent) {
        Log.i("TAG", "所有edittext" + (hasContent ? "有" : "没有") + "值了");
        if (mChangeListener != null)
            mChangeListener.textChange(hasContent);
        button.setEnabled(hasContent);
    }

}
