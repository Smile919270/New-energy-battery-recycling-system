package cn.zyc.res.controller;

import cn.hutool.core.date.DateUtil;
import cn.zyc.basics.utils.PageUtil;
import cn.zyc.basics.utils.ResultUtil;
import cn.zyc.basics.baseVo.PageVo;
import cn.zyc.basics.baseVo.Result;
import cn.zyc.basics.utils.SecurityUtil;
import cn.zyc.data.entity.User;
import cn.zyc.data.utils.ZwzNullUtils;
import cn.zyc.res.entity.PurchaseOrder;
import cn.zyc.res.entity.Resource;
import cn.zyc.res.entity.SalesOrder;
import cn.zyc.res.entity.TransactionOrder;
import cn.zyc.res.service.IPurchaseOrderService;
import cn.zyc.res.service.IResourceService;
import cn.zyc.res.service.ISalesOrderService;
import cn.zyc.res.service.ITransactionOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

 
@Slf4j
@RestController
@Api(tags = "电池交易单管理接口")
@RequestMapping("/zyc/transactionOrder")
@Transactional
public class TransactionOrderController {

    @Autowired
    private ITransactionOrderService iTransactionOrderService;

    @Autowired
    private ISalesOrderService iSalesOrderService;

    @Autowired
    private IPurchaseOrderService iPurchaseOrderService;

    @Autowired
    private IResourceService iResourceService;

    @Autowired
    private SecurityUtil securityUtil;

    @RequestMapping(value = "/getOne", method = RequestMethod.GET)
    @ApiOperation(value = "查询单条电池交易单")
    public Result<TransactionOrder> get(@RequestParam String id){
        return new ResultUtil<TransactionOrder>().setData(iTransactionOrderService.getById(id));
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部电池交易单个数")
    public Result<Long> getCount(){
        return new ResultUtil<Long>().setData(iTransactionOrderService.count());
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部电池交易单")
    public Result<List<TransactionOrder>> getAll(){
        return new ResultUtil<List<TransactionOrder>>().setData(iTransactionOrderService.list());
    }

    @RequestMapping(value = "/getByPage", method = RequestMethod.GET)
    @ApiOperation(value = "查询电池交易单")
    public Result<IPage<TransactionOrder>> getByPage(@ModelAttribute TransactionOrder transactionOrder ,@ModelAttribute PageVo page){
        QueryWrapper<TransactionOrder> qw = new QueryWrapper<>();
        if(!ZwzNullUtils.isNull(transactionOrder.getResName())) {
            qw.like("res_name",transactionOrder.getResName());
        }
        if(!ZwzNullUtils.isNull(transactionOrder.getReleaseName())) {
            qw.like("release_name",transactionOrder.getReleaseName());
        }
        IPage<TransactionOrder> data = iTransactionOrderService.page(PageUtil.initMpPage(page),qw);
        return new ResultUtil<IPage<TransactionOrder>>().setData(data);
    }

    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "增改电池交易单")
    public Result<TransactionOrder> saveOrUpdate(TransactionOrder transactionOrder){
        if(iTransactionOrderService.saveOrUpdate(transactionOrder)){
            return new ResultUtil<TransactionOrder>().setData(transactionOrder);
        }
        return ResultUtil.error();
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation(value = "新增电池交易单")
    public Result<TransactionOrder> insert(TransactionOrder transactionOrder){
        iTransactionOrderService.saveOrUpdate(transactionOrder);
        return new ResultUtil<TransactionOrder>().setData(transactionOrder);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "编辑电池交易单")
    public Result<TransactionOrder> update(TransactionOrder transactionOrder){
        iTransactionOrderService.saveOrUpdate(transactionOrder);
        return new ResultUtil<TransactionOrder>().setData(transactionOrder);
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "删除电池交易单")
    public Result<Object> delByIds(@RequestParam String[] ids){
        for(String id : ids){
            TransactionOrder tOrder = iTransactionOrderService.getById(id);
            if(tOrder != null && !ZwzNullUtils.isNull(tOrder.getResId())) {
                Resource resource = iResourceService.getById(tOrder.getResId());
                if(resource != null && tOrder.getNumber() != null) {
                    BigDecimal sellNumber = resource.getSellNumber() == null ? BigDecimal.ZERO : resource.getSellNumber();
                    resource.setSellNumber(sellNumber.subtract(tOrder.getNumber()));
                    iResourceService.saveOrUpdate(resource);
                }
            }
            iTransactionOrderService.removeById(id);
        }
        return ResultUtil.success();
    }

    @RequestMapping(value = "/addOrder", method = RequestMethod.POST)
    @ApiOperation(value = "快速新增交易单")
    public Result<Object> addOrder(@RequestParam String type,@RequestParam String orderId){
        if(Objects.equals("0",type)) {
            PurchaseOrder order = iPurchaseOrderService.getById(orderId);
            if(order == null) {
                return ResultUtil.error("求购单不存在");
            }
            // 新增交易单
            TransactionOrder tOrder = new TransactionOrder();
            tOrder.setType(1);
            tOrder.setOrderId(order.getId());
            tOrder.setResId(order.getResId());
            tOrder.setResName(order.getResName());
            tOrder.setReleaseId(order.getReleaseId());
            tOrder.setReleaseName(order.getReleaseName());
            tOrder.setNumber(order.getNumber());
            tOrder.setPrice(order.getPrice());
            tOrder.setContent(order.getContent());
            User currUser = securityUtil.getCurrUser();
            tOrder.setFinishId(currUser.getId());
            tOrder.setFinishName(currUser.getNickname());
            tOrder.setFinishTime(DateUtil.now());
            iTransactionOrderService.saveOrUpdate(tOrder);
            // 累加电池品类交易量
            if(!ZwzNullUtils.isNull(order.getResId())) {
                Resource resource = iResourceService.getById(order.getResId());
                if(resource != null && order.getNumber() != null) {
                    BigDecimal sellNumber = resource.getSellNumber() == null ? BigDecimal.ZERO : resource.getSellNumber();
                    resource.setSellNumber(sellNumber.add(order.getNumber()));
                    iResourceService.saveOrUpdate(resource);
                }
            }
            // 持久化
            order.setStatus(1);
            iPurchaseOrderService.saveOrUpdate(order);
        } else {
            SalesOrder order = iSalesOrderService.getById(orderId);
            if(order == null) {
                return ResultUtil.error("销售单不存在");
            }
            // 新增交易单
            TransactionOrder tOrder = new TransactionOrder();
            tOrder.setType(1);
            tOrder.setOrderId(order.getId());
            tOrder.setResId(order.getResId());
            tOrder.setResName(order.getResName());
            tOrder.setReleaseId(order.getReleaseId());
            tOrder.setReleaseName(order.getReleaseName());
            tOrder.setNumber(order.getNumber());
            tOrder.setPrice(order.getPrice());
            tOrder.setContent(order.getContent());
            User currUser = securityUtil.getCurrUser();
            tOrder.setFinishId(currUser.getId());
            tOrder.setFinishName(currUser.getNickname());
            tOrder.setFinishTime(DateUtil.now());
            iTransactionOrderService.saveOrUpdate(tOrder);
            // 累加电池品类交易量
            if(!ZwzNullUtils.isNull(order.getResId())) {
                Resource resource = iResourceService.getById(order.getResId());
                if(resource != null && order.getNumber() != null) {
                    BigDecimal sellNumber = resource.getSellNumber() == null ? BigDecimal.ZERO : resource.getSellNumber();
                    resource.setSellNumber(sellNumber.add(order.getNumber()));
                    iResourceService.saveOrUpdate(resource);
                }
            }
            // 持久化
            order.setStatus(1);
            iSalesOrderService.saveOrUpdate(order);
        }
        return ResultUtil.success();
    }
}
