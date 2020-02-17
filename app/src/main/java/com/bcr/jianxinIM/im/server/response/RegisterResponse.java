package com.bcr.jianxinIM.im.server.response;


/**
 * Created by AMing on 15/12/23.
 * Company RongCloud
 */
public class RegisterResponse  {


    /**
     * Success : true
     * ResultData : {"userId":"9569352135","Id":"6fa7fc2d-c0b3-466c-bf6d-b150bfe219c5","UserNickName":"系统默认","UserPic":"https://p0.ssl.qhimgs1.com/bdr/400__/t01475c8e781b6c6b60.jpg","Sex":null,"City":null,"Tel":null,"Rtoken":"552bRePomufA9N7cAKYIoT9sw8nWg0ccOyVwpFo/7XhTNINrSa6i7eIHGJkYrfAWPN7AjE/qdRbuLo5ghDUnYNMplLqjc8Th"}
     * Message : 注册成功
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
         * userId : 9569352135
         * Id : 6fa7fc2d-c0b3-466c-bf6d-b150bfe219c5
         * UserNickName : 系统默认
         * UserPic : https://p0.ssl.qhimgs1.com/bdr/400__/t01475c8e781b6c6b60.jpg
         * Sex : null
         * City : null
         * Tel : null
         * Rtoken : 552bRePomufA9N7cAKYIoT9sw8nWg0ccOyVwpFo/7XhTNINrSa6i7eIHGJkYrfAWPN7AjE/qdRbuLo5ghDUnYNMplLqjc8Th
         */

        private String userId;
        private String Id;
        private String UserNickName;
        private String UserPic;
        private Object Sex;
        private Object City;
        private Object Tel;
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

        public Object getSex() {
            return Sex;
        }

        public void setSex(Object Sex) {
            this.Sex = Sex;
        }

        public Object getCity() {
            return City;
        }

        public void setCity(Object City) {
            this.City = City;
        }

        public Object getTel() {
            return Tel;
        }

        public void setTel(Object Tel) {
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
