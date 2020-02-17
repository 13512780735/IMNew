package com.bcr.jianxinIM.model;

import java.util.ArrayList;
import java.util.List;

public class CircleFriendsModel {
    private int avatarUrl;
    private String name;
    private String content;
    private String address;
    private String time;
    private String message;
    private String likes;
    private boolean islike;
    private String likesNumber;
    private List<MessageBean> messageBean;

    public int getAvatarUrl() {
        return avatarUrl;
    }

    public boolean isIslike() {
        return islike;
    }

    public void setIslike(boolean islike) {
        this.islike = islike;
    }

    public void setAvatarUrl(int avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getLikesNumber() {
        return likesNumber;
    }

    public void setLikesNumber(String likesNumber) {
        this.likesNumber = likesNumber;
    }

    public List<MessageBean> getMessageBean() {
        return messageBean;
    }

    public void setMessageBean(List<MessageBean> messageBean) {
        this.messageBean = messageBean;
    }


    public static class MessageBean{
        private String name;
        private String content;
        private String flag;

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

        public boolean isShowAll = false;
        public List<String> images = new ArrayList<>();

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }
}
