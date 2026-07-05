package cn.zyc.res.serviceimpl;

import cn.zyc.res.mapper.ReceptionistMapper;
import cn.zyc.res.entity.Receptionist;
import cn.zyc.res.service.IReceptionistService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 咨询接待人员 服务层接口实现
 * @author 张奕昌
 */
@Slf4j
@Service
@Transactional
public class IReceptionistServiceImpl extends ServiceImpl<ReceptionistMapper, Receptionist> implements IReceptionistService {

    @Autowired
    private ReceptionistMapper receptionistMapper;
}