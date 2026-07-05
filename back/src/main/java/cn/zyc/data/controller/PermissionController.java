package cn.zyc.data.controller;

import cn.zyc.basics.log.LogType;
import cn.zyc.basics.log.SystemLog;
import cn.zyc.basics.parameter.CommonConstant;
import cn.zyc.basics.redis.RedisTemplateHelper;
import cn.zyc.basics.utils.ResultUtil;
import cn.zyc.basics.utils.SecurityUtil;
import cn.zyc.basics.baseVo.Result;
import cn.zyc.data.entity.*;
import cn.zyc.data.service.*;
import cn.zyc.data.utils.VoUtil;
import cn.zyc.data.utils.ZwzNullUtils;
import cn.zyc.data.vo.MenuVo;
import cn.zyc.data.vo.UserByPermissionVo;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

 
@RestController
@Api(tags = "菜单管理接口")
@RequestMapping("/zyc/permission")
@CacheConfig(cacheNames = "permission")
@Transactional
public class PermissionController {

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private IRolePermissionService iRolePermissionService;

    @Autowired
    private IPermissionService iPermissionService;

    @Autowired
    private IUserRoleService iUserRoleService;

    @Autowired
    private RedisTemplateHelper redisTemplateHelper;

    @Autowired
    private IRoleService iRoleService;

    @Autowired
    private IUserService iUserService;

    @SystemLog(about = "查询菜单权限拥有者", type = LogType.DATA_CENTER,doType = "PERMISSION-01")
    @ApiOperation(value = "查询菜单权限拥有者")
    @RequestMapping(value = "/getUserByPermission", method = RequestMethod.GET)
    public Result<List<UserByPermissionVo>> getUserByPermission(@RequestParam String permissionId){
        Permission permission = iPermissionService.getById(permissionId);
        if(permission == null) {
            return ResultUtil.error("该菜单已被删除");
        }
        List<UserByPermissionVo> ansList = new ArrayList<>();
        // 查询用户
        QueryWrapper<RolePermission> qw = new QueryWrapper<>();
        qw.eq("permission_id",permissionId);
        List<RolePermission> rolePermissionList = iRolePermissionService.list(qw);
        for (RolePermission rp : rolePermissionList) {
            Role role = iRoleService.getById(rp.getRoleId());
            if(role != null) {
                QueryWrapper<UserRole> urQw = new QueryWrapper<>();
                urQw.eq("role_id",role.getId());
                List<UserRole> userRoleList = iUserRoleService.list(urQw);
                for (UserRole ur : userRoleList) {
                    User user = iUserService.getById(ur.getUserId());
                    if(user != null) {
                        boolean flag = false;
                        for (UserByPermissionVo vo : ansList) {
                            if(Objects.equals(vo.getUserId(),user.getId())) {
                                flag = true;
                                vo.setRoleStr(vo.getRoleStr() + role.getName() + "(" + role.getDescription() + ") ");
                                break;
                            }
                        }
                        if(!flag) {
                            UserByPermissionVo vo = new UserByPermissionVo();
                            vo.setUserId(user.getId());
                            vo.setUserName(user.getNickname());
                            vo.setRoleStr(role.getName());
                            vo.setCode(user.getUsername());
                            vo.setMobile(user.getMobile());
                            ansList.add(vo);
                        }
                    }
                }
            }
        }
        return new ResultUtil<List<UserByPermissionVo>>().setData(ansList);
    }

    @ApiOperation(value = "根据层级查询菜单")
    private List<Permission> getPermissionListByLevel(int level) {
        QueryWrapper<Permission> qw = new QueryWrapper<>();
        qw.eq("level",level);
        qw.orderByAsc("sort_order");
        return iPermissionService.list(qw);
    }

    @SystemLog(about = "查询菜单", type = LogType.DATA_CENTER,doType = "PERMISSION-02")
    @RequestMapping(value = "/getMenuList", method = RequestMethod.GET)
    @ApiOperation(value = "查询菜单")
    public Result<List<MenuVo>> getMenuList(){
        List<MenuVo> menuList = new ArrayList<>();
        User currUser = securityUtil.getCurrUser();
        String keyInRedis = "permission::userMenuList:" + currUser.getId();
        String valueInRedis = redisTemplateHelper.get(keyInRedis);
        if(!ZwzNullUtils.isNull(valueInRedis)){
            List<MenuVo> cachedMenuList = JSON.parseArray(valueInRedis,MenuVo.class);
            if(!Objects.equals(currUser.getType(),1)) {
                cachedMenuList.removeIf(vo -> Objects.equals(vo.getTitle(), "系统基础模块"));
            }
            return new ResultUtil<List<MenuVo>>().setData(cachedMenuList);
        }
        // 拥有的菜单列表
        List<Permission> list = getPermissionByUserId(currUser.getId());
        // 顶级菜单
        for(Permission permission : list){
            if(CommonConstant.PERMISSION_NAV.equals(permission.getType())&&CommonConstant.LEVEL_ZERO.equals(permission.getLevel())) {
                menuList.add(VoUtil.permissionToMenuVo(permission));
            }
        }
        // 一级菜单
        List<MenuVo> firstMenuList = new ArrayList<>();
        for(Permission permission : list){
            if(Objects.equals(CommonConstant.PERMISSION_PAGE,permission.getType()) && Objects.equals(CommonConstant.LEVEL_ONE,permission.getLevel())) {
                firstMenuList.add(VoUtil.permissionToMenuVo(permission));
            }
        }
        // 二级菜单
        List<MenuVo> secondMenuList = new ArrayList<>();
        for(Permission permission : list){
            if(Objects.equals(CommonConstant.PERMISSION_PAGE,permission.getType()) && Objects.equals(CommonConstant.LEVEL_TWO,permission.getLevel())) {
                secondMenuList.add(VoUtil.permissionToMenuVo(permission));
            }
        }
        // 按钮
        List<MenuVo> buttonPermissions = new ArrayList<>();
        for(Permission permission : list){
            if(Objects.equals(CommonConstant.PERMISSION_OPERATION,permission.getType()) && Objects.equals(CommonConstant.LEVEL_THREE,permission.getLevel())) {
                buttonPermissions.add(VoUtil.permissionToMenuVo(permission));
            }
        }
        // 有权限的二级菜单
        for(MenuVo vo : secondMenuList){
            List<String> permTypes = new ArrayList<>();
            for(MenuVo menuVo : buttonPermissions){
                if(Objects.equals(vo.getId(),menuVo.getParentId())){
                    permTypes.add(menuVo.getButtonType());
                }
            }
            vo.setPermTypes(permTypes);
        }
        // 二连一
        for(MenuVo vo : firstMenuList) {
            List<MenuVo> secondMenu = new ArrayList<>();
            for(MenuVo menuVo : secondMenuList){
                if(Objects.equals(vo.getId(),menuVo.getParentId())) {
                    secondMenu.add(menuVo);
                }
            }
            vo.setChildren(secondMenu);
        }
        // 一连顶
        for(MenuVo vo : menuList) {
            List<MenuVo> firstMenu = new ArrayList<>();
            for(MenuVo menuVo : firstMenuList){
                if(Objects.equals(vo.getId(),menuVo.getParentId())) {
                    firstMenu.add(menuVo);
                }
            }
            vo.setChildren(firstMenu);
        }

        if(!Objects.equals(currUser.getType(),1)) {
            menuList.removeIf(vo -> Objects.equals(vo.getTitle(), "系统基础模块"));
        }

        redisTemplateHelper.set(keyInRedis, JSONObject.toJSONString(menuList), 10L, TimeUnit.DAYS);
        return new ResultUtil<List<MenuVo>>().setData(menuList);
    }

    @SystemLog(about = "搜索菜单", type = LogType.DATA_CENTER,doType = "PERMISSION-03")
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ApiOperation(value = "搜索菜单")
    public Result<List<Permission>> searchPermissionList(@RequestParam String title){
        QueryWrapper<Permission> qw = new QueryWrapper<>();
        qw.like("title",title);
        qw.orderByAsc("sort_order");
        return new ResultUtil<List<Permission>>().setData(iPermissionService.list(qw));
    }

    @RequestMapping(value = "/clearMenuCache", method = RequestMethod.POST)
    @ApiOperation(value = "清除菜单缓存")
    public Result<Object> clearMenuCache(){
        User currUser = securityUtil.getCurrUser();
        String keyInRedis = "permission::userMenuList:" + currUser.getId();
        redisTemplateHelper.delete(keyInRedis);
        return ResultUtil.success("菜单缓存已清除");
    }

    @ApiOperation(value = "根据父ID查询菜单")
    private List<Permission> getPermissionListByParentId(String parentId) {
        QueryWrapper<Permission> qw = new QueryWrapper<>();
        qw.eq("parent_id",parentId);
        qw.orderByAsc("sort_order");
        return iPermissionService.list(qw);
    }

    @SystemLog(about = "查询全部菜单", type = LogType.DATA_CENTER,doType = "PERMISSION-04")
    @RequestMapping(value = "/getAllList", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部菜单")
    @Cacheable(key = "'allList'")
    public Result<List<Permission>> getAllList(){
        // 顶级菜单列表
        List<Permission> list0 = getPermissionListByLevel(0);
        for(Permission p0 : list0){
            // 一级
            List<Permission> list1 = getPermissionListByParentId(p0.getId());
            p0.setChildren(list1);
            // 二级
            for(Permission p1 : list1){
                List<Permission> children1 = getPermissionListByParentId(p1.getId());
                p1.setChildren(children1);
                // 三级
                for(Permission p2 : children1){
                    List<Permission> children2 = getPermissionListByParentId(p2.getId());
                    p2.setChildren(children2);
                }
            }
        }
        return new ResultUtil<List<Permission>>().setData(list0);
    }

    @SystemLog(about = "删除菜单", type = LogType.DATA_CENTER,doType = "PERMISSION-05")
    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "删除菜单")
    @CacheEvict(key = "'menuList'")
    public Result<Object> delByIds(@RequestParam String[] ids){
        for(String id : ids){
            QueryWrapper<RolePermission> qw = new QueryWrapper<>();
            qw.like("permission_id",id);
            long rolePermissionCount = iRolePermissionService.count(qw);
            if(rolePermissionCount > 0L) {
                Permission permission = iPermissionService.getById(id);
                if(permission == null) {
                    return ResultUtil.error("菜单不存在");
                }
                return ResultUtil.error(permission.getTitle() + "菜单正在被角色使用，不能删除");
            }
        }
        for(String id:ids){
            iPermissionService.removeById(id);
        }
        redisTemplateHelper.delete("permission::allList");
        return ResultUtil.success();
    }

    @SystemLog(about = "添加菜单", type = LogType.DATA_CENTER,doType = "PERMISSION-06")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "添加菜单")
    @CacheEvict(key = "'menuList'")
    public Result<Permission> add(Permission permission){
        if(Objects.equals(CommonConstant.PERMISSION_OPERATION,permission.getType())) {
            QueryWrapper<Permission> perQw = new QueryWrapper<>();
            perQw.eq("title",permission.getTitle());
            long permissionByTitleCount = iPermissionService.count(perQw);
            if(permissionByTitleCount > 0L){
                return new ResultUtil<Permission>().setErrorMsg("名称已存在");
            }
        }
        if(Objects.equals(CommonConstant.PERMISSION_NAV,permission.getType())) {
            // 顶级菜单添加标识
            permission.setParentId("0");
            if(ZwzNullUtils.isNull(permission.getPath())) {
                permission.setPath(permission.getName());
            }
            permission.setDescription("");
            permission.setComponent("");
        }
        iPermissionService.saveOrUpdate(permission);
        redisTemplateHelper.delete("permission::allList");
        return new ResultUtil<Permission>().setData(permission);
    }

    @SystemLog(about = "编辑菜单", type = LogType.DATA_CENTER,doType = "PERMISSION-07")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ApiOperation(value = "编辑菜单")
    public Result<Permission> edit(Permission permission){
        if(Objects.equals(CommonConstant.PERMISSION_OPERATION,permission.getType())) {
            Permission p = iPermissionService.getById(permission.getId());
            if(!Objects.equals(p.getTitle(),permission.getTitle())) {
                QueryWrapper<Permission> perQw = new QueryWrapper<>();
                perQw.eq("title",permission.getTitle());
                long permissionCount = iPermissionService.count(perQw);
                if(permissionCount > 0L){
                    return new ResultUtil<Permission>().setErrorMsg("名称已存在");
                }
            }
        }
        iPermissionService.saveOrUpdate(permission);
        Set<String> keysUser = redisTemplateHelper.keys("user:" + "*");
        redisTemplateHelper.delete(keysUser);
        Set<String> keysUserMenu = redisTemplateHelper.keys("permission::userMenuList:*");
        redisTemplateHelper.delete(keysUserMenu);
        redisTemplateHelper.delete("permission::allList");
        return new ResultUtil<Permission>().setData(permission);
    }

    private List<Permission> getPermissionByUserId(String userId) {
        List<String> roleIdList = new ArrayList<>();
        User user = iUserService.getById(userId);
        if(user != null && user.getType() != null) {
            QueryWrapper<Role> mappedRoleQw = new QueryWrapper<>();
            if(Objects.equals(user.getType(),1)) {
                mappedRoleQw.and(wrapper -> wrapper.eq("name", "ROLE_ADMIN").or().like("description", "管理员"));
            } else if(Objects.equals(user.getType(),2)) {
                mappedRoleQw.and(wrapper -> wrapper.like("description", "电池出售方").or().like("description", "出售方"));
            } else if(Objects.equals(user.getType(),3)) {
                mappedRoleQw.and(wrapper -> wrapper.like("description", "电池买入方").or().like("description", "买入方"));
            } else if(Objects.equals(user.getType(),4)) {
                mappedRoleQw.and(wrapper -> wrapper.eq("name", "ROLE_USER").or().like("description", "客服人员"));
            }
            mappedRoleQw.orderByDesc("default_role");
            mappedRoleQw.orderByAsc("create_time");
            List<Role> mappedRoleList = iRoleService.list(mappedRoleQw);
            for (Role role : mappedRoleList) {
                roleIdList.add(role.getId());
            }
        }

        if(roleIdList.isEmpty()) {
            QueryWrapper<UserRole> urQw = new QueryWrapper<>();
            urQw.eq("user_id",userId);
            List<UserRole> userRoleList = iUserRoleService.list(urQw);
            for (UserRole userRole : userRoleList) {
                roleIdList.add(userRole.getRoleId());
            }
        }

        List<Permission> permissionList = new ArrayList<>();
        for (String roleId : roleIdList) {
            QueryWrapper<RolePermission> rpQw = new QueryWrapper<>();
            rpQw.eq("role_id",roleId);
            List<RolePermission> rolePermissionList = iRolePermissionService.list(rpQw);
            for (RolePermission rolePermission : rolePermissionList) {
                Permission perm = iPermissionService.getById(rolePermission.getPermissionId());
                if (perm == null) {
                    continue;
                }
                boolean flag = true;
                for (Permission permission : permissionList) {
                    if(Objects.equals(permission.getId(), perm.getId())) {
                        flag = false;
                        break;
                    }
                }
                if(flag) {
                    permissionList.add(perm);
                }
            }
        }
        appendChartDisplayPermissions(permissionList);
        return permissionList;
    }

    private void appendChartDisplayPermissions(List<Permission> permissionList) {
        QueryWrapper<Permission> chartQw = new QueryWrapper<>();
        chartQw.eq("title", "图表展示");
        List<Permission> chartRootList = iPermissionService.list(chartQw);
        for (Permission chartRoot : chartRootList) {
            addPermissionIfAbsent(permissionList, chartRoot);
            appendParentPermissions(permissionList, chartRoot);
            appendChildPermissions(permissionList, chartRoot.getId());
        }
    }

    private void appendParentPermissions(List<Permission> permissionList, Permission permission) {
        String parentId = permission.getParentId();
        while (!ZwzNullUtils.isNull(parentId) && !Objects.equals(parentId, "0")) {
            Permission parent = iPermissionService.getById(parentId);
            if (parent == null) {
                break;
            }
            addPermissionIfAbsent(permissionList, parent);
            parentId = parent.getParentId();
        }
    }

    private void appendChildPermissions(List<Permission> permissionList, String parentId) {
        List<Permission> children = getPermissionListByParentId(parentId);
        for (Permission child : children) {
            addPermissionIfAbsent(permissionList, child);
            appendChildPermissions(permissionList, child.getId());
        }
    }

    private void addPermissionIfAbsent(List<Permission> permissionList, Permission permission) {
        for (Permission existed : permissionList) {
            if (Objects.equals(existed.getId(), permission.getId())) {
                return;
            }
        }
        permissionList.add(permission);
    }
}
