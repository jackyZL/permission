package com.zl.dto;

import com.google.common.collect.Lists;
import com.zl.model.SysAclModule;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author jacky
 * @date 2017/10/26
 */
@Getter
@Setter
@ToString
public class AclModuleLevelDto extends SysAclModule {

    //子级权限模块列表
    private List<AclModuleLevelDto> aclModuleList = Lists.newArrayList();

    //子级权限点列表
    private List<AclDto> aclList = Lists.newArrayList();

    public static AclModuleLevelDto adapt(SysAclModule aclModule) {
        AclModuleLevelDto dto = new AclModuleLevelDto();
        BeanUtils.copyProperties(aclModule, dto);
        return dto;
    }
}
