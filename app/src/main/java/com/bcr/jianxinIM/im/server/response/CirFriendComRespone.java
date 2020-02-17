package com.bcr.jianxinIM.im.server.response;

public class CirFriendComRespone {


    /**
     * Success : true
     * ResultData : {"ComId":"375a56eb-6b50-4ffb-92c3-04ed601a886b","userId":"1403066163","FriendId":"4612839777","UserNickName":"Tf","FriendName":"Log","ComDate":"2019/12/18 19:59:02","Content":"和234312"}
     * Message : 评论成功
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
         * ComId : 375a56eb-6b50-4ffb-92c3-04ed601a886b
         * UserId : 1403066163
         * FriendId : 4612839777
         * UserNickName : Tf
         * FriendName : Log
         * ComDate : 2019/12/18 19:59:02
         * Content : 和234312
         */

        private String ComId;
        private String UserId;
        private String FriendId;
        private String UserNickName;
        private String FriendName;
        private String ComDate;
        private String Content;
        private String UserPic;
        private String FriendPic;

        public String getUserPic() {
            return UserPic;
        }

        public void setUserPic(String userPic) {
            UserPic = userPic;
        }

        public String getFriendPic() {
            return FriendPic;
        }

        public void setFriendPic(String friendPic) {
            FriendPic = friendPic;
        }

        public String getComId() {
            return ComId;
        }

        public void setComId(String ComId) {
            this.ComId = ComId;
        }

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String UserId) {
            this.UserId = UserId;
        }

        public String getFriendId() {
            return FriendId;
        }

        public void setFriendId(String FriendId) {
            this.FriendId = FriendId;
        }

        public String getUserNickName() {
            return UserNickName;
        }

        public void setUserNickName(String UserNickName) {
            this.UserNickName = UserNickName;
        }

        public String getFriendName() {
            return FriendName;
        }

        public void setFriendName(String FriendName) {
            this.FriendName = FriendName;
        }

        public String getComDate() {
            return ComDate;
        }

        public void setComDate(String ComDate) {
            this.ComDate = ComDate;
        }

        public String getContent() {
            return Content;
        }

        public void setContent(String Content) {
            this.Content = Content;
        }
    }
}
