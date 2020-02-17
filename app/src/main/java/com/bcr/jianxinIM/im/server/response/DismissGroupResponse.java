package com.bcr.jianxinIM.im.server.response;

/**
 * Created by AMing on 16/1/29.
 * Company RongCloud
 */
public class DismissGroupResponse {

    /**
     * Success : true
     * ResultData : 6485441925
     * Message : 解散群组成功
     * ResultType : 0
     */

    private boolean Success;
    private String ResultData;
    private String Message;
    private int ResultType;

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean Success) {
        this.Success = Success;
    }

    public String getResultData() {
        return ResultData;
    }

    public void setResultData(String ResultData) {
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
}
