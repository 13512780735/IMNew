package com.bcr.jianxinIM.im.server.response;

public class UserInfoRespone {

    /**
     * Success : true
     * ResultData : {"userId":"1080832006","NickName":"郑佳","UserPic":"http://192.168.3.35:9992/Ashx/image/20191210175711NjJa.jpg","CirFriendPic":""}
     * Message : 获取用户详细信息成功
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
         * UserId : 1080832006
         * NickName : 郑佳
         * UserPic : http://192.168.3.35:9992/Ashx/image/20191210175711NjJa.jpg
         * CirFriendPic :
         */

        private String UserId;
        private String NickName;
        private String UserPic;
        private String CirFriendPic;
        private String Remark;

        public String getRemark() {
            return Remark;
        }

        public void setRemark(String remark) {
            Remark = remark;
        }

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String UserId) {
            this.UserId = UserId;
        }

        public String getNickName() {
            return NickName;
        }

        public void setNickName(String NickName) {
            this.NickName = NickName;
        }

        public String getUserPic() {
            return UserPic;
        }

        public void setUserPic(String UserPic) {
            this.UserPic = UserPic;
        }

        public String getCirFriendPic() {
            return CirFriendPic;
        }

        public void setCirFriendPic(String CirFriendPic) {
            this.CirFriendPic = CirFriendPic;
        }
    }
}
