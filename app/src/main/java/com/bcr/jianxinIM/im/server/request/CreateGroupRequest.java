package com.bcr.jianxinIM.im.server.request;

import java.util.List;

/**
 * Created by AMing on 16/1/25.
 * Company RongCloud
 */
public class CreateGroupRequest {

    private String name;
    private String imgUrl;

    private List<String> memberIds;

    public CreateGroupRequest(String name, List<String> memberIds,String imgUrl) {
        this.name = name;
        this.memberIds = memberIds;
        this.imgUrl = imgUrl;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
