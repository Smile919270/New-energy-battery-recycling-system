package cn.zyc.data.serviceimpl;

import cn.zyc.data.dao.mapper.UserMapper;
import cn.zyc.data.entity.User;
import cn.zyc.data.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

 
@Service
public class IUserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
