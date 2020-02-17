package com.bcr.jianxinIM.im.server.response;

public class CollectVideoRespone {

    /**
     * Success : true
     * ResultData : null
     * Message : 收藏成功
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
