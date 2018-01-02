package com.zl.beans;

/**
 * @author jacky
 * @date 2017/10/26
 */
public interface LogType {

    int TYPE_DEPT = 1; // 部门

    int TYPE_USER = 2; // 用户

    int TYPE_ACL_MODULE = 3; // 权限模块

    int TYPE_ACL = 4; // 权限点

    int TYPE_ROLE = 5; // 角色

    int TYPE_ROLE_ACL = 6; // 角色-权限

    int TYPE_ROLE_USER = 7; // 角色-用户
}
