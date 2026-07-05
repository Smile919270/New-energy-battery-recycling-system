package cn.zyc.data.serviceimpl;

import cn.zyc.data.dao.mapper.DictMapper;
import cn.zyc.data.entity.Dict;
import cn.zyc.data.service.IDictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

 
@Service
public class IDictServiceImpl extends ServiceImpl<DictMapper, Dict> implements IDictService {

}
