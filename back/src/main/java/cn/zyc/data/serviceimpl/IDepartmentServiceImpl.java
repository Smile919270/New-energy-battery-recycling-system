package cn.zyc.data.serviceimpl;

import cn.zyc.data.dao.mapper.DepartmentMapper;
import cn.zyc.data.entity.Department;
import cn.zyc.data.service.IDepartmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

 
@Service
public class IDepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements IDepartmentService {

}
