package com.bcr.jianxinIM.im.server.response;

import java.util.List;

public class CollectListMessage {

    /**
     * Success : true
     * ResultData : [{"ID":"2cc4e798-b5e9-4bd0-85d4-05988fe52675","userId":"6274583050","UserName":"小帅锅","UserPic":"http://192.168.3.35:9992/Ashx/image/20191227160828WMmp.png","UserSex":1,"Date":"16分钟前","Content":"今晚加班","Type":"0"},{"ID":"14fd5780-702c-4dcc-8fc5-c3fb7467acb8","userId":"6274583050","UserName":"小帅锅","UserPic":"http://192.168.3.35:9992/Ashx/image/20191227160828WMmp.png","UserSex":1,"Date":"18分钟前","Content":"http://jximtest.oss-cn-shenzhen.aliyuncs.com/image/6274583050/cc13d17bd1364446aaa8353897cf59bf.jpg","Type":"1"}]
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
         * ID : 2cc4e798-b5e9-4bd0-85d4-05988fe52675
         * UserId : 6274583050
         * UserName : 小帅锅
         * UserPic : http://192.168.3.35:9992/Ashx/image/20191227160828WMmp.png
         * UserSex : 1
         * Date : 16分钟前
         * Content : 今晚加班
         * Type : 0
         */

        private String ID;
        private String UserId;
        private String UserName;
        private String UserPic;
        private int UserSex;
        private String Date;
        private String Content;
        private String Type;
        private Boolean checked;

        public Boolean getChecked() {
            return checked;
        }

        public void setChecked(Boolean checked) {
            this.checked = checked;
        }

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String UserId) {
            this.UserId = UserId;
        }

        public String getUserName() {
            return UserName;
        }

        public void setUserName(String UserName) {
            this.UserName = UserName;
        }

        public String getUserPic() {
            return UserPic;
        }

        public void setUserPic(String UserPic) {
            this.UserPic = UserPic;
        }

        public int getUserSex() {
            return UserSex;
        }

        public void setUserSex(int UserSex) {
            this.UserSex = UserSex;
        }

        public String getDate() {
            return Date;
        }

        public void setDate(String Date) {
            this.Date = Date;
        }

        public String getContent() {
            return Content;
        }

        public void setContent(String Content) {
            this.Content = Content;
        }

        public String getType() {
            return Type;
        }

        public void setType(String Type) {
            this.Type = Type;
        }
    }
}
