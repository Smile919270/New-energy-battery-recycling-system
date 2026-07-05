package cn.zyc.data.serviceimpl;

import cn.zyc.data.dao.mapper.RolePermissionMapper;
import cn.zyc.data.entity.RolePermission;
import cn.zyc.data.service.IRolePermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

 
@Service
public class IRolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements IRolePermissionService {

}
