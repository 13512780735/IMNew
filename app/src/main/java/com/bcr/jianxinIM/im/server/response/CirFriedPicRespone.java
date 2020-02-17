package com.bcr.jianxinIM.im.server.response;

public class CirFriedPicRespone {

    /**
     * Success : true
     * ResultData : http://192.168.3.35:9992/Ashx/image/20191210114551KiLw.jpg
     * Message : 设置朋友圈的背景图片成功
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
