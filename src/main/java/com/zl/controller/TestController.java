package com.zl.controller;

import com.zl.common.ApplicationContextHelper;
import com.zl.common.JsonData;
import com.zl.dao.SysAclModuleMapper;
import com.zl.exception.ParamException;
import com.zl.exception.PermissionException;
import com.zl.model.SysAclModule;
import com.zl.param.TestVo;
import com.zl.util.BeanValidator;
import com.zl.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author jacky
 * @date 2017/10/26
 */
@Controller
@RequestMapping("/test")
@Slf4j
public class TestController {

    @RequestMapping("/hello.json")
    @ResponseBody
    public JsonData hello() {
        log.info("hello");
        throw new PermissionException("test exception");
        // return JsonData.success("hello, permission");
    }

    @RequestMapping("/validate.json")
    @ResponseBody
    public JsonData validate(TestVo vo) throws ParamException {
        log.info("validate");
        SysAclModuleMapper moduleMapper = ApplicationContextHelper.popBean(SysAclModuleMapper.class);
        SysAclModule module = moduleMapper.selectByPrimaryKey(1);
        log.info(JsonMapper.obj2String(module));
        BeanValidator.check(vo);

        return JsonData.success("test validate");
    }
}
