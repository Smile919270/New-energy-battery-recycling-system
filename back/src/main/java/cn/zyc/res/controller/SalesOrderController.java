package cn.zyc.res.controller;

import cn.hutool.core.date.DateUtil;
import cn.zyc.basics.utils.PageUtil;
import cn.zyc.basics.utils.ResultUtil;
import cn.zyc.basics.baseVo.PageVo;
import cn.zyc.basics.baseVo.Result;
import cn.zyc.basics.utils.SecurityUtil;
import cn.zyc.data.entity.User;
import cn.zyc.data.utils.ZwzNullUtils;
import cn.zyc.res.entity.Resource;
import cn.zyc.res.entity.SalesOrder;
import cn.zyc.res.entity.TransactionOrder;
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
@Api(tags = "电池出售单管理接口")
@RequestMapping("/zyc/salesOrder")
@Transactional
public class SalesOrderController {

    @Autowired
    private ISalesOrderService iSalesOrderService;

    @Autowired
    private IResourceService iResourceService;

    @Autowired
    private ITransactionOrderService iTransactionOrderService;

    @Autowired
    private SecurityUtil securityUtil;

    @RequestMapping(value = "/getOne", method = RequestMethod.GET)
    @ApiOperation(value = "查询单条电池出售单")
    public Result<SalesOrder> get(@RequestParam String id){
        return new ResultUtil<SalesOrder>().setData(iSalesOrderService.getById(id));
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部电池出售单个数")
    public Result<Long> getCount(){
        return new ResultUtil<Long>().setData(iSalesOrderService.count());
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部电池出售单")
    public Result<List<SalesOrder>> getAll(){
        return new ResultUtil<List<SalesOrder>>().setData(iSalesOrderService.list());
    }

    @RequestMapping(value = "/getNotSellAll", method = RequestMethod.GET)
    @ApiOperation(value = "查询微交易的电池出售单")
    public Result<List<SalesOrder>> getNotSellAll(){
        QueryWrapper<SalesOrder> qw = new QueryWrapper<>();
        qw.eq("status",0);
        return new ResultUtil<List<SalesOrder>>().setData(iSalesOrderService.list(qw));
    }

    @RequestMapping(value = "/getByPage", method = RequestMethod.GET)
    @ApiOperation(value = "查询电池出售单")
    public Result<IPage<SalesOrder>> getByPage(@ModelAttribute SalesOrder salesOrder ,@ModelAttribute PageVo page){
        QueryWrapper<SalesOrder> qw = new QueryWrapper<>();
        if(!ZwzNullUtils.isNull(salesOrder.getResName())) {
            qw.like("res_name",salesOrder.getResName());
        }
        if(!ZwzNullUtils.isNull(salesOrder.getReleaseName())) {
            qw.like("release_name",salesOrder.getReleaseName());
        }
        IPage<SalesOrder> data = iSalesOrderService.page(PageUtil.initMpPage(page),qw);
        return new ResultUtil<IPage<SalesOrder>>().setData(data);
    }

    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "增改电池出售单")
    public Result<SalesOrder> saveOrUpdate(SalesOrder salesOrder){
        if(iSalesOrderService.saveOrUpdate(salesOrder)){
            return new ResultUtil<SalesOrder>().setData(salesOrder);
        }
        return ResultUtil.error();
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation(value = "新增电池出售单")
    public Result<SalesOrder> insert(SalesOrder salesOrder){
        Resource resource = iResourceService.getById(salesOrder.getResId());
        if(resource == null) {
            return ResultUtil.error("电池不存在");
        }
        salesOrder.setResName(resource.getType() + "/" + resource.getTitle() + "/" + resource.getModal());
        User currUser = securityUtil.getCurrUser();
        salesOrder.setReleaseId(currUser.getId());
        salesOrder.setReleaseName(currUser.getNickname());
        salesOrder.setStatus(0);
        iSalesOrderService.saveOrUpdate(salesOrder);
        return new ResultUtil<SalesOrder>().setData(salesOrder);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "编辑电池出售单")
    public Result<SalesOrder> update(SalesOrder salesOrder){
        Resource resource = iResourceService.getById(salesOrder.getResId());
        if(resource == null) {
            return ResultUtil.error("电池不存在");
        }
        salesOrder.setResName(resource.getType() + "/" + resource.getTitle() + "/" + resource.getModal());
        iSalesOrderService.saveOrUpdate(salesOrder);
        return new ResultUtil<SalesOrder>().setData(salesOrder);
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "删除电池出售单")
    public Result<Object> delByIds(@RequestParam String[] ids){
        for(String id : ids){
            SalesOrder order = iSalesOrderService.getById(id);
            if(order == null) {
                continue;
            }
            if(!Objects.equals(0,order.getStatus())) {
                return ResultUtil.error("已出售订单不可删除");
            }
            iSalesOrderService.removeById(id);
        }
        return ResultUtil.success();
    }

    @RequestMapping(value = "/sell", method = RequestMethod.POST)
    @ApiOperation(value = "交易订单")
    public Result<Object> sell(@RequestParam String id){
        SalesOrder order = iSalesOrderService.getById(id);
        if(order == null) {
            return ResultUtil.error("订单不存在");
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
        return ResultUtil.success();
    }
}
