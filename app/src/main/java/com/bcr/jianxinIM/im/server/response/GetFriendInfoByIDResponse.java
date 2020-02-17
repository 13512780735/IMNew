package com.bcr.jianxinIM.im.server.response;

/**
 * Created by wangmingqiang on 16/9/11.
 * Company RongCloud
 */

public class GetFriendInfoByIDResponse {


    /**
     * Success : true
     * ResultData : {"FriendId":"9571398998","UserNickName":"系统默认","UserPic":"http://i0.hdslb.com/bfs/article/123412d75583d1e5082c261086e633e390704a4e.jpg","Tel":"13300000001","City":null,"Sex":null,"Remark":"疾风剑豪2","Message":"您好","Status":20,"UpdateDate":"2019/11/20 18:34:00"}
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
         * FriendId : 9571398998
         * UserNickName : 系统默认
         * UserPic : http://i0.hdslb.com/bfs/article/123412d75583d1e5082c261086e633e390704a4e.jpg
         * Tel : 13300000001
         * City : null
         * Sex : null
         * Remark : 疾风剑豪2
         * Message : 您好
         * Status : 20
         * UpdateDate : 2019/11/20 18:34:00
         */

        private String FriendId;
        private String UserNickName;
        private String UserPic;
        private String Tel;
        private Object City;
        private Object Sex;
        private String Remark;
        private String Message;
        private int Status;
        private String UpdateDate;

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

        public String getUserPic() {
            return UserPic;
        }

        public void setUserPic(String UserPic) {
            this.UserPic = UserPic;
        }

        public String getTel() {
            return Tel;
        }

        public void setTel(String Tel) {
            this.Tel = Tel;
        }

        public Object getCity() {
            return City;
        }

        public void setCity(Object City) {
            this.City = City;
        }

        public Object getSex() {
            return Sex;
        }

        public void setSex(Object Sex) {
            this.Sex = Sex;
        }

        public String getRemark() {
            return Remark;
        }

        public void setRemark(String Remark) {
            this.Remark = Remark;
        }

        public String getMessage() {
            return Message;
        }

        public void setMessage(String Message) {
            this.Message = Message;
        }

        public int getStatus() {
            return Status;
        }

        public void setStatus(int Status) {
            this.Status = Status;
        }

        public String getUpdateDate() {
            return UpdateDate;
        }

        public void setUpdateDate(String UpdateDate) {
            this.UpdateDate = UpdateDate;
        }
    }
}
