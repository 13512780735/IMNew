package com.bcr.jianxinIM.im.server.response;


/**
 * Created by AMing on 15/12/23.
 * Company RongCloud
 */
public class SendCodeResponse {


    /**
     * Success : true
     * ResultData : 180
     * Message : null
     * ResultType : 0
     */

    private boolean Success;
    private int ResultData;
    private String Message;
    private int ResultType;

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean Success) {
        this.Success = Success;
    }

    public int getResultData() {
        return ResultData;
    }

    public void setResultData(int ResultData) {
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
