package cn.zyc.res.serviceimpl;

import cn.zyc.res.mapper.PurchaseOrderMapper;
import cn.zyc.res.entity.PurchaseOrder;
import cn.zyc.res.service.IPurchaseOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 电池求购单 服务层接口实现
 * @author 张奕昌
 */
@Slf4j
@Service
@Transactional
public class IPurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> implements IPurchaseOrderService {

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;
}