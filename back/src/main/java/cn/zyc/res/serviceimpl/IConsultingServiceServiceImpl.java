package cn.zyc.res.serviceimpl;

import cn.zyc.res.mapper.ConsultingServiceMapper;
import cn.zyc.res.entity.ConsultingService;
import cn.zyc.res.service.IConsultingServiceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 电池回收咨询 服务层接口实现
 * @author 张奕昌
 */
@Slf4j
@Service
@Transactional
public class IConsultingServiceServiceImpl extends ServiceImpl<ConsultingServiceMapper, ConsultingService> implements IConsultingServiceService {

    @Autowired
    private ConsultingServiceMapper consultingServiceMapper;
}