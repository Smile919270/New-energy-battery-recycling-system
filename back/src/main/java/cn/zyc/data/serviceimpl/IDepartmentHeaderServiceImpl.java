package cn.zyc.data.serviceimpl;

import cn.zyc.data.dao.mapper.DepartmentHeaderMapper;
import cn.zyc.data.entity.DepartmentHeader;
import cn.zyc.data.service.IDepartmentHeaderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

 
@Service
public class IDepartmentHeaderServiceImpl extends ServiceImpl<DepartmentHeaderMapper, DepartmentHeader> implements IDepartmentHeaderService {

}
