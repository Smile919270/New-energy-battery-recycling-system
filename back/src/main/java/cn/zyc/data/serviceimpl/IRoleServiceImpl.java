package cn.zyc.data.serviceimpl;

import cn.zyc.data.dao.mapper.RoleMapper;
import cn.zyc.data.entity.Role;
import cn.zyc.data.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

 
@Service
public class IRoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

}
