package com.bcr.jianxinIM.im.server.response;

/**
 * Created by AMing on 16/3/4.
 * Company RongCloud
 */
public class AddToBlackListResponse {

    /**
     * Success : true
     * ResultData : null
     * Message : 加入黑名单成功
     * ResultType : 0
     */

    private boolean Success;
    private Object ResultData;
    private String Message;
    private int ResultType;

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean Success) {
        this.Success = Success;
    }

    public Object getResultData() {
        return ResultData;
    }

    public void setResultData(Object ResultData) {
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
