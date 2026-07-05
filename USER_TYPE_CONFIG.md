# 用户类型配置说明

## 系统支持的用户类型

本系统支持 4 种用户类型，统一编号和定义如下：

| Type | 中文名称 | 英文标识 | 前端标签 | 后端角色 | 说明 |
|------|---------|---------|---------|---------|------|
| 1 | 管理员 | ADMIN | 管理员 | ROLE_ADMIN | 系统管理员，拥有全部权限 |
| 2 | 电池出售方 | SELLER | 电池出售方 | ROLE_SELLER | 电池产品供应商 |
| 3 | 电池买入方 | BUYER | 电池买入方 | ROLE_BUYER | 电池产品购买方 |
| 4 | 客服人员 | SERVICE | 客服人员 | ROLE_SERVICE/ROLE_CUSTOMER_SERVICE | 客服及咨询接待人员 |

## 前端实现

### 1. 登录页面 (`front/src/views/login.vue`)
```javascript
roleOptions: [
    { label: "管理员", value: "ADMIN", aliases: ["ROLE_ADMIN", "管理员", "超级管理员", "1"] },
    { label: "电池出售方", value: "SELLER", aliases: ["ROLE_SELLER", "电池出售方", "出售方", "卖方", "2"] },
    { label: "电池买入方", value: "BUYER", aliases: ["ROLE_BUYER", "电池买入方", "买入方", "买方", "3"] },
    { label: "客服人员", value: "SERVICE", aliases: ["ROLE_SERVICE", "ROLE_CUSTOMER_SERVICE", "客服人员", "客服", "咨询接待人员", "4"] }
]
```

### 2. 注册页面 (`front/src/views/regist.vue`)
```html
<Option :value="1">管理员</Option>
<Option :value="2">电池出售方</Option>
<Option :value="3">电池买入方</Option>
<Option :value="4">客服人员</Option>
```

### 3. 用户编辑页面 (`front/src/views/roster/user/addEdit.vue`)
```html
<Option :value="1">管理员</Option>
<Option :value="2">电池出售方</Option>
<Option :value="3">电池买入方</Option>
<Option :value="4">客服人员</Option>
```

## 后端实现

### 1. UserController 类型映射 (`UserController.java`)

#### getMappedRoleByUserType() 方法
- type=1 → 查询名称包含 "ROLE_ADMIN" 或 "管理员" 的角色
- type=2 → 查询描述包含 "电池出售方" 或 "出售方" 的角色
- type=3 → 查询描述包含 "电池买入方" 或 "买入方" 的角色
- type=4 → 查询名称包含 "ROLE_SERVICE"/"ROLE_CUSTOMER_SERVICE" 或描述包含 "客服"/"咨询" 的角色

#### getUserTypeName() 方法
返回用户类型对应的中文名称

### 2. PermissionController 权限查询 (`PermissionController.java`)

#### getPermissionByUserId() 方法
- type=1 → 查询 "ROLE_ADMIN" 或 "管理员" 角色的权限
- type=2 → 查询 "电池出售方" 或 "出售方" 角色的权限
- type=3 → 查询 "电池买入方" 或 "买入方" 角色的权限
- type=4 → 查询 "ROLE_USER" 或 "客服人员" 角色的权限

## 数据库配置

### a_user 表 - type 字段
```sql
ALTER TABLE `a_user` MODIFY COLUMN `type` int DEFAULT NULL COMMENT '用户类型: 1=管理员, 2=电池出售方, 3=电池买入方, 4=客服人员';
```

### 角色配置要求 (a_role 表)

系统需要以下角色存在：

| 角色名称 | 角色描述 | 用户类型 |
|---------|--------|---------|
| ROLE_ADMIN | 管理员 | 1 |
| ROLE_SELLER | 电池出售方/出售方 | 2 |
| ROLE_BUYER | 电池买入方/买入方 | 3 |
| ROLE_SERVICE 或 ROLE_CUSTOMER_SERVICE | 客服人员 | 4 |

### 权限配置要求 (a_role_permission 表)

每个角色需在 a_role_permission 表中配置其拥有的菜单权限（使用打勾标记）。

- **客服人员权限配置**：在权限管理界面，为 ROLE_SERVICE/ROLE_CUSTOMER_SERVICE 角色勾选其可访问的菜单项
- **权限查询流程**：
  1. 用户登录时，系统根据 user.type 查询对应的角色
  2. 调用 `/permission/getMenuList` 接口
  3. 后端根据 a_role_permission 返回该角色拥有的菜单（只返回打勾的项）
  4. 前端直接显示返回的菜单，不进行任何硬编码过滤

## 同步检查清单

- [x] 前端登录页面支持 4 种角色
- [x] 前端注册页面支持 4 种用户类型
- [x] 前端用户编辑页面支持 4 种用户类型
- [x] 后端 UserController 支持 4 种类型的角色映射
- [x] 后端 PermissionController 支持 4 种类型的权限查询
- [x] 前端权限菜单完全依赖后端返回（不做硬编码过滤）
- [ ] 数据库中验证所有必需的角色已配置
- [ ] 数据库中为客服角色配置了适当的菜单权限

## 新用户类型添加步骤

如需添加新的用户类型，请按以下步骤执行：

1. **定义新类型**：选择未使用的 type 值（如 type=5）
2. **前端更新**：
   - 在 `login.vue` 的 roleOptions 中添加新选项
   - 在 `regist.vue` 中添加新选项
   - 在 `addEdit.vue` 中添加新选项
3. **后端更新**：
   - 修改 `UserController.getMappedRoleByUserType()` 添加新类型的条件
   - 修改 `UserController.getUserTypeName()` 返回新类型的名称
   - 修改 `PermissionController.getPermissionByUserId()` 添加新类型的查询
4. **数据库配置**：
   - 在 a_role 表中创建对应的角色记录
   - 在 a_role_permission 表中配置该角色的权限
5. **编译验证**：前后端都进行编译测试

---
**最后更新**：2026年5月12日
**维护人员**：系统管理员
