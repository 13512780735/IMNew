package com.bcr.jianxinIM.im.server.response;

public class PerfectUserInfoResponse {

    /**
     * Success : true
     * ResultData : {"userId":"6990588942","Id":"f3cca6d2-bf5d-4c2a-990e-e37c055d827f","UserNickName":"疾风剑豪1","UserPic":"ra1s qe0SR3ezZi4jr1qVj9sw8nWg0ccOyVwpFo/7XhTNINrSa6i7YqBdhE7UsvZH9  E5XWFvJ0oiX7f/JXNWKQwZSdcEN8","Sex":1,"City":"长沙","Tel":"长沙","Rtoken":"ra1s+qe0SR3ezZi4jr1qVj9sw8nWg0ccOyVwpFo/7XhTNINrSa6i7YqBdhE7UsvZH9++E5XWFvJ0oiX7f/JXNWKQwZSdcEN8"}
     * Message : 完善成功
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
         * UserNickName : 疾风剑豪1
         * UserPic : ra1s qe0SR3ezZi4jr1qVj9sw8nWg0ccOyVwpFo/7XhTNINrSa6i7YqBdhE7UsvZH9  E5XWFvJ0oiX7f/JXNWKQwZSdcEN8
         * Sex : 1
         * City : 长沙
         * Tel : 长沙
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
