package cn.zyc.res.serviceimpl;

import cn.zyc.res.mapper.TransactionOrderMapper;
import cn.zyc.res.entity.TransactionOrder;
import cn.zyc.res.service.ITransactionOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 电池交易单 服务层接口实现
 * @author 张奕昌
 */
@Slf4j
@Service
@Transactional
public class ITransactionOrderServiceImpl extends ServiceImpl<TransactionOrderMapper, TransactionOrder> implements ITransactionOrderService {

    @Autowired
    private TransactionOrderMapper transactionOrderMapper;
}