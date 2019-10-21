package com.goodaybase.model.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * 微信端用户缓存对象
 *
 * @author Weddorn
 */
public class LoginUserVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 令牌
     */
    private String token;

    /**
     * 会员Id，
     */
    private String id;

    /**
     * 用户类型（0.平台管理员，1.企业超管，2.企业用户）
     */
    private Integer userType;

    /**
     * 是否会员（激活将成为会员）（1.是，2.否）
     */
    private Integer isMember;

    /**
     * 是否锁定（0.否,1.是），暂时不用
     */
    private Integer isLock;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 手机
     */
    private String phone;

    /**
     * 微信OPENID
     */
    private String openId;

    /**
     * 微信名称
     */
    private String nickName;

    /**
     * 头像图片路径
     */
    private String userFace;

    /**
     * 实名
     */
    private String realName;

    /**
     * 身份证
     */
    private String idNo;

    /**
     * 性别（Code：XB）
     */
    private String sex;

    /**
     * 出生日期
     */
    private Date birthday;
    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 项目规划主项文字
     */
    private String  mainItemsText;

    /**
     * 项目规划主项（字典项：FWXM）
     */
    private String  mainItemsBig;

    /**
     * 项目规划主项（字典项：FWXM）（逗号隔开）
     */
    private String  mainItemsSmall;

    /**
     * 项目规划副项文字
     */
    private String  subItemsText;

    /**
     * 项目规划副项（字典项：FWXM）
     */
    private String  subItemsBig;

    /**
     * 项目规划副项（字典项：FWXM）（逗号隔开）
     */
    private String  subItemsSmall;

    /**
     * 用户的角色code，英文逗号分隔
     */
    private String roles;

    /**
     * 当前角色
     */
    private String nowRole;

    /**
     * 学校id
     */
    private String etpId;

    /**
     * 企业名称
     */
    private String unitName;

    /**
     * 企业联系人
     */
    private String unitContacts;

    private String teacherStaffNo;

    public String getEtpId() {
        return etpId;
    }

    public void setEtpId(String etpId) {
        this.etpId = etpId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public Integer getIsMember() {
        return isMember;
    }

    public void setIsMember(Integer isMember) {
        this.isMember = isMember;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserFace() {
        return userFace;
    }

    public void setUserFace(String userFace) {
        this.userFace = userFace;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getIsLock() {
        return isLock;
    }

    public void setIsLock(Integer isLock) {
        this.isLock = isLock;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMainItemsText() {
        return mainItemsText;
    }

    public void setMainItemsText(String mainItemsText) {
        this.mainItemsText = mainItemsText;
    }

    public String getMainItemsBig() {
        return mainItemsBig;
    }

    public void setMainItemsBig(String mainItemsBig) {
        this.mainItemsBig = mainItemsBig;
    }

    public String getMainItemsSmall() {
        return mainItemsSmall;
    }

    public void setMainItemsSmall(String mainItemsSmall) {
        this.mainItemsSmall = mainItemsSmall;
    }

    public String getSubItemsText() {
        return subItemsText;
    }

    public void setSubItemsText(String subItemsText) {
        this.subItemsText = subItemsText;
    }

    public String getSubItemsBig() {
        return subItemsBig;
    }

    public void setSubItemsBig(String subItemsBig) {
        this.subItemsBig = subItemsBig;
    }

    public String getSubItemsSmall() {
        return subItemsSmall;
    }

    public void setSubItemsSmall(String subItemsSmall) {
        this.subItemsSmall = subItemsSmall;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitContacts() {
        return unitContacts;
    }

    public void setUnitContacts(String unitContacts) {
        this.unitContacts = unitContacts;
    }

    public String getTeacherStaffNo() {
        return teacherStaffNo;
    }

    public void setTeacherStaffNo(String teacherStaffNo) {
        this.teacherStaffNo = teacherStaffNo;
    }

    public String getNowRole() {
        return nowRole;
    }

    public void setNowRole(String nowRole) {
        this.nowRole = nowRole;
    }
}
