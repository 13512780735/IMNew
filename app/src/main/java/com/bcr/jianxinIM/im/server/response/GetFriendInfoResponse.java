package com.bcr.jianxinIM.im.server.response;

public class GetFriendInfoResponse {

    /**
     * Success : true
     * ResultData : {"userId":"6275769783","Id":"34ef3235-7fb3-4ee8-bcea-1b627c1a9c00","UserNickName":"测试3","UserPic":"http://192.168.3.35:9992/Ashx/image/20200108112728bSus.png","Sex":0,"City":"重庆","Tel":"18000000003","Encode":"D75rdC8y"}
     * Message : 获取成功
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
         * userId : 6275769783
         * Id : 34ef3235-7fb3-4ee8-bcea-1b627c1a9c00
         * UserNickName : 测试3
         * UserPic : http://192.168.3.35:9992/Ashx/image/20200108112728bSus.png
         * Sex : 0
         * City : 重庆
         * Tel : 18000000003
         * Encode : D75rdC8y
         */

        private String userId;
        private String Id;
        private String UserNickName;
        private String UserPic;
        private int Sex;
        private String City;
        private String Tel;
        private String Encode;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getId() {
            return Id;
        }

        public void setId(String Id) {
            this.Id = Id;
        }

        public String getUserNickName() {
            return UserNickName;
        }

        public void setUserNickName(String UserNickName) {
            this.UserNickName = UserNickName;
        }

        public String getUserPic() {
            return UserPic;
        }

        public void setUserPic(String UserPic) {
            this.UserPic = UserPic;
        }

        public int getSex() {
            return Sex;
        }

        public void setSex(int Sex) {
            this.Sex = Sex;
        }

        public String getCity() {
            return City;
        }

        public void setCity(String City) {
            this.City = City;
        }

        public String getTel() {
            return Tel;
        }

        public void setTel(String Tel) {
            this.Tel = Tel;
        }

        public String getEncode() {
            return Encode;
        }

        public void setEncode(String Encode) {
            this.Encode = Encode;
        }
    }
}
