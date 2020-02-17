/*
package com.bcr.jianxin.im.message.provider;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.rong.imlib.model.UserInfo;

public class AtUserService {
    private static AtUserService instance;
    private static final String AT_GTOUP_IDS="at_group_ids";
    public static AtUserService getInstance() {
        if (instance == null) {
            instance = new AtUserService();
        }
        return instance;
    }

    //发送时@人的列表
    public List<TempUser> atUsers =new ArrayList<>();

    public void addUser(UserInfo user){
        TempUser tempUser=new TempUser();
        tempUser.uid=user.getUserId();
        tempUser.name=user.getName();
        atUsers.add(tempUser);
    }
    public List<String> getUserIds(String text){
        if(atUsers==null||atUsers.size()==0){
            return null;
        }
        ArrayList<String> ids=new ArrayList();
        for(int i=0;i<atUsers.size();i++){
            if(text.contains(atUsers.get(i).name)){
                ids.add(atUsers.get(i).uid);
            }
        }
        return ids;
    }

    //接受 ：谁@了我的列表
    private Set<String> atGroupIds =new HashSet<>();

    public Set<String> getAtGroupIds(){
        if(atGroupIds==null||atGroupIds.size()==0){
            String string=UserConfigUtil.getStringConfig(AT_GTOUP_IDS,"");
            if(string!=null&&!string.equals("")){
                atGroupIds=GSONUtil.getGsonInstence().fromJson(string,new TypeToken<Set<String>>(){}.getType());
            }
        }
        return atGroupIds;
    }
    public void addAtGroupId(String id){
        atGroupIds.add(id);
        UserConfigUtil.setConfig(AT_GTOUP_IDS,GSONUtil.getGsonInstence().toJson(atGroupIds),true);
    }
    public void removeAtGroupId(String id){
        atGroupIds.remove(id);
        UserConfigUtil.setConfig(AT_GTOUP_IDS,GSONUtil.getGsonInstence().toJson(atGroupIds),true);
    }
    public Set<String> curConversationId=new HashSet<>();

    public void addCurConversationId(String id){
        curConversationId.add(id);
    }
    public void removeCurConversationId(String id){
        curConversationId.remove(id);
    }
    public class TempUser{
        String uid;
        String name;
    }
}
*/
