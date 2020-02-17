package com.bcr.jianxinIM.im.server.response;


/**
 * Created by AMing on 15/12/24.
 * Company RongCloud
 */
public class RestPasswordResponse {


    /**
     * Success : true
     * ResultData : {"userId":"5155096233","Id":"fd5a17a3-5511-410a-a34a-438a0da5dbc1","UserNickName":"sss","UserPic":"https://p0.ssl.qhimgs1.com/bdr/400__/t01475c8e781b6c6b60.jpg","Sex":0,"City":"广州","Tel":"广州","Rtoken":"KrzdZmTfjvbhvv5ph2JSleGsNkveIaM3CD3W7rExuUG0AR1XaaWb+8ADEcAAnMhEs3m/RxLmL+w8aGhQPPC9dyMiCWzbfUJr"}
     * Message : 用户修改成功
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
         * userId : 5155096233
         * Id : fd5a17a3-5511-410a-a34a-438a0da5dbc1
         * UserNickName : sss
         * UserPic : https://p0.ssl.qhimgs1.com/bdr/400__/t01475c8e781b6c6b60.jpg
         * Sex : 0
         * City : 广州
         * Tel : 广州
         * Rtoken : KrzdZmTfjvbhvv5ph2JSleGsNkveIaM3CD3W7rExuUG0AR1XaaWb+8ADEcAAnMhEs3m/RxLmL+w8aGhQPPC9dyMiCWzbfUJr
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
