package com.bcr.jianxinIM.im.server.response;

/**
 * Created by AMing on 16/2/2.
 * Company RongCloud
 */
public class SetGroupDisplayNameResponse {

    /**
     * Success : true
     * ResultData : 2449467959
     * Message : 修改成功
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
