package com.bcr.jianxinIM.im.server.response;

/**
 * Created by AMing on 16/1/4.
 * Company RongCloud
 */
public class GetUserInfoByIdResponse {


    /**
     * Success : true
     * ResultData : {"userId":"6990588942","Id":"f3cca6d2-bf5d-4c2a-990e-e37c055d827f","UserNickName":"系统默认","UserPic":"https://p0.ssl.qhimgs1.com/bdr/400__/t01475c8e781b6c6b60.jpg","Sex":null,"City":null,"Tel":"13823925879","Rtoken":"ra1s+qe0SR3ezZi4jr1qVj9sw8nWg0ccOyVwpFo/7XhTNINrSa6i7YqBdhE7UsvZH9++E5XWFvJ0oiX7f/JXNWKQwZSdcEN8"}
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
         * userId : 6990588942
         * Id : f3cca6d2-bf5d-4c2a-990e-e37c055d827f
         * UserNickName : 系统默认
         * UserPic : https://p0.ssl.qhimgs1.com/bdr/400__/t01475c8e781b6c6b60.jpg
         * Sex : null
         * City : null
         * Tel : 13823925879
         * Rtoken : ra1s+qe0SR3ezZi4jr1qVj9sw8nWg0ccOyVwpFo/7XhTNINrSa6i7YqBdhE7UsvZH9++E5XWFvJ0oiX7f/JXNWKQwZSdcEN8
         */

        private String userId;
        private String Id;
        private String UserNickName;
        private String UserPic;
        private int Sex;
        private String City;
        private String Tel;
        private String Rtoken;
        private String Encode;

        public String getEncode() {
            return Encode;
        }

        public void setEncode(String encode) {
            Encode = encode;
        }

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

        public String getRtoken() {
            return Rtoken;
        }

        public void setRtoken(String Rtoken) {
            this.Rtoken = Rtoken;
        }
    }
}
