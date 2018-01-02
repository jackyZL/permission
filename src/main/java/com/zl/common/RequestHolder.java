package com.zl.common;


import com.zl.model.SysUser;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jacky
 * @date 2017/10/26
 */
public class RequestHolder {

    // 登录用户
    private static final ThreadLocal<SysUser> userHolder = new ThreadLocal<SysUser>();

    // 请求request
    private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<HttpServletRequest>();

    public static void add(SysUser sysUser) {
        userHolder.set(sysUser);
    }

    public static void add(HttpServletRequest request) {
        requestHolder.set(request);
    }

    public static SysUser getCurrentUser() {
        return userHolder.get();
    }

    public static HttpServletRequest getCurrentRequest() {
        return requestHolder.get();
    }

    public static void remove() {
        userHolder.remove();
        requestHolder.remove();
    }
}
