package cn.zyc.data.serviceimpl;

import cn.zyc.data.dao.mapper.SettingMapper;
import cn.zyc.data.entity.Setting;
import cn.zyc.data.service.ISettingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

 
@Service
public class ISettingServiceImpl extends ServiceImpl<SettingMapper, Setting> implements ISettingService {

}
