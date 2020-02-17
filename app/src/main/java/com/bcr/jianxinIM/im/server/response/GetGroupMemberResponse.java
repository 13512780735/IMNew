package com.bcr.jianxinIM.im.server.response;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AMing on 16/1/27.
 * Company RongCloud
 */
public class GetGroupMemberResponse implements Serializable {

    private static final long serialVersionUID = -3972802951229254770L;



    /**
     * Success : true
     * ResultData : [{"userId":"9571398998","UserNickName":"系统默认","UserPic":"http://i0.hdslb.com/bfs/article/123412d75583d1e5082c261086e633e390704a4e.jpg","Tel":"13300000001","City":null,"Sex":1,"RemarkTitle":"系统默认","JoinDate":"2019/11/25 20:40:54","role":1},{"userId":"6990588942","UserNickName":"疾风剑豪1","UserPic":"https://p0.ssl.qhimgs1.com/bdr/400__/t01475c8e781b6c6b60.jpg","Tel":"13823925879","City":"长沙","Sex":0,"RemarkTitle":"疾风剑豪1","JoinDate":"2019/11/25 20:40:54","role":0}]
     * Message : 查询群成员成功
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

    public static class ResultDataBean implements Serializable{
        private static final long serialVersionUID = -7540110593142872802L;
        /**
         * UserId : 9571398998
         * UserNickName : 系统默认
         * UserPic : http://i0.hdslb.com/bfs/article/123412d75583d1e5082c261086e633e390704a4e.jpg
         * Tel : 13300000001
         * City : null
         * Sex : 1
         * RemarkTitle : 系统默认
         * JoinDate : 2019/11/25 20:40:54
         * role : 1
         */

        private String UserId;
        private String UserNickName;
        private String UserPic;
        private String Tel;
        private Object City;
        private int Sex;
        private String RemarkTitle;
        private String JoinDate;
        private int role;

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

        public String getRemarkTitle() {
            return RemarkTitle;
        }

        public void setRemarkTitle(String RemarkTitle) {
            this.RemarkTitle = RemarkTitle;
        }

        public String getJoinDate() {
            return JoinDate;
        }

        public void setJoinDate(String JoinDate) {
            this.JoinDate = JoinDate;
        }

        public int getRole() {
            return role;
        }

        public void setRole(int role) {
            this.role = role;
        }
    }
}
