package com.bcr.jianxinIM.im.server.response;


import java.util.List;

/**
 * Created by AMing on 16/1/7.
 * Company RongCloud
 */
public class UserRelationshipResponse {


    /**
     * Success : true
     * ResultData : [{"userId":"9571398998","UserNickName":"系统默认","UserPic":"https://p0.ssl.qhimgs1.com/bdr/400__/t01475c8e781b6c6b60.jpg","Tel":"13300000001","City":null,"Sex":null,"Remark":"疾风剑豪2","Message":"您好","Status":20,"UpdateDate":"2019/11/20 18:34:00"},{"userId":"6990588942","UserNickName":"疾风剑豪1","UserPic":"https://p0.ssl.qhimgs1.com/bdr/400__/t01475c8e781b6c6b60.jpg","Tel":"13823925879","City":"长沙","Sex":0,"Remark":"疾风剑豪","Message":"您好1","Status":20,"UpdateDate":"2019/11/20 18:35:22"}]
     * Message : 获取成功
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

    public static class ResultDataBean implements Comparable {

        public ResultDataBean(String userId, String userNickName, String userPic, String tel, String city, int sex, String remark, String message, int status, String updateDate) {
            this.UserId = userId;
            this.UserNickName = userNickName;
            this.UserPic = userPic;
            this.Tel = tel;
            this.City = city;
            this.Sex = sex;
            this.Remark = remark;
            this.Message = message;
            this.Status = status;
            this.UpdateDate = updateDate;
        }
        public  ResultDataBean(){}

        /**
         * UserId : 9571398998
         * UserNickName : 系统默认
         * UserPic : https://p0.ssl.qhimgs1.com/bdr/400__/t01475c8e781b6c6b60.jpg
         * Tel : 13300000001
         * City : null
         * Sex : null
         * Remark : 疾风剑豪2
         * Message : 您好
         * Status : 20
         * UpdateDate : 2019/11/20 18:34:00
         */

        private String UserId;
        private String UserNickName;
        private String UserPic;
        private String Tel;
        private String  City;
        private int Sex;
        private String Remark;
        private String Message;
        private int Status;
        private String UpdateDate;

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String UserId) {
            this.UserId = UserId;
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

        public String getCity() {
            return City;
        }

        public void setCity(String City) {
            this.City = City;
        }

        public int getSex() {
            return Sex;
        }

        public void setSex(int Sex) {
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

        @Override
        public int compareTo(Object another) {
            return 0;
        }
    }
}
