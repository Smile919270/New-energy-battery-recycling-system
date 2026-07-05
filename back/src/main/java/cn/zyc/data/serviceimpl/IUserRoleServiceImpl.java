package cn.zyc.data.serviceimpl;

import cn.zyc.data.dao.mapper.UserRoleMapper;
import cn.zyc.data.entity.UserRole;
import cn.zyc.data.service.IUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

 
@Service
public class IUserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

}
