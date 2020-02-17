package com.bcr.jianxinIM.model;

import java.io.Serializable;

public class ComUserBean implements Serializable {
    String userId;
    String userName;
    String UserPic;

    public String getUserPic() {
        return UserPic;
    }

    public void setUserPic(String userPic) {
        UserPic = userPic;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
