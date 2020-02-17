package com.bcr.jianxinIM.im.server.response;

public class CollectListMessage1 {

    /**
     * ID : c318d41e-0627-4879-84e5-1f34ab5d399d
     * UserId : 0149518470
     * UserName : 测试数据
     * UserPic : http://192.168.3.35:9992/Ashx/image/202001062006001pGc.png
     * UserSex : 0
     * Date : 1分钟前
     * Content : 车架号
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
    private String  Source;
    private String  Conver;

    public String getConver() {
        return Conver;
    }

    public void setConver(String conver) {
        Conver = conver;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }

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
