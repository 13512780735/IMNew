package com.bcr.jianxinIM.im.server.response;

/**
 * Created by AMing on 15/12/24.
 * Company RongCloud
 */
public class GetTokenResponse {


    /**
     * Success : true
     * ResultData : {"userId":"6990588942","Rtoken":"ra1s+qe0SR3ezZi4jr1qVj9sw8nWg0ccOyVwpFo/7XhTNINrSa6i7YqBdhE7UsvZH9++E5XWFvJ0oiX7f/JXNWKQwZSdcEN8"}
     * Message : 返回成功
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
         * Rtoken : ra1s+qe0SR3ezZi4jr1qVj9sw8nWg0ccOyVwpFo/7XhTNINrSa6i7YqBdhE7UsvZH9++E5XWFvJ0oiX7f/JXNWKQwZSdcEN8
         */

        private String userId;
        private String Rtoken;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getRtoken() {
            return Rtoken;
        }

        public void setRtoken(String Rtoken) {
            this.Rtoken = Rtoken;
        }
    }
}
