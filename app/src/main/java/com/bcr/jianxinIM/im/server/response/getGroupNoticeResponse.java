package com.bcr.jianxinIM.im.server.response;

import java.util.List;

public class getGroupNoticeResponse {

    /**
     * Success : true
     * ResultData : [{"userId":"8134869597","UserPic":"https://p0.ssl.qhimgs1.com/bdr/400__/t01475c8e781b6c6b60.jpg","GroupId":"07412904427","UserNickName":"321","NickName":"xxx","Message":null,"State":20},{"userId":"8981459858","UserPic":"http://192.168.3.35:9992/Ashx/image/20191129101201eQUl.jpg","GroupId":"41655180698","UserNickName":"hxhhx","NickName":"公共号","Message":null,"State":20},{"userId":"8407370815","UserPic":"https://p0.ssl.qhimgs1.com/bdr/400__/t01475c8e781b6c6b60.jpg","GroupId":"07412904427","UserNickName":"风格","NickName":"xxx","Message":"风格申请加入xxx","State":10},{"userId":"3120014924","UserPic":"https://p0.ssl.qhimgs1.com/bdr/400__/t01475c8e781b6c6b60.jpg","GroupId":"07412904427","UserNickName":"yhgbj","NickName":"xxx","Message":null,"State":20},{"userId":"3120014924","UserPic":"https://p0.ssl.qhimgs1.com/bdr/400__/t01475c8e781b6c6b60.jpg","GroupId":"41655180698","UserNickName":"yhgbj","NickName":"公共号","Message":null,"State":20}]
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

    public static class ResultDataBean {
        /**
         * UserId : 8134869597
         * UserPic : https://p0.ssl.qhimgs1.com/bdr/400__/t01475c8e781b6c6b60.jpg
         * GroupId : 07412904427
         * UserNickName : 321
         * NickName : xxx
         * Message : null
         * State : 20
         */

        private String UserId;
        private String UserPic;
        private String GroupId;
        private String UserNickName;
        private String NickName;
        private String Message;
        private int State;

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String UserId) {
            this.UserId = UserId;
        }

        public String getUserPic() {
            return UserPic;
        }

        public void setUserPic(String UserPic) {
            this.UserPic = UserPic;
        }

        public String getGroupId() {
            return GroupId;
        }

        public void setGroupId(String GroupId) {
            this.GroupId = GroupId;
        }

        public String getUserNickName() {
            return UserNickName;
        }

        public void setUserNickName(String UserNickName) {
            this.UserNickName = UserNickName;
        }

        public String getNickName() {
            return NickName;
        }

        public void setNickName(String NickName) {
            this.NickName = NickName;
        }

        public String getMessage() {
            return Message;
        }

        public void setMessage(String Message) {
            this.Message = Message;
        }

        public int getState() {
            return State;
        }

        public void setState(int State) {
            this.State = State;
        }
    }
}
