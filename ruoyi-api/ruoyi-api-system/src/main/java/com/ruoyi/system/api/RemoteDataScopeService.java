package com.ruoyi.system.api;

/**
 * 数据权限服务
 *
 * @author csFan
 */
public interface RemoteDataScopeService {

    /**
     * 获取角色自定义权限语句
     */
    String getRoleCustom(Long roleId);

    /**
     * 获取部门和下级权限语句
     */
    String getDeptAndChild(Long deptId);

}
