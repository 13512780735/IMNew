package com.bcr.jianxinIM.im.server.response;

/**
 * Created by AMing on 16/1/26.
 * Company RongCloud
 */
public class GetGroupInfoResponse {


    /**
     * Success : true
     * ResultData : {"id":"7622870696","name":"比较纠结","portraitUri":"https://p0.ssl.qhimgs1.com/bdr/400__/t01475c8e781b6c6b60.jpg","memberCount":2,"creatorId":"6990588942","bulletin":"","bulletinTime":null,"deletedAt":null,"isMute":1}
     * Message : 获取群详细信息成功
     * ResultType : 0
     */

    private boolean Success;
    private ResultDataBean ResultData;
    private String Message;
    private int ResultType;

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean Success) {
        this.Success = Success;
    }

    public ResultDataBean getResultData() {
        return ResultData;
    }

    public void setResultData(ResultDataBean ResultData) {
        this.ResultData = ResultData;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public int getResultType() {
        return ResultType;
    }

    public void setResultType(int ResultType) {
        this.ResultType = ResultType;
    }

    public static class ResultDataBean {
        /**
         * id : 7622870696
         * name : 比较纠结
         * portraitUri : https://p0.ssl.qhimgs1.com/bdr/400__/t01475c8e781b6c6b60.jpg
         * memberCount : 2
         * creatorId : 6990588942
         * bulletin :
         * bulletinTime : null
         * deletedAt : null
         * isMute : 1
         *
         */

        private String id;
        private String name;
        private String portraitUri;
        private int memberCount;
        private String creatorId;
        private String bulletin;
        private Object bulletinTime;
        private Object deletedAt;
        private int isMute;
        private String Encode;
        private int isAddFriend;

        public int getIsAddFriend() {
            return isAddFriend;
        }

        public void setIsAddFriend(int isAddFriend) {
            this.isAddFriend = isAddFriend;
        }

        public String getEncode() {
            return Encode;
        }

        public void setEncode(String encode) {
            Encode = encode;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPortraitUri() {
            return portraitUri;
        }

        public void setPortraitUri(String portraitUri) {
            this.portraitUri = portraitUri;
        }

        public int getMemberCount() {
            return memberCount;
        }

        public void setMemberCount(int memberCount) {
            this.memberCount = memberCount;
        }

        public String getCreatorId() {
            return creatorId;
        }

        public void setCreatorId(String creatorId) {
            this.creatorId = creatorId;
        }

        public String getBulletin() {
            return bulletin;
        }

        public void setBulletin(String bulletin) {
            this.bulletin = bulletin;
        }

        public Object getBulletinTime() {
            return bulletinTime;
        }

        public void setBulletinTime(Object bulletinTime) {
            this.bulletinTime = bulletinTime;
        }

        public Object getDeletedAt() {
            return deletedAt;
        }

        public void setDeletedAt(Object deletedAt) {
            this.deletedAt = deletedAt;
        }

        public int getIsMute() {
            return isMute;
        }

        public void setIsMute(int isMute) {
            this.isMute = isMute;
        }
    }
}
