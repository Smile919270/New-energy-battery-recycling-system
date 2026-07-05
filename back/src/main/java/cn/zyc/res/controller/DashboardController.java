package cn.zyc.res.controller;

import cn.hutool.core.date.DateUtil;
import cn.zyc.basics.utils.ResultUtil;
import cn.zyc.basics.baseVo.Result;
import cn.zyc.res.entity.*;
import cn.zyc.res.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * 首页仪表盘数据接口
 * @author 张奕昌
 */
@Slf4j
@RestController
@Api(tags = "首页仪表盘接口")
@RequestMapping("/zyc/dashboard")
public class DashboardController {

    @Autowired
    private IResourceService iResourceService;

    @Autowired
    private IResourceTypeService iResourceTypeService;

    @Autowired
    private IPurchaseOrderService iPurchaseOrderService;

    @Autowired
    private ISalesOrderService iSalesOrderService;

    @Autowired
    private ITransactionOrderService iTransactionOrderService;

    @Autowired
    private IConsultingServiceService iConsultingServiceService;

    @Autowired
    private IResourceOrganizationService iResourceOrganizationService;

    @RequestMapping(value = "/indexData", method = RequestMethod.GET)
    @ApiOperation(value = "获取首页仪表盘数据")
    public Result<Map<String, Object>> getIndexData() {
        Map<String, Object> result = new HashMap<>(16);

        // ===== 1. 顶部统计卡片 =====

        // 电池总数（电池品类总数）
        long totalBatteries = iResourceService.count(new QueryWrapper<Resource>().eq("del_flag", 0));
        result.put("totalBatteries", totalBatteries);

        // 今日入库（今天创建的电池品类数）
        Date todayStart = DateUtil.beginOfDay(new Date());
        Date todayEnd = DateUtil.endOfDay(new Date());
        long todayInbound = iResourceService.count(new QueryWrapper<Resource>()
                .eq("del_flag", 0)
                .between("create_time", todayStart, todayEnd));
        result.put("todayInbound", todayInbound);

        // 求购单总数
        long totalPurchaseOrders = iPurchaseOrderService.count(new QueryWrapper<PurchaseOrder>()
                .eq("del_flag", 0));
        result.put("totalPurchaseOrders", totalPurchaseOrders);

        // 出售单总数
        long totalSalesOrders = iSalesOrderService.count(new QueryWrapper<SalesOrder>()
                .eq("del_flag", 0));
        result.put("totalSalesOrders", totalSalesOrders);

        // 本月回收额（本月完成的交易单总金额）
        Date monthStart = DateUtil.beginOfMonth(new Date());
        Date monthEnd = DateUtil.endOfMonth(new Date());
        List<TransactionOrder> monthTransactions = iTransactionOrderService.list(
                new QueryWrapper<TransactionOrder>()
                        .eq("del_flag", 0)
                        .between("create_time", monthStart, monthEnd));
        BigDecimal monthlyAmount = BigDecimal.ZERO;
        for (TransactionOrder order : monthTransactions) {
            if (order.getPrice() != null && order.getNumber() != null) {
                monthlyAmount = monthlyAmount.add(order.getPrice().multiply(order.getNumber()));
            }
        }
        result.put("monthlyAmount", monthlyAmount);

        // ===== 2. 电池类型分布（饼图 - 按电池类型分组统计） =====
        List<Resource> allResources = iResourceService.list(new QueryWrapper<Resource>().eq("del_flag", 0));
        List<Map<String, Object>> typeDistribution = new ArrayList<>();
        Map<String, Integer> typeCountMap = new LinkedHashMap<>();
        for (Resource r : allResources) {
            String typeName = r.getType() != null && !r.getType().isEmpty() ? r.getType() : "其他";
            typeCountMap.put(typeName, typeCountMap.getOrDefault(typeName, 0) + 1);
        }
        for (Map.Entry<String, Integer> entry : typeCountMap.entrySet()) {
            Map<String, Object> item = new HashMap<>(4);
            item.put("type", entry.getKey());
            item.put("value", entry.getValue());
            typeDistribution.add(item);
        }
        result.put("typeDistribution", typeDistribution);

        // ===== 3. 电池状态分布（折线图 - 按新旧程度分组） =====
        List<Map<String, Object>> statusDistribution = new ArrayList<>();
        Map<String, Integer> degreeCountMap = new LinkedHashMap<>();
        for (Resource r : allResources) {
            String degree = r.getDegree() != null ? r.getDegree() : "其他";
            degreeCountMap.put(degree, degreeCountMap.getOrDefault(degree, 0) + 1);
        }
        for (Map.Entry<String, Integer> entry : degreeCountMap.entrySet()) {
            Map<String, Object> item = new HashMap<>(4);
            item.put("type", entry.getKey());
            item.put("value", entry.getValue());
            statusDistribution.add(item);
        }
        result.put("statusDistribution", statusDistribution);

        // ===== 4. 安全状态区域 =====
        // 未处理报警（未回复的咨询数量）
        long unrepliedConsulting = iConsultingServiceService.count(new QueryWrapper<ConsultingService>()
                .eq("del_flag", 0)
                .eq("reply_flag", false));
        result.put("unrepliedConsulting", unrepliedConsulting);

        // 今日咨询（今天创建的咨询数量）
        long todayConsulting = iConsultingServiceService.count(new QueryWrapper<ConsultingService>()
                .eq("del_flag", 0)
                .between("create_time", todayStart, todayEnd));
        result.put("todayConsulting", todayConsulting);

        // 总资讯数量
        long totalConsulting = iConsultingServiceService.count(new QueryWrapper<ConsultingService>()
                .eq("del_flag", 0));
        result.put("totalConsulting", totalConsulting);

        // 回收机构数量
        long orgCount = iResourceOrganizationService.count(new QueryWrapper<ResourceOrganization>()
                .eq("del_flag", 0));
        result.put("orgCount", orgCount);

        // ===== 5. 待办事项 =====

        // 待处理求购单
        long pendingPurchase = iPurchaseOrderService.count(new QueryWrapper<PurchaseOrder>()
                .eq("del_flag", 0)
                .eq("status", 0));
        result.put("pendingPurchase", pendingPurchase);

        // 待处理出售单
        long pendingSales = iSalesOrderService.count(new QueryWrapper<SalesOrder>()
                .eq("del_flag", 0)
                .eq("status", 0));
        result.put("pendingSales", pendingSales);

        // 已完成交易单总数
        long completedTransactions = iTransactionOrderService.count(new QueryWrapper<TransactionOrder>()
                .eq("del_flag", 0));
        result.put("completedTransactions", completedTransactions);

        // 待回复咨询
        result.put("pendingConsulting", unrepliedConsulting);

        // ===== 6. 交易数据分析（金属价格替换为各类电池交易统计） =====
        List<Map<String, Object>> tradeAnalysis = new ArrayList<>();
        // 按电池名称统计交易量和交易金额
        List<TransactionOrder> allTransactions = iTransactionOrderService.list(
                new QueryWrapper<TransactionOrder>().eq("del_flag", 0));
        Map<String, BigDecimal[]> tradeMap = new LinkedHashMap<>();
        for (TransactionOrder t : allTransactions) {
            String name = t.getResName() != null ? t.getResName() : "其他";
            // 取电池名称的第一段（'/'之前的部分）作为简称
            if (name.contains("/")) {
                name = name.substring(0, name.indexOf("/"));
            }
            BigDecimal[] data = tradeMap.getOrDefault(name, new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
            if (t.getNumber() != null) {
                data[0] = data[0].add(t.getNumber());
            }
            if (t.getPrice() != null && t.getNumber() != null) {
                data[1] = data[1].add(t.getPrice().multiply(t.getNumber()));
            }
            tradeMap.put(name, data);
        }
        for (Map.Entry<String, BigDecimal[]> entry : tradeMap.entrySet()) {
            Map<String, Object> item = new HashMap<>(4);
            item.put("name", entry.getKey());
            item.put("number", entry.getValue()[0]);
            item.put("amount", entry.getValue()[1]);
            tradeAnalysis.add(item);
        }
        result.put("tradeAnalysis", tradeAnalysis);

        // ===== 7. 各类电池单价信息 =====
        List<Map<String, Object>> priceList = new ArrayList<>();
        for (Resource r : allResources) {
            Map<String, Object> item = new HashMap<>(4);
            item.put("name", r.getTitle());
            // 确保单价为纯数字
            BigDecimal price = r.getUnitPrice();
            if (price == null) {
                price = BigDecimal.ZERO;
            }
            item.put("unitPrice", price);
            item.put("unit", r.getUnit());
            priceList.add(item);
        }
        result.put("priceList", priceList);

        return new ResultUtil<Map<String, Object>>().setData(result);
    }
}
