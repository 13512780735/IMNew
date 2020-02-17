package com.bcr.jianxinIM.event;

import com.bcr.jianxinIM.model.CircleFriendsModel1;
import com.bcr.jianxinIM.view.CommentsView;

public class EventBusCarrier {
    int position;
    CircleFriendsModel1.CommentBean commentBean;
    int type;
    CircleFriendsModel1 circleFriendsModel1;
    CommentsView view;

    public CommentsView getView() {
        return view;
    }

    public void setView(CommentsView view) {
        this.view = view;
    }

    public CircleFriendsModel1 getCircleFriendsModel1() {
        return circleFriendsModel1;
    }

    public void setCircleFriendsModel1(CircleFriendsModel1 circleFriendsModel1) {
        this.circleFriendsModel1 = circleFriendsModel1;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public CircleFriendsModel1.CommentBean getCommentBean() {
        return commentBean;
    }

    public void setCommentBean(CircleFriendsModel1.CommentBean commentBean) {
        this.commentBean = commentBean;
    }
}
