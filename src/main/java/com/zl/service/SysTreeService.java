package com.zl.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.zl.dao.SysAclMapper;
import com.zl.dao.SysAclModuleMapper;
import com.zl.dao.SysDeptMapper;
import com.zl.dto.AclDto;
import com.zl.dto.AclModuleLevelDto;
import com.zl.dto.DeptLevelDto;
import com.zl.model.SysAcl;
import com.zl.model.SysAclModule;
import com.zl.model.SysDept;
import com.zl.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 计算树形结构类
 *
 * @author jacky
 * @date 2017/10/26
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Service
public class SysTreeService {

    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private SysAclModuleMapper sysAclModuleMapper;

    @Resource
    private SysCoreService sysCoreService;

    @Resource
    private SysAclMapper sysAclMapper;

    /**
     * 获取指定用户所拥有的权限树（包含权限模块和权限点）
     *
     * @param userId
     * @return
     */
    public List<AclModuleLevelDto> userAclTree(int userId) {
        List<SysAcl> userAclList = sysCoreService.getUserAclList(userId);
        List<AclDto> aclDtoList = Lists.newArrayList();
        for (SysAcl acl : userAclList) {
            AclDto dto = AclDto.adapt(acl);
            dto.setHasAcl(true);
            dto.setChecked(true);
            aclDtoList.add(dto);
        }
        return aclListToTree(aclDtoList);
    }

    /**
     * 获取角色下的权限树（包括整个系统的权限，其中权限包含权限模块及其权限点）
     *
     * @param roleId
     * @return
     */
    public List<AclModuleLevelDto> roleTree(int roleId) {
        // 1、当前用户已分配的权限点
        List<SysAcl> userAclList = sysCoreService.getCurrentUserAclList();
        // 2、当前角色分配的权限点
        List<SysAcl> roleAclList = sysCoreService.getRoleAclList(roleId);
        // 3、当前系统所有权限点
        List<AclDto> aclDtoList = Lists.newArrayList();

        Set<Integer> userAclIdSet = userAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());
        Set<Integer> roleAclIdSet = roleAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());

        List<SysAcl> allAclList = sysAclMapper.getAll(); //获取整个系统所有的acl列表
        for (SysAcl acl : allAclList) {
            AclDto dto = AclDto.adapt(acl);
            if (userAclIdSet.contains(acl.getId())) {
                dto.setHasAcl(true); // 设置是否拥有该权限点
            }
            if (roleAclIdSet.contains(acl.getId())) {
                dto.setChecked(true); // 设置是否默认选中
            }
            aclDtoList.add(dto);
        }
        return aclListToTree(aclDtoList); //权限列表转换为权限树
    }

    /**
     * 权限点列表 转换为 权限点树
     *
     * @param aclDtoList
     * @return
     */
    public List<AclModuleLevelDto> aclListToTree(List<AclDto> aclDtoList) {
        if (CollectionUtils.isEmpty(aclDtoList)) {
            return Lists.newArrayList();
        }
        List<AclModuleLevelDto> aclModuleLevelList = aclModuleTree(); //得到整个系统的权限模块树（不含权限点）

        Multimap<Integer, AclDto> moduleIdAclMap = ArrayListMultimap.create();
        for (AclDto acl : aclDtoList) {
            if (acl.getStatus() == 1) { //判断是否有效
                moduleIdAclMap.put(acl.getAclModuleId(), acl); //key为权限模块id，valu为acl
            }
        }
        bindAclsWithOrder(aclModuleLevelList, moduleIdAclMap); //将权限点绑定到权限模块树上
        return aclModuleLevelList;
    }

    /**
     * 将权限点绑定到权限树上面，有序
     *
     * @param aclModuleLevelList
     * @param moduleIdAclMap
     */
    public void bindAclsWithOrder(List<AclModuleLevelDto> aclModuleLevelList, Multimap<Integer, AclDto> moduleIdAclMap) {
        if (CollectionUtils.isEmpty(aclModuleLevelList)) {
            return;
        }
        for (AclModuleLevelDto dto : aclModuleLevelList) {
            List<AclDto> aclDtoList = (List<AclDto>) moduleIdAclMap.get(dto.getId());
            if (CollectionUtils.isNotEmpty(aclDtoList)) {
                Collections.sort(aclDtoList, aclSeqComparator);
                dto.setAclList(aclDtoList);
            }
            bindAclsWithOrder(dto.getAclModuleList(), moduleIdAclMap);
        }
    }


    /**
     * 获取整个权限系统的权限模块树
     *
     * @return
     */
    public List<AclModuleLevelDto> aclModuleTree() {
        List<SysAclModule> aclModuleList = sysAclModuleMapper.getAllAclModule();
        List<AclModuleLevelDto> dtoList = Lists.newArrayList();
        for (SysAclModule aclModule : aclModuleList) {
            dtoList.add(AclModuleLevelDto.adapt(aclModule));
        }
        return aclModuleListToTree(dtoList);
    }

    /**
     * 获取权限模块树
     *
     * @param dtoList
     * @return
     */
    public List<AclModuleLevelDto> aclModuleListToTree(List<AclModuleLevelDto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return Lists.newArrayList();
        }
        // level -> [aclmodule1, aclmodule2, ...] Map<String, List<Object>>
        Multimap<String, AclModuleLevelDto> levelAclModuleMap = ArrayListMultimap.create();
        List<AclModuleLevelDto> rootList = Lists.newArrayList();

        for (AclModuleLevelDto dto : dtoList) {
            levelAclModuleMap.put(dto.getLevel(), dto);
            if (LevelUtil.ROOT.equals(dto.getLevel())) {
                rootList.add(dto);
            }
        }
        Collections.sort(rootList, aclModuleSeqComparator);
        transformAclModuleTree(rootList, LevelUtil.ROOT, levelAclModuleMap);
        return rootList;
    }

    public void transformAclModuleTree(List<AclModuleLevelDto> dtoList, String level, Multimap<String, AclModuleLevelDto> levelAclModuleMap) {
        for (int i = 0; i < dtoList.size(); i++) {
            AclModuleLevelDto dto = dtoList.get(i);
            String nextLevel = LevelUtil.calculateLevel(level, dto.getId());
            List<AclModuleLevelDto> tempList = (List<AclModuleLevelDto>) levelAclModuleMap.get(nextLevel);
            if (CollectionUtils.isNotEmpty(tempList)) {
                Collections.sort(tempList, aclModuleSeqComparator);
                dto.setAclModuleList(tempList);
                transformAclModuleTree(tempList, nextLevel, levelAclModuleMap);
            }
        }
    }


    /**
     * 获取部门树
     *
     * @return
     */
    public List<DeptLevelDto> deptTree() {

        //1. 获取所有部门信息
        List<SysDept> deptList = sysDeptMapper.getAllDept();

        List<DeptLevelDto> dtoList = Lists.newArrayList();
        for (SysDept dept : deptList) {
            DeptLevelDto dto = DeptLevelDto.adapt(dept);
            dtoList.add(dto);
        }
        return deptListToTree(dtoList);
    }

    public List<DeptLevelDto> deptListToTree(List<DeptLevelDto> deptLevelList) {
        if (CollectionUtils.isEmpty(deptLevelList)) {
            return Lists.newArrayList();
        }
        //非常实用 level -> [dept1, dept2, ...]   Map<String, List<Object>>
        Multimap<String, DeptLevelDto> levelDeptMap = ArrayListMultimap.create(); //key为level，value为list[dept1,dept2，...]
        List<DeptLevelDto> rootList = Lists.newArrayList(); //顶级部门List

        for (DeptLevelDto dto : deptLevelList) {
            levelDeptMap.put(dto.getLevel(), dto); // 相同的key，会将value放入List结构中
            if (LevelUtil.ROOT.equals(dto.getLevel())) {
                rootList.add(dto);
            }
        }
        // 按照seq从小到大排序
        Collections.sort(rootList, new Comparator<DeptLevelDto>() { //排序rootList
            @Override
            public int compare(DeptLevelDto o1, DeptLevelDto o2) {
                return o1.getSeq() - o2.getSeq();
            }
        });
        // 递归生成树（通过level递归）
        transformDeptTree(rootList, LevelUtil.ROOT, levelDeptMap);
        return rootList;
    }

    /**
     * @param deptLevelList
     * @param level         当前deptLevelList所在的层级
     * @param levelDeptMap
     */
    public void transformDeptTree(List<DeptLevelDto> deptLevelList, String level, Multimap<String, DeptLevelDto> levelDeptMap) {
        for (int i = 0; i < deptLevelList.size(); i++) {
            // 遍历该层的每个元素
            DeptLevelDto deptLevelDto = deptLevelList.get(i);
            // 处理当前层级的数据
            String nextLevel = LevelUtil.calculateLevel(level, deptLevelDto.getId());
            // 处理下一层
            List<DeptLevelDto> tempDeptList = (List<DeptLevelDto>) levelDeptMap.get(nextLevel);
            if (CollectionUtils.isNotEmpty(tempDeptList)) {
                // 排序
                Collections.sort(tempDeptList, deptSeqComparator);
                // 设置下一层部门
                deptLevelDto.setDeptList(tempDeptList);
                // 进入到下一层处理
                transformDeptTree(tempDeptList, nextLevel, levelDeptMap);
            }
        }
    }

    public Comparator<DeptLevelDto> deptSeqComparator = new Comparator<DeptLevelDto>() {
        @Override
        public int compare(DeptLevelDto o1, DeptLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };

    public Comparator<AclModuleLevelDto> aclModuleSeqComparator = new Comparator<AclModuleLevelDto>() {
        @Override
        public int compare(AclModuleLevelDto o1, AclModuleLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };

    public Comparator<AclDto> aclSeqComparator = new Comparator<AclDto>() {
        @Override
        public int compare(AclDto o1, AclDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };
}
