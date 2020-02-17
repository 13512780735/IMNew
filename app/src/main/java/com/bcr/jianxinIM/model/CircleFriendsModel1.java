package com.bcr.jianxinIM.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CircleFriendsModel1 implements Serializable{

    /**
     * ID : a5d2f40c-b974-4ad2-b628-20d35a202a3c
     * UserId : 4612839777
     * UserPic : http://192.168.3.35:9992/Ashx/image/20191210183350xSdq.jpg
     * UserNickName : Log
     * ReleaseDate : 2019/12/13 18:02:14
     * Content : 111
     * Location :
     * LinksCount : 0
     * Links : []
     * LikeitCount : 2
     * Likeit : [{"userId":"1080832006","UserPic":"http://192.168.3.35:9992/Ashx/image/20191210175711NjJa.jpg","UserNickName":"郑佳"},{"userId":"1403066163","UserPic":"http://192.168.3.35:9992/Ashx/image/20191210115319PdDp.jpg","UserNickName":"Tf"}]
     * CommentCount : 2
     * Comment : [{"userId":"1403066163","FriendId":"","UserNickName":"Tf","FriendName":"","ComDate":"2019/12/16 13:19:34","Content":"和234312"},{"userId":"1403066163","FriendId":"","UserNickName":"Tf","FriendName":"","ComDate":"2019/12/16 13:19:31","Content":"和2341231"}]
     */

    private String ID;
    private String UserId;
    private String UserPic;
    private String UserNickName;
    private String ReleaseDate;
    private String Content;
    private String Location;
    private String  IsLikeit;
    private String  Remark;
    private int LinksCount;
    private int LikeitCount;
    private int CommentCount;
    public List<String> Links=new ArrayList<>();
    private List<LikeitBean> Likeit;
    private List<CommentBean> Comment;

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getIsLikeit() {
        return IsLikeit;
    }

    public void setIsLikeit(String isLikeit) {
        IsLikeit = isLikeit;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    public String getUserPic() {
        return UserPic;
    }

    public void setUserPic(String UserPic) {
        this.UserPic = UserPic;
    }

    public String getUserNickName() {
        return UserNickName;
    }

    public void setUserNickName(String UserNickName) {
        this.UserNickName = UserNickName;
    }

    public String getReleaseDate() {
        return ReleaseDate;
    }

    public void setReleaseDate(String ReleaseDate) {
        this.ReleaseDate = ReleaseDate;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String Content) {
        this.Content = Content;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String Location) {
        this.Location = Location;
    }

    public int getLinksCount() {
        return LinksCount;
    }

    public void setLinksCount(int LinksCount) {
        this.LinksCount = LinksCount;
    }

    public int getLikeitCount() {
        return LikeitCount;
    }

    public void setLikeitCount(int LikeitCount) {
        this.LikeitCount = LikeitCount;
    }

    public int getCommentCount() {
        return CommentCount;
    }

    public void setCommentCount(int CommentCount) {
        this.CommentCount = CommentCount;
    }
    public boolean isShowAll = false;
    public List<String> getLinks() {
        return Links;
    }

    public void setLinks(List<String> Links) {
        this.Links = Links;
    }
    public List<LikeitBean> getLikeit() {
        return Likeit;
    }

    public void setLikeit(List<LikeitBean> Likeit) {
        this.Likeit = Likeit;
    }

    public List<CommentBean> getComment() {
        return Comment;
    }

    public void setComment(List<CommentBean> Comment) {
        this.Comment = Comment;
    }

    public static class LikeitBean implements Serializable{
        /**
         * UserId : 1080832006
         * UserPic : http://192.168.3.35:9992/Ashx/image/20191210175711NjJa.jpg
         * UserNickName : 郑佳
         */

        private String UserId;
        private String UserPic;
        private String UserNickName;

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String UserId) {
            this.UserId = UserId;
        }

        public String getUserPic() {
            return UserPic;
        }

        public void setUserPic(String UserPic) {
            this.UserPic = UserPic;
        }

        public String getUserNickName() {
            return UserNickName;
        }

        public void setUserNickName(String UserNickName) {
            this.UserNickName = UserNickName;
        }
    }

    public static class CommentBean implements Serializable {
        /**
         * UserId : 1403066163
         * FriendId :
         * UserNickName : Tf
         * FriendName :
         * ComDate : 2019/12/16 13:19:34
         * Content : 和234312
         */

//        private String UserId;
//        private String FriendId;
//        private String UserNickName;
//        private String FriendName;
        private String ComDate;
        private String Content;
        private String ComId;

        private ComUserBean Myuser;
        private ComUserBean Friuser;

        public String getComId() {
            return ComId;
        }

        public void setComId(String comId) {
            ComId = comId;
        }

        public String getComDate() {
            return ComDate;
        }

        public void setComDate(String comDate) {
            ComDate = comDate;
        }

        public String getContent() {
            return Content;
        }

        public void setContent(String content) {
            Content = content;
        }

        public ComUserBean getMyuser() {
            return Myuser;
        }

        public void setMyuser(ComUserBean myuser) {
            Myuser = myuser;
        }

        public ComUserBean getFriuser() {
            return Friuser;
        }

        public void setFriuser(ComUserBean friuser) {
            Friuser = friuser;
        }
        //        public String getUserId() {
//            return UserId;
//        }
//
//        public void setUserId(String UserId) {
//            this.UserId = UserId;
//        }
//
//        public String getFriendId() {
//            return FriendId;
//        }
//
//        public void setFriendId(String FriendId) {
//            this.FriendId = FriendId;
//        }
//
//        public String getUserNickName() {
//            return UserNickName;
//        }
//
//        public void setUserNickName(String UserNickName) {
//            this.UserNickName = UserNickName;
//        }
//
//        public String getFriendName() {
//            return FriendName;
//        }
//
//        public void setFriendName(String FriendName) {
//            this.FriendName = FriendName;
//        }
//
//        public String getComDate() {
//            return ComDate;
//        }
//
//        public void setComDate(String ComDate) {
//            this.ComDate = ComDate;
//        }
//
//        public String getContent() {
//            return Content;
//        }
//
//        public void setContent(String Content) {
//            this.Content = Content;
//        }
    }
}
