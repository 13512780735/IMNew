package com.bcr.jianxinIM.im.server;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.bcr.jianxinIM.im.server.response.CheckOnlineRespone;
import com.bcr.jianxinIM.im.server.response.CirFriedPicRespone;
import com.bcr.jianxinIM.im.server.response.CirFriendComRespone;
import com.bcr.jianxinIM.im.server.response.ClickSupportRespone;
import com.bcr.jianxinIM.im.server.response.CollectListMessage;
import com.bcr.jianxinIM.im.server.response.CollectMessageRespone;
import com.bcr.jianxinIM.im.server.response.CollectVideoRespone;
import com.bcr.jianxinIM.im.server.response.DelCollectMessage;
import com.bcr.jianxinIM.im.server.response.DeleteCirFriendRespone;
import com.bcr.jianxinIM.im.server.response.DeleteComRespone;
import com.bcr.jianxinIM.im.server.response.DynamicListRespone;
import com.bcr.jianxinIM.im.server.response.DynamicRespone;
import com.bcr.jianxinIM.im.server.response.GetFriendInfoResponse;
import com.bcr.jianxinIM.im.server.response.GetUserAddWayResponse;
import com.bcr.jianxinIM.im.server.response.GetVersionMessage;
import com.bcr.jianxinIM.im.server.response.PushMessageRespone;
import com.bcr.jianxinIM.im.server.response.SetAddFriendResponse;
import com.bcr.jianxinIM.im.server.response.SetUserAddWayResponse;
import com.bcr.jianxinIM.im.server.response.UserInfoRespone;
import com.orhanobut.logger.Logger;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.network.http.RequestParams;
import com.bcr.jianxinIM.im.server.request.CheckPhoneRequest;
import com.bcr.jianxinIM.im.server.request.VerifyCodeRequest;
import com.bcr.jianxinIM.im.server.response.AgreeGroupNoticeResponse;
import com.bcr.jianxinIM.im.server.response.AppKeyRespone;
import com.bcr.jianxinIM.im.server.response.CheckPhoneResponse;
import com.bcr.jianxinIM.im.server.response.EditUserInfoResponse;
import com.bcr.jianxinIM.im.server.response.PerfectUserInfoResponse;
import com.bcr.jianxinIM.im.server.response.PhoneLoginResponse;
import com.bcr.jianxinIM.im.server.response.QRAddGroupResponse;
import com.bcr.jianxinIM.im.server.response.RegisterResponse;
import com.bcr.jianxinIM.im.server.response.SendCodeResponse;
import com.bcr.jianxinIM.im.server.response.SendGroupImageResponse;
import com.bcr.jianxinIM.im.server.response.SetBannedResponse;
import com.bcr.jianxinIM.im.server.response.SetNoticeResponse;
import com.bcr.jianxinIM.im.server.response.UpdatePwdResponse;
import com.bcr.jianxinIM.im.server.response.UserFeedBackRespone;
import com.bcr.jianxinIM.im.server.response.VerifyCodeResponse;
import com.bcr.jianxinIM.im.server.response.delMemberBannedResponse;
import com.bcr.jianxinIM.im.server.response.getCodeResponse;
import com.bcr.jianxinIM.im.server.response.getGroupNoticeResponse;
import com.bcr.jianxinIM.im.server.response.getMemberBannedResponse;
import com.bcr.jianxinIM.im.server.response.getNeedValidatedRespone;
import com.bcr.jianxinIM.im.server.response.setMemberBannedResponse;
import com.bcr.jianxinIM.im.server.response.setNeedValidatedRespone;
import com.bcr.jianxinIM.im.server.utils.json.JsonMananger;

import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.bcr.jianxinIM.im.server.request.ChangePasswordRequest;
import com.bcr.jianxinIM.im.server.request.JoinGroupRequest;
import com.bcr.jianxinIM.im.server.request.SetNameRequest;
import com.bcr.jianxinIM.im.server.response.AddGroupMemberResponse;
import com.bcr.jianxinIM.im.server.response.AddToBlackListResponse;
import com.bcr.jianxinIM.im.server.response.AgreeFriendsResponse;
import com.bcr.jianxinIM.im.server.response.ChangePasswordResponse;
import com.bcr.jianxinIM.im.server.response.CreateGroupResponse;
import com.bcr.jianxinIM.im.server.response.DefaultConversationResponse;
import com.bcr.jianxinIM.im.server.response.DeleteFriendResponse;
import com.bcr.jianxinIM.im.server.response.DeleteGroupMemberResponse;
import com.bcr.jianxinIM.im.server.response.DismissGroupResponse;
import com.bcr.jianxinIM.im.server.response.FriendInvitationResponse;
import com.bcr.jianxinIM.im.server.response.GetBlackListResponse;
import com.bcr.jianxinIM.im.server.response.GetFriendInfoByIDResponse;
import com.bcr.jianxinIM.im.server.response.GetGroupInfoResponse;
import com.bcr.jianxinIM.im.server.response.GetGroupMemberResponse;
import com.bcr.jianxinIM.im.server.response.GetGroupResponse;
import com.bcr.jianxinIM.im.server.response.GetTokenResponse;
import com.bcr.jianxinIM.im.server.response.GetUserInfoByIdResponse;
import com.bcr.jianxinIM.im.server.response.GetUserInfoByPhoneResponse;
import com.bcr.jianxinIM.im.server.response.GetUserInfosResponse;
import com.bcr.jianxinIM.im.server.response.JoinGroupResponse;
import com.bcr.jianxinIM.im.server.response.LoginResponse;
import com.bcr.jianxinIM.im.server.response.QiNiuTokenResponse;
import com.bcr.jianxinIM.im.server.response.QuitGroupResponse;
import com.bcr.jianxinIM.im.server.response.RemoveFromBlackListResponse;
import com.bcr.jianxinIM.im.server.response.RestPasswordResponse;
import com.bcr.jianxinIM.im.server.response.SetFriendDisplayNameResponse;
import com.bcr.jianxinIM.im.server.response.SetGroupDisplayNameResponse;
import com.bcr.jianxinIM.im.server.response.SetGroupNameResponse;
import com.bcr.jianxinIM.im.server.response.SetGroupPortraitResponse;
import com.bcr.jianxinIM.im.server.response.SetNameResponse;
import com.bcr.jianxinIM.im.server.response.SetPortraitResponse;
import com.bcr.jianxinIM.im.server.response.SyncTotalDataResponse;
import com.bcr.jianxinIM.im.server.response.UserRelationshipResponse;
import com.bcr.jianxinIM.im.server.response.VersionResponse;
import com.bcr.jianxinIM.im.server.utils.NLog;

/**
 * Created by AMing on 16/1/14.
 * Company RongCloud
 */
@SuppressWarnings("deprecation")
public class SealAction extends BaseAction {
    private final String CONTENT_TYPE = "application/json";
    private final String ENCODING = "utf-8";

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public SealAction(Context context) {
        super(context);
    }


    /**
     * 检查手机是否被注册
     *
     * @param region 国家码
     * @param phone  手机号
     * @throws HttpException
     */
    public CheckPhoneResponse checkPhoneAvailable(String region, String phone) throws HttpException {
        String url = getURL("user/check_phone_available");
        String json = JsonMananger.beanToJson(new CheckPhoneRequest(phone, region));
        StringEntity entity = null;
        try {
            entity = new StringEntity(json, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        CheckPhoneResponse response = null;
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, CheckPhoneResponse.class);
        }
        return response;
    }


    /**
     * 发送验证码
     *
     * @param Type 国家码
     * @param phone  手机号
     * @throws HttpException
     */
    public SendCodeResponse sendCode(String phone,String Type) throws HttpException {
        String url = getURL2("UserAshx.ashx?Method=SendMsg");
        RequestParams params=new RequestParams();
        params.put("Tel",phone);
        params.put("Type",Type);
        SendCodeResponse response = null;
        String result = httpManager.post(mContext, url,params);
        if (!TextUtils.isEmpty(result)) {
            response = JsonMananger.jsonToBean(result, SendCodeResponse.class);
        }
        return response;
    }

    /*
    * 200: 验证成功
    1000: 验证码错误
    2000: 验证码过期
    异常返回，返回的 HTTP Status Code 如下：

    400: 错误的请求
    500: 应用服务器内部错误
    * */

    /**
     * 验证验证码是否正确(必选先用手机号码调sendcode)
     *
     * @param region 国家码
     * @param phone  手机号
     * @throws HttpException
     */
    public VerifyCodeResponse verifyCode(String region, String phone, String code) throws HttpException {
        String url = getURL("user/verify_code");
        String json = JsonMananger.beanToJson(new VerifyCodeRequest(region, phone, code));
        VerifyCodeResponse response = null;
        StringEntity entity = null;
        try {
            entity = new StringEntity(json, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        if (!TextUtils.isEmpty(result)) {
            Log.e("VerifyCodeResponse", result);
            response = jsonToBean(result, VerifyCodeResponse.class);
        }
        return response;
    }

    /**
     * 注册
     *
     * @param mobile
     * @param password
     * @param confirmPwd
     * @return
     * @throws HttpException
     */
    public RegisterResponse register(String mobile,String code, String password, String confirmPwd) throws HttpException {
        String url = getURL("UserAshx.ashx?Method=RegUser");

        RequestParams params = new RequestParams();
        params.put("Tel", mobile);
        params.put("MsgStr", code);
        params.put("Password1", password);
        params.put("Password2", confirmPwd);
//        StringEntity entity = null;
//        try {
//            entity = new StringEntity(JsonMananger.beanToJson(new RegisterRequest(nickname, password, verification_token)), ENCODING);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        RegisterResponse response = null;
        String result = httpManager.post(mContext, url, params);
        if (!TextUtils.isEmpty(result)) {
            NLog.e("RegisterResponse", result);
            response = jsonToBean(result, RegisterResponse.class);
        }
        return response;
    }

    /**
     * 完善资料
     *
     * @param userId
     * @param userPic
     * @param nickName
     * @param sex
     * @param city
     * @return
     * @throws HttpException
     */
    public PerfectUserInfoResponse perfectUserInfo(String userId, String userPic, String nickName, int sex, String city) throws HttpException {
        String url = getURL("UserAshx.ashx?Method=UserInfo");

        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("UserPic", userPic);
        params.put("UserNickName", nickName);
        params.put("Sex", sex);
        params.put("City", city);
        PerfectUserInfoResponse response = null;
        String result = httpManager.post(mContext, url, params);
        if (!TextUtils.isEmpty(result)) {
            NLog.e("PerfectUserInfoResponse", result);
            response = jsonToBean(result, PerfectUserInfoResponse.class);
        }
        return response;
    }

    /**
     * 登录: 登录成功后，会设置 Cookie，后续接口调用需要登录的权限都依赖于 Cookie。
     *
     * @param region   国家码
     * @param phone    手机号
     * @param password 密码
     * @throws HttpException
     */
    public LoginResponse login(String region, String phone, String password) throws HttpException {
        String uri = getURL("UserAshx.ashx?Method=Login");
        // String json = JsonMananger.beanToJson(new LoginRequest(region, phone, password));
        RequestParams params = new RequestParams();
        params.put("Tel", phone);
        params.put("Password", password);
//        StringEntity entity = null;
//        try {
//            entity = new StringEntity(json, ENCODING);
//            entity.setContentType(CONTENT_TYPE);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        String result = httpManager.post(mContext, uri, params);
        LoginResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("LoginResponse", result);
            response = JsonMananger.jsonToBean(result, LoginResponse.class);
        }
        return response;
    }


    /**
     * 获取 token 前置条件需要登录   502 坏的网关 测试环境用户已达上限
     *
     * @throws HttpException
     */
    public GetTokenResponse getToken(String userId) throws HttpException {
        String url = getURL("UserAshx.ashx?Method=get_token");

        RequestParams params = new RequestParams();
        params.put("Id", userId);
        String result = httpManager.post(url, params);
        GetTokenResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("GetTokenResponse", result);
            response = jsonToBean(result, GetTokenResponse.class);
        }
        return response;
    }

    /**
     * 设置自己的昵称
     *
     * @param nickname 昵称
     * @throws HttpException
     */
    public SetNameResponse setName(String nickname) throws HttpException {
        String url = getURL("user/set_nickname");
        String json = JsonMananger.beanToJson(new SetNameRequest(nickname));
        StringEntity entity = null;
        try {
            entity = new StringEntity(json, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        SetNameResponse response = null;
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, SetNameResponse.class);
        }
        return response;
    }

    /**
     * 设置用户头像
     *
     * @param portraitUri 头像 path
     * @throws HttpException
     */
    public SetPortraitResponse setPortrait(String portraitUri) throws HttpException {
        String url = getURL("UserAshx.ashx?Method=GetImage");
        RequestParams params = new RequestParams();
        params.put("BaseString", portraitUri);
//        String json = JsonMananger.beanToJson(new SetPortraitRequest(portraitUri));
//        StringEntity entity = null;
//        try {
//            entity = new StringEntity(json, ENCODING);
//            entity.setContentType(CONTENT_TYPE);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        SetPortraitResponse response = null;
        String result = httpManager.post(mContext, url, params);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, SetPortraitResponse.class);
        }
        return response;
    }


    /**
     * 当前登录用户通过旧密码设置新密码  前置条件需要登录才能访问
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @throws HttpException
     */
    public ChangePasswordResponse changePassword(String oldPassword, String newPassword) throws HttpException {
        String url = getURL("user/change_password");
        String json = JsonMananger.beanToJson(new ChangePasswordRequest(oldPassword, newPassword));
        StringEntity entity = null;
        try {
            entity = new StringEntity(json, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        ChangePasswordResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("ChangePasswordResponse", result);
            response = jsonToBean(result, ChangePasswordResponse.class);
        }
        return response;
    }


    /**
     * 通过手机验证码重置密码
     *
     * @param Tel
     * @param MsgStr
     * @param password1
     * @param password2
     * @return
     * @throws HttpException
     */
    public RestPasswordResponse restPassword(String Tel, String MsgStr, String password1, String password2) throws HttpException {
        String uri = getURL2("UserAshx.ashx?Method=RetrievePwd");
        RequestParams params = new RequestParams();
        params.put("Tel", Tel);
        params.put("MsgStr", MsgStr);
        params.put("Password1", password1);
        params.put("Password2", password2);
        String result = httpManager.post(mContext, uri, params);
        RestPasswordResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("RestPasswordResponse", result);
            response = jsonToBean(result, RestPasswordResponse.class);
        }
        return response;
    }

    /**
     * 修改获取用户休息
     *
     * @param UserId
     * @param NickName
     * @param Sex
     * @param City
     * @param UserPic
     * @return
     * @throws HttpException
     */
    public EditUserInfoResponse EditUserInfo(String UserId, String NickName, String Sex, String City, String UserPic) throws HttpException {
        String uri = getURL("UserAshx.ashx?Method=set_UserInfo");
        RequestParams params = new RequestParams();
        params.put("userId", UserId);
        params.put("NickName", NickName);
        params.put("Sex", Sex);
        params.put("City", City);
        params.put("UserPic", UserPic);
        String result = httpManager.post(mContext, uri, params);
        EditUserInfoResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("RestPasswordResponse", result);
            response = jsonToBean(result, EditUserInfoResponse.class);
        }
        return response;
    }

    /**
     * 手机验证码登录
     *
     * @param Tel
     * @param MsgStr
     * @return
     * @throws HttpException
     */
    public LoginResponse PhoneLogin(String Tel, String MsgStr) throws HttpException {
        String uri = getURL2("UserAshx.ashx?Method=PhoneLogin");
        RequestParams params = new RequestParams();
        params.put("Tel", Tel);
        params.put("MsgStr", MsgStr);
        String result = httpManager.post(mContext, uri, params);
        LoginResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("PhoneLoginResponse", result);
            response = jsonToBean(result, LoginResponse.class);
        }
        return response;
    }

    /**
     * 修改密码
     *
     * @param UserId
     * @param Password1
     * @param Password2
     * @param Password3
     * @return
     * @throws HttpException
     */
    public UpdatePwdResponse UpdatePwd(String UserId, String Password1, String Password2, String Password3) throws HttpException {
        String uri = getURL("UserAshx.ashx?Method=UpdatePwd");
        RequestParams params = new RequestParams();
        params.put("userId", UserId);
        params.put("Password1", Password1);
        params.put("Password2", Password2);
        params.put("Password3", Password3);
        String result = httpManager.post(mContext, uri, params);
        UpdatePwdResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("UpdatePwdResponse", result);
            response = jsonToBean(result, UpdatePwdResponse.class);
        }
        return response;
    }

    /**
     * 根据 id 去服务端查询用户信息
     *
     * @param userid 用户ID
     * @throws HttpException
     */
    public GetUserInfoByIdResponse getUserInfoById(String userid) throws HttpException {
        String url = getURL("UserAshx.ashx?Method=GetUserInfo");
        RequestParams params = new RequestParams();
        params.put("userId", userid);
        String result = httpManager.post(url, params);
        GetUserInfoByIdResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("GetUserInfoByIdResponse", result);
            response = jsonToBean(result, GetUserInfoByIdResponse.class);
        }
        return response;
    }


    /**
     * 通过国家码和手机号查询用户信息
     *
     * @param phone  手机号
     * @throws HttpException
     */
    public GetUserInfoByPhoneResponse getUserInfoFromPhone( String phone) throws HttpException {
        String url = getURL("/UserAshx.ashx?Method=PhoneGetUserInfo");
        RequestParams params = new RequestParams();
        params.put("Tel", phone);
        String result = httpManager.post(url, params);
        GetUserInfoByPhoneResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("GetUserInfoByPhoneResponse", result);
            response = jsonToBean(result, GetUserInfoByPhoneResponse.class);
        }
        return response;
    }


    /**
     * 发送好友邀请
     *
     * @param fromUserId
     * @param toUserId
     * @param content
     * @return
     * @throws HttpException
     */
    public FriendInvitationResponse sendFriendInvitation(String fromUserId, String toUserId, String content) throws HttpException {
        String url = getURL("FriedShip.ashx?Method=invite");
        // String json = JsonMananger.beanToJson(new FriendInvitationRequest(fromUserId,toUserId, co));
        RequestParams params = new RequestParams();
        params.put("fromUserId", fromUserId);
        params.put("toUserId", toUserId);
        params.put("content", content);
//        StringEntity entity = null;
//        try {
//            entity = new StringEntity(json, ENCODING);
//            entity.setContentType(CONTENT_TYPE);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        String result = httpManager.post(mContext, url, params);
        FriendInvitationResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("FriendInvitationResponse", result);
            response = jsonToBean(result, FriendInvitationResponse.class);
        }
        return response;
    }


    /**
     * 获取发生过用户关系的列表
     *
     * @throws HttpException
     */
    public UserRelationshipResponse getAllUserRelationship(String userId) throws HttpException {
        String url = getURL("FriedShip.ashx?Method=GetFrideAll");
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        String result = httpManager.post(url, params);
        UserRelationshipResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("UserRelationshipResponse", result);
            response = jsonToBean(result, UserRelationshipResponse.class);
        }
        return response;
    }

    /**
     * 根据userId去服务器查询好友信息
     *
     * @throws HttpException
     */
    public GetFriendInfoByIDResponse getFriendInfoByID(String userid, String FriendId) throws HttpException {
        String url = getURL("FriedShip.ashx?Method=GetFride");
        RequestParams params = new RequestParams();
        params.put("userId", userid);
        params.put("FriendId", FriendId);
        String result = httpManager.post(url, params);
        GetFriendInfoByIDResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("GetFriendInfoByIDResponse", result);
            response = jsonToBean(result, GetFriendInfoByIDResponse.class);
        }
        return response;
    }

    /**
     * 同意对方好友邀请
     *
     * @param friendId 好友ID
     * @throws HttpException
     */
    public AgreeFriendsResponse agreeFriends(String UserId, String friendId) throws HttpException {
        String url = getURL("FriedShip.ashx?Method=agree");

        RequestParams params = new RequestParams();
        params.put("userId", UserId);
        params.put("FriedId", friendId);
//        String json = JsonMananger.beanToJson(new AgreeFriendsRequest(friendId));
//        StringEntity entity = null;
//        try {
//            entity = new StringEntity(json, ENCODING);
//            entity.setContentType(CONTENT_TYPE);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        String result = httpManager.post(mContext, url, params);
        AgreeFriendsResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, AgreeFriendsResponse.class);
        }
        return response;
    }

    /**
     * 创建群组
     *
     * @param name      群组名
     * @param memberIds 群组成员id
     * @throws HttpException
     */
    public CreateGroupResponse createGroup(String name, List<String> memberIds, String imgPic) throws HttpException {
        String url = getURL("Group.ashx?Method=CreateGroup");
        String userId = JsonMananger.beanToJson(memberIds);
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("GroupName", name);
        params.put("GroupPic", imgPic);
//        String json = JsonMananger.beanToJson(new CreateGroupRequest(name, memberIds,imgPic));
//        StringEntity entity = null;
//        try {
//            entity = new StringEntity(json, ENCODING);
//            entity.setContentType(CONTENT_TYPE);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        String result = httpManager.post(mContext, url, params);

        Logger.d("url:-->" + url);
        // Logger.d("params:-->"+params);
        CreateGroupResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, CreateGroupResponse.class);
        }
        return response;
    }

    /**
     * 创建者设置群组头像
     *
     * @param groupId     群组Id
     * @param portraitUri 群组头像
     * @throws HttpException
     */
    public SetGroupPortraitResponse setGroupPortrait(String UserID, String groupId, String portraitUri) throws HttpException {
        String url = getURL("Group.ashx?Method=set_GroupImage");
        RequestParams params = new RequestParams();
        params.put("userId", UserID);
        params.put("GroupId", groupId);
        params.put("GroupPic", portraitUri);
//        String json = JsonMananger.beanToJson(new SetGroupPortraitRequest(groupId, portraitUri));
//        StringEntity entity = null;
//        try {
//            entity = new StringEntity(json, ENCODING);
//            entity.setContentType(CONTENT_TYPE);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        String result = httpManager.post(mContext, url, params);
        SetGroupPortraitResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, SetGroupPortraitResponse.class);
        }
        return response;
    }

    /**
     * 获取当前用户所属群组列表
     *
     * @throws HttpException
     */
    public GetGroupResponse getGroups(String UserId) throws HttpException {
        String url = getURL("Group.ashx?Method=GetGroupList");
        RequestParams params = new RequestParams();
        params.put("userId", UserId);
        String result = httpManager.post(mContext, url, params);
        GetGroupResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("GetGroupResponse", result);
            response = jsonToBean(result, GetGroupResponse.class);
        }
        return response;
    }

    /**
     * 根据 群组id 查询该群组信息   403 群组成员才能看
     *
     * @param groupId 群组Id
     * @throws HttpException
     */
    public GetGroupInfoResponse getGroupInfo(String groupId) throws HttpException {
        String url = getURL("Group.ashx?Method=GroupInfo");
        RequestParams params = new RequestParams();
        params.put("GroupId", groupId);
        String result = httpManager.post(mContext, url, params);
        GetGroupInfoResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("GetGroupInfoResponse", result);
            response = jsonToBean(result, GetGroupInfoResponse.class);
        }
        return response;
    }
    //设置是否允许添加好友
    public SetAddFriendResponse setAddFriend(String groupId,String Statu) throws HttpException {
        String url = getURL("Group.ashx?Method=set_AddFriend");
        RequestParams params = new RequestParams();
        params.put("GroupId", groupId);
        params.put("Statu", Statu);
        String result = httpManager.post(mContext, url, params);
        SetAddFriendResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("GetGroupInfoResponse", result);
            response = jsonToBean(result, SetAddFriendResponse.class);
        }
        return response;
    }

    /**
     * 获取添加方式
     * @return
     * @throws HttpException
     */
    public GetUserAddWayResponse getUserAddWay(String UserId) throws HttpException {
        String url = getURL("UserAshx.ashx?Method=Get_UserAddWay");
        RequestParams params = new RequestParams();
        params.put("userId", UserId);
        String result = httpManager.post(mContext, url, params);
        GetUserAddWayResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("GetGroupInfoResponse", result);
            response = jsonToBean(result, GetUserAddWayResponse.class);
        }
        return response;
    }
    public SetUserAddWayResponse setUserAddWay(String UserId,String Type,String AddWay) throws HttpException {
        String url = getURL("UserAshx.ashx?Method=set_UserAddWay");
        RequestParams params = new RequestParams();
        params.put("userId", UserId);
        params.put("Type", Type);
        params.put("AddWay", AddWay);
        String result = httpManager.post(mContext, url, params);
        SetUserAddWayResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("GetGroupInfoResponse", result);
            response = jsonToBean(result, SetUserAddWayResponse.class);
        }
        return response;
    }
    public GetFriendInfoResponse getFriendInfoDetaisl(String Content, String Type) throws HttpException {
        String url = getURL("FriedShip.ashx?Method=get_FriendInfo");
        RequestParams params = new RequestParams();
        params.put("Content", Content);
        params.put("Type", Type);
        String result = httpManager.post(mContext, url, params);
        GetFriendInfoResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("GetGroupInfoResponse", result);
            response = jsonToBean(result, GetFriendInfoResponse.class);
        }
        return response;
    }



    /**
     * 根据群id获取群组成员
     *
     * @param groupId 群组Id
     * @throws HttpException
     */
    public GetGroupMemberResponse getGroupMember(String groupId) throws HttpException {
        String url = getURL("Group.ashx?Method=QueryGroup");
        RequestParams params = new RequestParams();
        params.put("GroupId", groupId);
        String result = httpManager.post(mContext, url, params);
        GetGroupMemberResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("GetGroupMemberResponse", result);
            response = jsonToBean(result, GetGroupMemberResponse.class);
        }
        return response;
    }

    /**
     * 当前用户添加群组成员
     *
     * @param groupId   群组Id
     * @param memberIds 成员集合
     * @throws HttpException
     */
    public AddGroupMemberResponse addGroupMember(String AddId, String GroupName, String groupId, List<String> memberIds) throws HttpException {
        String url = getURL("Group.ashx?Method=AddGrouo&GroupName");
        String json = JsonMananger.beanToJson(memberIds);
        RequestParams params = new RequestParams();
        params.put("GroupName", GroupName);
        params.put("GroupId", groupId);
        params.put("userId", json);
        params.put("AddId", AddId);

//        StringEntity entity = null;
//        try {
//            entity = new StringEntity(json, ENCODING);
//            entity.setContentType(CONTENT_TYPE);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        String result = httpManager.post(mContext, url, params);
        AddGroupMemberResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("AddGroupMemberResponse", result);
            response = jsonToBean(result, AddGroupMemberResponse.class);
        }
        return response;
    }

    /**
     * 创建者将群组成员提出群组
     *
     * @param groupId   群组Id
     * @param memberIds 成员集合
     * @throws HttpException
     */
    public DeleteGroupMemberResponse deleGroupMember(String groupId, List<String> memberIds, String KickId) throws HttpException {
        String url = getURL("Group.ashx?Method=KickGroup");
        String json = JsonMananger.beanToJson(memberIds);
        RequestParams params = new RequestParams();
        params.put("userId", json);
        params.put("GroupId", groupId);
        params.put("KickId", KickId);
        String result = httpManager.post(mContext, url, params);
        DeleteGroupMemberResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("DeleteGroupMemberResponse", result);
            response = jsonToBean(result, DeleteGroupMemberResponse.class);
        }
        return response;
    }

    /**
     * 创建者更改群组昵称
     *
     * @param groupId 群组Id
     * @param name    群昵称
     * @throws HttpException
     */
    public SetGroupNameResponse setGroupName(String groupId, String name, String UserId) throws HttpException {
        String url = getURL("Group.ashx?Method=Set_GroupName");

        RequestParams params = new RequestParams();
        params.put("userId", UserId);
        params.put("GroupId", groupId);
        params.put("GroupName", name);
        String result = httpManager.post(mContext, url, params);
        SetGroupNameResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("SetGroupNameResponse", result);
            response = jsonToBean(result, SetGroupNameResponse.class);
        }
        return response;
    }

    public QRAddGroupResponse SendAddGroupMsg(String groupId, String UserId) throws HttpException {
        String url = getURL("Group.ashx?Method=SendAddGroupMsg");

        RequestParams params = new RequestParams();
        params.put("userId", UserId);
        params.put("GroupId", groupId);
        String result = httpManager.post(mContext, url, params);
        QRAddGroupResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("QRAddGroupResponse", result);
            response = jsonToBean(result, QRAddGroupResponse.class);
        }
        return response;
    }

    /**
     * 用户自行退出群组
     *
     * @param groupId 群组Id
     * @throws HttpException
     */
    public QuitGroupResponse quitGroup(String UserId, String groupId) throws HttpException {
        String url = getURL("Group.ashx?Method=QuitGroup");
        RequestParams params = new RequestParams();
        params.put("userId", UserId);
        params.put("GroupId", groupId);
        String result = httpManager.post(mContext, url, params);
        QuitGroupResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("QuitGroupResponse", result);
            response = jsonToBean(result, QuitGroupResponse.class);
        }
        return response;
    }

    /**
     * 发送公告
     *
     * @param Bulletin
     * @param groupId
     * @return
     * @throws HttpException
     */
    public SetNoticeResponse setNotice(String Bulletin, String groupId) throws HttpException {
        String url = getURL("Group.ashx?Method=ste_Groupbulletin");
        RequestParams params = new RequestParams();
        params.put("Bulletin", Bulletin);
        params.put("GroupId", groupId);
        String result = httpManager.post(mContext, url, params);
        SetNoticeResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("QuitGroupResponse", result);
            response = jsonToBean(result, SetNoticeResponse.class);
        }
        return response;
    }

    /**
     * \
     * 设置群禁言
     *
     * @param groupId
     * @return
     * @throws HttpException
     */
    public SetBannedResponse setBanned(String Statu, String groupId) throws HttpException {
        String url = getURL("Group.ashx?Method=Forbidden");
        RequestParams params = new RequestParams();
        params.put("Statu", Statu);
        params.put("GroupId", groupId);
        String result = httpManager.post(mContext, url, params);
        SetBannedResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("QuitGroupResponse", result);
            response = jsonToBean(result, SetBannedResponse.class);
        }
        return response;
    }

    /**
     * 获取成员是否被禁言
     *
     * @param GroupId
     * @param UserId
     * @return
     * @throws HttpException
     */
    public getMemberBannedResponse getMemberBanned(String GroupId, String UserId) throws HttpException {
        String url = getURL("Group.ashx?Method=IsGag");
        RequestParams params = new RequestParams();
        params.put("GroupId", GroupId);
        params.put("userId", UserId);
        String result = httpManager.post(mContext, url, params);
        getMemberBannedResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("QuitGroupResponse", result);
            response = jsonToBean(result, getMemberBannedResponse.class);
        }
        return response;
    }

    /**
     * 设置成员禁言
     *
     * @param GroupId
     * @param UserId
     * @return
     * @throws HttpException
     */
    public setMemberBannedResponse setMemberBanned(String GroupId, String UserId) throws HttpException {
        String url = getURL("Group.ashx?Method=AddGag");
        RequestParams params = new RequestParams();
        params.put("GroupId", GroupId);
        params.put("userId", UserId);
        String result = httpManager.post(mContext, url, params);
        setMemberBannedResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("QuitGroupResponse", result);
            response = jsonToBean(result, setMemberBannedResponse.class);
        }
        return response;
    }

    /**
     * 解除成员禁言
     *
     * @param GroupId
     * @param UserId
     * @return
     * @throws HttpException
     */
    public delMemberBannedResponse delMemberBanned(String GroupId, String UserId) throws HttpException {
        String url = getURL("Group.ashx?Method=RemoveGag");
        RequestParams params = new RequestParams();
        params.put("GroupId", GroupId);
        params.put("userId", UserId);
        String result = httpManager.post(mContext, url, params);
        delMemberBannedResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("QuitGroupResponse", result);
            response = jsonToBean(result, delMemberBannedResponse.class);
        }
        return response;
    }

    /**
     * 创建者解散群组
     *
     * @param groupId 群组Id
     * @throws HttpException
     */
    public DismissGroupResponse dissmissGroup(String UserID, String groupId) throws HttpException {
        String url = getURL("Group.ashx?Method=DismissGroup");

        RequestParams params = new RequestParams();
        params.put("userId", UserID);
        params.put("GroupId", groupId);
        String result = httpManager.post(mContext, url, params);
        DismissGroupResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("DismissGroupResponse", result);
            response = jsonToBean(result, DismissGroupResponse.class);
        }
        return response;
    }


    /**
     * 修改自己的当前的群昵称
     *
     * @param groupId     群组Id
     * @param displayName 群名片
     * @throws HttpException
     */
    public SetGroupDisplayNameResponse setGroupDisplayName(String groupId, String displayName, String UserId) throws HttpException {
        String url = getURL("Group.ashx?Method=set_Remark");
        RequestParams params = new RequestParams();
        params.put("userId", UserId);
        params.put("GroupId", groupId);
        params.put("Remark", displayName);
        String result = httpManager.post(mContext, url, params);
        SetGroupDisplayNameResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, SetGroupDisplayNameResponse.class);
        }
        return response;
    }

    /**
     * 删除好友
     *
     * @param friendId 好友Id
     * @throws HttpException
     */
    public DeleteFriendResponse deleteFriend(String userId, String friendId) throws HttpException {
        String url = getURL("FriedShip.ashx?Method=delete");
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("FriedId", friendId);
//        String json = JsonMananger.beanToJson(new DeleteFriendRequest(friendId));
//        StringEntity entity = null;
//        try {
//            entity = new StringEntity(json, ENCODING);
//            entity.setContentType(CONTENT_TYPE);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        String result = httpManager.post(mContext, url, params);
        DeleteFriendResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, DeleteFriendResponse.class);
        }
        return response;
    }

    /**
     * 设置好友的备注名称
     *
     * @param friendId    好友Id
     * @param displayName 备注名
     * @throws HttpException
     */
    public SetFriendDisplayNameResponse setFriendDisplayName(String userId, String friendId, String displayName) throws HttpException {
        String url = getURL("FriedShip.ashx?Method=set_URemark");

        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("FriedId", friendId);
        params.put("Remark", displayName);
//        String json = JsonMananger.beanToJson(new SetFriendDisplayNameRequest(friendId, displayName));
//        StringEntity entity = null;
//        try {
//            entity = new StringEntity(json, ENCODING);
//            entity.setContentType(CONTENT_TYPE);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        String result = httpManager.post(mContext, url, params);
        SetFriendDisplayNameResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, SetFriendDisplayNameResponse.class);
        }
        return response;
    }

    /**
     * 获取黑名单
     *
     * @throws HttpException
     */
    public GetBlackListResponse getBlackList(String UserId) throws HttpException {
        String url = getURL("UserAshx.ashx?Method=blacklist");
        RequestParams params = new RequestParams();
        params.put("userId", UserId);
        String result = httpManager.post(mContext, url, params);
        GetBlackListResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, GetBlackListResponse.class);
        }
        return response;
    }

    /**
     * 加入黑名单
     *
     * @param friendId 群组Id
     * @throws HttpException
     */
    public AddToBlackListResponse addToBlackList(String userId, String friendId) throws HttpException {
        String url = getURL("UserAshx.ashx?Method=add_to_blacklist");
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("BlackUserId", friendId);
//        String json = JsonMananger.beanToJson(new AddToBlackListRequest(friendId));
//        StringEntity entity = null;
//        try {
//            entity = new StringEntity(json, ENCODING);
//            entity.setContentType(CONTENT_TYPE);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        String result = httpManager.post(mContext, url, params);
        AddToBlackListResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, AddToBlackListResponse.class);
        }
        return response;
    }

    /**
     * 移除黑名单
     *
     * @param friendId 好友Id
     * @throws HttpException
     */
    public RemoveFromBlackListResponse removeFromBlackList(String UserId, String friendId) throws HttpException {
        String url = getURL("UserAshx.ashx?Method=remove_from_blacklist");
        // String json = JsonMananger.beanToJson(new RemoveFromBlacklistRequest(friendId));

        RequestParams params = new RequestParams();
        params.put("userId", UserId);
        params.put("BlackUserId", friendId);
//        StringEntity entity = null;
//        try {
//            entity = new StringEntity(json, ENCODING);
//            entity.setContentType(CONTENT_TYPE);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        String result = httpManager.post(mContext, url, params);
        RemoveFromBlackListResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, RemoveFromBlackListResponse.class);
        }
        return response;
    }

    /**
     * 获取二维码
     *
     * @param Type
     * @param Id
     * @return
     * @throws HttpException
     */
    public getCodeResponse getIamgeCode(String Type, String Id) throws HttpException {
        String url = getURL("UserAshx.ashx?Method=GetImage2");
        RequestParams params = new RequestParams();
        params.put("Type", Type);
        params.put("Id", Id);
        String result = httpManager.post(mContext, url, params);
        getCodeResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, getCodeResponse.class);
        }
        return response;
    }

    /**
     * 获取群主需要同意的列表
     *
     * @param UserId
     * @return
     * @throws HttpException
     */
    public getGroupNoticeResponse GetGroupRequest(String UserId) throws HttpException {
        String url = getURL("Group.ashx?Method=GetGroupRequest");
        RequestParams params = new RequestParams();
        params.put("userId", UserId);
        String result = httpManager.post(mContext, url, params);
        getGroupNoticeResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, getGroupNoticeResponse.class);
        }
        return response;
    }

    /**
     * 群主同意成员进群
     *
     * @param GroupId
     * @param UserId
     * @return
     * @throws HttpException
     */
    public AgreeGroupNoticeResponse AgreeGroup(String GroupId, String UserId) throws HttpException {
        String url = getURL("Group.ashx?Method=AgreeGroup");
        RequestParams params = new RequestParams();
        params.put("GroupId", GroupId);
        params.put("userId", UserId);
        String result = httpManager.post(mContext, url, params);
        AgreeGroupNoticeResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, AgreeGroupNoticeResponse.class);
        }
        return response;
    }

    /**
     * 发送截屏信息
     *
     * @param GroupId
     * @param UserId
     * @param UserPic
     * @return
     * @throws HttpException
     */
    public SendGroupImageResponse SendGroupIamge(String GroupId, String UserId, String UserPic) throws HttpException {
        String url = getURL("Group.ashx?Method=SendGroup");
        RequestParams params = new RequestParams();
        params.put("GroupId", GroupId);
        params.put("userId", UserId);
        params.put("UserPic", UserPic);
        String result = httpManager.post(mContext, url, params);
        SendGroupImageResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, SendGroupImageResponse.class);
        }
        return response;
    }

    /**
     * 获取融云key
     * @return
     * @throws HttpException
     */
    public AppKeyRespone GetAppKey() throws HttpException {
        String url = getURL("UserAshx.ashx?Method=GetAppKey");
        String result = httpManager.post(url);
        AppKeyRespone response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, AppKeyRespone.class);
        }
        return response;
    }

    /**
     * 获取添加好友是否需要验证
     * @param userId
     * @return
     * @throws HttpException
     */
    public getNeedValidatedRespone getNeedValidated(String userId) throws HttpException {
        String url = getURL("UserAshx.ashx?Method=get_NeedValidated");
        RequestParams params=new RequestParams();
        params.put("userId",userId);
        String result = httpManager.post(url,params);
        getNeedValidatedRespone response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, getNeedValidatedRespone.class);
        }
        return response;
    }

    /**
     * 设置是否需要好友验证
     * @param userId
     * @param flag
     * @return
     * @throws HttpException
     */
    public setNeedValidatedRespone setNeedValidated(String userId,String flag) throws HttpException {
        String url = getURL("UserAshx.ashx?Method=set_NeedValidated");
        RequestParams params=new RequestParams();
        params.put("userId",userId);
        params.put("NeedValidated",flag);
        String result = httpManager.post(url,params);
        setNeedValidatedRespone response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, setNeedValidatedRespone.class);
        }
        return response;
    }

    /**
     * 意见反馈
     * @param userId
     * @param Title
     * @param Substance
     * @return
     * @throws HttpException
     */
    public UserFeedBackRespone SetUserFeedBack(String userId, String Title,String Substance) throws HttpException {
        String url = getURL("UserAshx.ashx?Method=UserFeedBack");
        RequestParams params=new RequestParams();
        params.put("userId",userId);
        params.put("Title",Title);
        params.put("Substance",Substance);
        String result = httpManager.post(url,params);
        UserFeedBackRespone response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, UserFeedBackRespone.class);
        }
        return response;
    }

    /**
     * 发布朋友圈
     * @return
     * @throws HttpException
     */
    public DynamicRespone postDynamic(String UserId, List<String> BasePic, String Content,String Location) throws HttpException {
        String url = getURL("Dynamic.ashx?Method=Release");
        String json=JsonMananger.beanToJson(BasePic);
        RequestParams params=new RequestParams();
        params.put("userId",UserId);
        params.put("BasePic",json);
        params.put("Content",Content);
        params.put("Location",Location);
        String result = httpManager.post(url,params);
        DynamicRespone response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, DynamicRespone.class);
        }
        return response;
    }

    /**
     *  获取朋友圈列表
     * @param UserId
     * @param pageIndex
     * @return
     * @throws HttpException
     */
    public DynamicListRespone GetDynamic(String UserId, String pageIndex) throws HttpException {
        String url = getURL("Dynamic.ashx?Method=Get_Dynamic");
        RequestParams params=new RequestParams();
        params.put("userId",UserId);
        params.put("pageIndex",pageIndex);
        String result = httpManager.post(url,params);
        DynamicListRespone response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, DynamicListRespone.class);
        }
        return response;
    }

    /**
     * 点赞
     * @param UserId
     * @param CirFrendId
     * @param isClick
     * @return
     * @throws HttpException
     */
    public ClickSupportRespone click_support(String UserId, String CirFrendId,String isClick) throws HttpException {
        String url = getURL("Dynamic.ashx?Method=click_support");
        RequestParams params=new RequestParams();
        params.put("userId",UserId);
        params.put("CirFrendId",CirFrendId);
        params.put("isClick",isClick);
        String result = httpManager.post(url,params);
        ClickSupportRespone response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, ClickSupportRespone.class);
        }
        return response;
    }

    /**
     * 朋友圈评论
     * @param UserId
     * @param CirFrendId
     * @param Content
     * @param FriendId
     * @return
     * @throws HttpException
     */
    public CirFriendComRespone CirFriendCom(String UserId, String CirFrendId,String Content,String FriendId) throws HttpException {
        String url = getURL("Dynamic.ashx?Method=CirFriendCom");
        RequestParams params=new RequestParams();
        params.put("userId",UserId);
        params.put("CirFrendId",CirFrendId);
        params.put("Content",Content);
        params.put("FriendId",FriendId);
        String result = httpManager.post(url,params);
        CirFriendComRespone response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, CirFriendComRespone.class);
        }
        return response;
    }

    /**
     * 朋友圈用户的详细信息
     * @param UserId
     * @return
     * @throws HttpException
     */
    public UserInfoRespone GetUserInfo(String UserId,String friendId) throws HttpException {
        String url = getURL("Dynamic.ashx?Method=Get_UserInfo");
        RequestParams params=new RequestParams();
        params.put("UserId",UserId);
        params.put("FriendId",friendId);
        String result = httpManager.post(url,params);
        UserInfoRespone response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, UserInfoRespone.class);
        }
        return response;
    }

    /**
     * 设置朋友圈的背景图片
     * @param UserId
     * @param CirFriedPic
     * @return
     * @throws HttpException
     */
    public CirFriedPicRespone setCirFriedPic(String UserId, String CirFriedPic) throws HttpException {
        String url = getURL("Dynamic.ashx?Method=set_CirFriedPic");
        RequestParams params=new RequestParams();
        params.put("userId",UserId);
        params.put("CirFriedPic",CirFriedPic);
        String result = httpManager.post(url,params);
        CirFriedPicRespone response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, CirFriedPicRespone.class);
        }
        return response;
    }
    /**
     * 删除朋友圈
     * @param UserId
     * @param CirFriedId
     * @return
     * @throws HttpException
     */
    public DeleteCirFriendRespone DeleteCirFriend(String UserId,String CirFriedId) throws HttpException {
        String url = getURL("Dynamic.ashx?Method=DeleteCirFriend");
        RequestParams params=new RequestParams();
        params.put("userId",UserId);
        params.put("CirFriedId",CirFriedId);
        String result = httpManager.post(url,params);
        DeleteCirFriendRespone response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, DeleteCirFriendRespone.class);
        }
        return response;
    }

    /**
     * 删除评论
     * @param UserId
     * @param ComId
     * @return
     * @throws HttpException
     */
    public DeleteComRespone DeleteCom(String UserId, String ComId) throws HttpException {
        String url = getURL("Dynamic.ashx?Method=Delete_Com");
        RequestParams params=new RequestParams();
        params.put("userId",UserId);
        params.put("ComId",ComId);
        String result = httpManager.post(url,params);
        DeleteComRespone response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, DeleteComRespone.class);
        }
        return response;
    }

    /**
     * 检测是否在线
     * @param UserId
     * @return
     * @throws HttpException
     */
    public CheckOnlineRespone checkOnline(String UserId) throws HttpException {
        String url = getURL("UserAshx.ashx?Method=checkOnline");
        RequestParams params=new RequestParams();
        params.put("userId",UserId);
        String result = httpManager.post(url,params);
        CheckOnlineRespone response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, CheckOnlineRespone.class);
        }
        return response;
    }

    /**
     * 调用后台发送消息
     * @param UserId
     * @return
     * @throws HttpException
     */
    public PushMessageRespone pushMessage(String UserId) throws HttpException {
        String url = getURL3("Push.ashx?Method=PushMsg");
        RequestParams params=new RequestParams();
        params.put("userId",UserId);
        String result = httpManager.post(url,params);
        PushMessageRespone response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, PushMessageRespone.class);
        }
        return response;
    }

    /**
     *收藏
     * @return
     * @throws HttpException
     */
    public CollectMessageRespone CollectMessage(String UserId,String SourceId,String Type,String Content,String format) throws HttpException {
        String url = getURL4("OSS.ashx?Method=Upload");
        RequestParams params=new RequestParams();
        params.put("userId",UserId);
        params.put("SourceId",SourceId);
        params.put("Type",Type);
        params.put("Content",Content);
        params.put("format",format);
        String result = httpManager.post(url,params);
        CollectMessageRespone response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, CollectMessageRespone.class);
        }
        return response;
    }
    public  CollectVideoRespone CollectVideoMessage(String UserId,String SourceId,String Content,String Cover) throws HttpException {
        String url = getURL4("OSS.ashx?Method=UploadSP");
        RequestParams params=new RequestParams();
        params.put("userId",UserId);
        params.put("SourceId",SourceId);
        params.put("Content",Content);
        params.put("Cover",Cover);
        String result = httpManager.post(url,params);
        CollectVideoRespone response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, CollectVideoRespone.class);
        }
        return response;
    }
    /**
     * 收藏列表
     * @param UserId
     * @return
     * @throws HttpException
     */

    public CollectListMessage CollectListMessage(String UserId,String pageIndex) throws HttpException {
        String url = getURL4("OSS.ashx?Method=GetUpload");
        RequestParams params=new RequestParams();
        params.put("userId",UserId);
        params.put("pageIndex",pageIndex);
        String result = httpManager.post(url,params);
        CollectListMessage response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, CollectListMessage.class);
        }
        return response;
    }

    /**
     * 删除收藏
     * @return
     * @throws HttpException
     */
    public DelCollectMessage DelCollectMessage(String UserId, List<String> id) throws HttpException {
        String url = getURL4("OSS.ashx?Method=DeleteUpload");

        String json = JsonMananger.beanToJson(id);
        RequestParams params=new RequestParams();
        params.put("userId",UserId);
        params.put("ID",json);
        String result = httpManager.post(url,params);
        DelCollectMessage response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, DelCollectMessage.class);
        }
        return response;
    }

    /**
     * 获取版本号
     * @param Type
     * @return
     * @throws HttpException
     */
    public GetVersionMessage GetVersion(String Type) throws HttpException {
        String url = getURL("UserAshx.ashx?Method=GetVersion");
        RequestParams params=new RequestParams();
        params.put("Type",Type);
        String result = httpManager.post(url,params);
        GetVersionMessage response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result,GetVersionMessage.class);
        }
        return response;
    }


    public QiNiuTokenResponse getQiNiuToken() throws HttpException {
        String url = getURL("user/get_image_token");
        String result = httpManager.get(mContext, url);
        QiNiuTokenResponse q = null;
        if (!TextUtils.isEmpty(result)) {
            q = jsonToBean(result, QiNiuTokenResponse.class);
        }
        return q;
    }


    /**
     * 当前用户加入某群组
     *
     * @param groupId 群组Id
     * @throws HttpException
     */
    public JoinGroupResponse JoinGroup(String groupId) throws HttpException {
        String url = getURL("group/join");
        String json = JsonMananger.beanToJson(new JoinGroupRequest(groupId));
        StringEntity entity = null;
        try {
            entity = new StringEntity(json, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        JoinGroupResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, JoinGroupResponse.class);
        }
        return response;
    }


    /**
     * 获取默认群组 和 聊天室
     *
     * @throws HttpException
     */
    public DefaultConversationResponse getDefaultConversation() throws HttpException {
        String url = getURL("misc/demo_square");
        String result = httpManager.get(mContext, url);
        DefaultConversationResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, DefaultConversationResponse.class);
        }
        return response;
    }

    /**
     * 根据一组ids 获取 一组用户信息
     *
     * @param ids 用户 id 集合
     * @throws HttpException
     */
    public GetUserInfosResponse getUserInfos(List<String> ids) throws HttpException {
        String url = getURL("user/batch?");
        StringBuilder sb = new StringBuilder();
        for (String s : ids) {
            sb.append("id=");
            sb.append(s);
            sb.append("&");
        }
        String stringRequest = sb.substring(0, sb.length() - 1);
        String newUrl = url + stringRequest;
        String result = httpManager.get(mContext, newUrl);
        GetUserInfosResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, GetUserInfosResponse.class);
        }
        return response;
    }

    /**
     * 获取版本信息
     *
     * @throws HttpException
     */
    public VersionResponse getSealTalkVersion() throws HttpException {
        String url = getURL("misc/client_version");
        String result = httpManager.get(mContext, url.trim());
        VersionResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, VersionResponse.class);
        }
        return response;
    }

    public SyncTotalDataResponse syncTotalData(String version) throws HttpException {
        String url = getURL("user/sync/" + version);
        String result = httpManager.get(mContext, url);
        SyncTotalDataResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, SyncTotalDataResponse.class);
        }
        return response;
    }

//    /**
//     * 根据userId去服务器查询好友信息
//     *
//     * @throws HttpException
//     */
//    public GetFriendInfoByIDResponse getFriendInfoByID(String userid) throws HttpException {
//        String url = getURL("friendship/" + userid + "/profile");
//        String result = httpManager.get(url);
//        GetFriendInfoByIDResponse response = null;
//        if (!TextUtils.isEmpty(result)) {
//            response = jsonToBean(result, GetFriendInfoByIDResponse.class);
//        }
//        return response;
//    }
    /**
     //     * 根据userId去服务器查询好友信息
     //     *
     //     * @throws HttpException
     //     */

}
