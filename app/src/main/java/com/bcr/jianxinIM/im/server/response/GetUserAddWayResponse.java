package com.bcr.jianxinIM.im.server.response;

public class GetUserAddWayResponse {

    /**
     * Success : true
     * ResultData : {"Id":"6275769783","Tel":1,"userId":1,"Code":1,"Group":1}
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
         * Id : 6275769783
         * Tel : 1
         * UserId : 1
         * Code : 1
         * Group : 1
         */

        private String Id;
        private int Tel;
        private int UserId;
        private int Code;
        private int Group;

        public String getId() {
            return Id;
        }

        public void setId(String Id) {
            this.Id = Id;
        }

        public int getTel() {
            return Tel;
        }

        public void setTel(int Tel) {
            this.Tel = Tel;
        }

        public int getUserId() {
            return UserId;
        }

        public void setUserId(int UserId) {
            this.UserId = UserId;
        }

        public int getCode() {
            return Code;
        }

        public void setCode(int Code) {
            this.Code = Code;
        }

        public int getGroup() {
            return Group;
        }

        public void setGroup(int Group) {
            this.Group = Group;
        }
    }
}
