package cn.zyc.data.serviceimpl;

import cn.zyc.data.dao.mapper.LogMapper;
import cn.zyc.data.entity.Log;
import cn.zyc.data.service.ILogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

 
@Service
public class ILogServiceImpl extends ServiceImpl<LogMapper, Log> implements ILogService {

}
