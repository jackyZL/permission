package com.zl.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zl.beans.LogType;
import com.zl.common.RequestHolder;
import com.zl.dao.SysLogMapper;
import com.zl.dao.SysRoleAclMapper;
import com.zl.model.SysLogWithBLOBs;
import com.zl.model.SysRoleAcl;
import com.zl.util.IpUtil;
import com.zl.util.JsonMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author jacky
 * @date 2017/10/26
 */
@Service
public class SysRoleAclService {

    @Resource
    private SysRoleAclMapper sysRoleAclMapper;
    @Resource
    private SysLogMapper sysLogMapper;

    public void changeRoleAcls(Integer roleId, List<Integer> aclIdList) {
        //1. 取出当前角色之前已经拥有的id
        List<Integer> originAclIdList = sysRoleAclMapper.getAclIdListByRoleIdList(Lists.newArrayList(roleId));
        if (originAclIdList.size() == aclIdList.size()) {
            Set<Integer> originAclIdSet = Sets.newHashSet(originAclIdList); // 原始权限id
            Set<Integer> aclIdSet = Sets.newHashSet(aclIdList); // 传入的权限id
            originAclIdSet.removeAll(aclIdSet);
            if (CollectionUtils.isEmpty(originAclIdSet)) { //表示传入和原始did列表一直，不需要更新db
                return;
            }
        }
        // 2.执行更新操作
        updateRoleAcls(roleId, aclIdList);
        // 3.记录日志
        saveRoleAclLog(roleId, originAclIdList, aclIdList);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateRoleAcls(int roleId, List<Integer> aclIdList) {
        sysRoleAclMapper.deleteByRoleId(roleId); //删除旧权限

        if (CollectionUtils.isEmpty(aclIdList)) {
            return;
        }
        List<SysRoleAcl> roleAclList = Lists.newArrayList();
        for (Integer aclId : aclIdList) {
            SysRoleAcl roleAcl = SysRoleAcl.builder()
                    .roleId(roleId)
                    .aclId(aclId)
                    .operator(RequestHolder.getCurrentUser().getUsername())
                    .operateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()))
                    .operateTime(new Date()).build();
            roleAclList.add(roleAcl);
        }
        sysRoleAclMapper.batchInsert(roleAclList);
    }

    private void saveRoleAclLog(int roleId, List<Integer> before, List<Integer> after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ROLE_ACL);
        sysLog.setTargetId(roleId);
        sysLog.setOldValue(before == null ? "" : JsonMapper.obj2String(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.obj2String(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setOperateTime(new Date());
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }
}
