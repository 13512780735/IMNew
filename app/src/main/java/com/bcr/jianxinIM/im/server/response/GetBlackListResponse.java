package com.bcr.jianxinIM.im.server.response;

import java.util.List;

/**
 * Created by AMing on 16/3/4.
 * Company RongCloud
 */
public class GetBlackListResponse {


    /**
     * Success : true
     * ResultData : [{"userId":"2828368772","UserNickName":"系统默认","UserPic":"https://p0.ssl.qhimgs1.com/bdr/400__/t01475c8e781b6c6b60.jpg","Tel":"13300000003","City":null,"Sex":1,"UpdateDate":"2019/11/22 13:47:17"},{"userId":"6990588942","UserNickName":"疾风剑豪1","UserPic":"https://p0.ssl.qhimgs1.com/bdr/400__/t01475c8e781b6c6b60.jpg","Tel":"13823925879","City":"长沙","Sex":0,"UpdateDate":"2019/11/19 18:08:46"}]
     * Message : 获取黑名单列表成功
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
         * UserId : 2828368772
         * UserNickName : 系统默认
         * UserPic : https://p0.ssl.qhimgs1.com/bdr/400__/t01475c8e781b6c6b60.jpg
         * Tel : 13300000003
         * City : null
         * Sex : 1
         * UpdateDate : 2019/11/22 13:47:17
         */

        private String UserId;
        private String UserNickName;
        private String UserPic;
        private String Tel;
        private Object City;
        private int Sex;
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

        public Object getCity() {
            return City;
        }

        public void setCity(Object City) {
            this.City = City;
        }

        public int getSex() {
            return Sex;
        }

        public void setSex(int Sex) {
            this.Sex = Sex;
        }

        public String getUpdateDate() {
            return UpdateDate;
        }

        public void setUpdateDate(String UpdateDate) {
            this.UpdateDate = UpdateDate;
        }
    }
}
