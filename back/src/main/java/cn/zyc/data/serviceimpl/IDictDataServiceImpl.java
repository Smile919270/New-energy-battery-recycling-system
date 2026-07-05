package cn.zyc.data.serviceimpl;

import cn.zyc.data.dao.mapper.DictDataMapper;
import cn.zyc.data.entity.DictData;
import cn.zyc.data.service.IDictDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

 
@Service
public class IDictDataServiceImpl extends ServiceImpl<DictDataMapper, DictData> implements IDictDataService {

}
