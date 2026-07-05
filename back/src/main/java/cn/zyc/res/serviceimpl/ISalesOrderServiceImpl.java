package cn.zyc.res.serviceimpl;

import cn.zyc.res.mapper.SalesOrderMapper;
import cn.zyc.res.entity.SalesOrder;
import cn.zyc.res.service.ISalesOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 电池出售单 服务层接口实现
 * @author 张奕昌
 */
@Slf4j
@Service
@Transactional
public class ISalesOrderServiceImpl extends ServiceImpl<SalesOrderMapper, SalesOrder> implements ISalesOrderService {

    @Autowired
    private SalesOrderMapper salesOrderMapper;
}