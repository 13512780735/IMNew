package com.bcr.jianxinIM.im.server.response;

import java.util.List;

/**
 * Created by AMing on 16/1/26.
 * Company RongCloud
 */
public class GetGroupResponse {


    /**
     * Success : true
     * ResultData : [{"role":0,"GroupId":"8441176290","NickName":"885","creatorId":"6990588942","Picture":"https://p0.ssl.qhimgs1.com/bdr/400__/t01475c8e781b6c6b60.jpg","isMute":0,"certiStatus":0,"memberCount":2}]
     * Message : 获取群组成功
     * ResultType : 0
     */

    private boolean Success;
    private String Message;
    private int ResultType;
    private List<ResultDataBean> ResultData;

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean Success) {
        this.Success = Success;
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

    public List<ResultDataBean> getResultData() {
        return ResultData;
    }

    public void setResultData(List<ResultDataBean> ResultData) {
        this.ResultData = ResultData;
    }

    public static class ResultDataBean {
        /**
         * role : 0
         * GroupId : 8441176290
         * NickName : 885
         * creatorId : 6990588942
         * Picture : https://p0.ssl.qhimgs1.com/bdr/400__/t01475c8e781b6c6b60.jpg
         * isMute : 0
         * certiStatus : 0
         * memberCount : 2
         */

        private int role;
        private String GroupId;
        private String NickName;
        private String creatorId;
        private String Picture;
        private int isMute;
        private int certiStatus;
        private int memberCount;

        public int getRole() {
            return role;
        }

        public void setRole(int role) {
            this.role = role;
        }

        public String getGroupId() {
            return GroupId;
        }

        public void setGroupId(String GroupId) {
            this.GroupId = GroupId;
        }

        public String getNickName() {
            return NickName;
        }

        public void setNickName(String NickName) {
            this.NickName = NickName;
        }

        public String getCreatorId() {
            return creatorId;
        }

        public void setCreatorId(String creatorId) {
            this.creatorId = creatorId;
        }

        public String getPicture() {
            return Picture;
        }

        public void setPicture(String Picture) {
            this.Picture = Picture;
        }

        public int getIsMute() {
            return isMute;
        }

        public void setIsMute(int isMute) {
            this.isMute = isMute;
        }

        public int getCertiStatus() {
            return certiStatus;
        }

        public void setCertiStatus(int certiStatus) {
            this.certiStatus = certiStatus;
        }

        public int getMemberCount() {
            return memberCount;
        }

        public void setMemberCount(int memberCount) {
            this.memberCount = memberCount;
        }
    }
}
