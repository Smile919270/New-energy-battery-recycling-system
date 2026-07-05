package cn.zyc.data.serviceimpl;

import cn.zyc.data.dao.mapper.PermissionMapper;
import cn.zyc.data.entity.Permission;
import cn.zyc.data.service.IPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

 
@Service
public class IPermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {

}
