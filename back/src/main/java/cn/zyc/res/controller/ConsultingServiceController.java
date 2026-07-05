package cn.zyc.res.controller;

import cn.hutool.core.date.DateUtil;
import cn.zyc.basics.utils.PageUtil;
import cn.zyc.basics.utils.ResultUtil;
import cn.zyc.basics.baseVo.PageVo;
import cn.zyc.basics.baseVo.Result;
import cn.zyc.basics.utils.SecurityUtil;
import cn.zyc.data.entity.User;
import cn.zyc.data.utils.ZwzNullUtils;
import cn.zyc.res.entity.ConsultingService;
import cn.zyc.res.entity.Receptionist;
import cn.zyc.res.service.IConsultingServiceService;
import cn.zyc.res.service.IReceptionistService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

 
@Slf4j
@RestController
@Api(tags = "电池回收咨询管理接口")
@RequestMapping("/zyc/consultingService")
@Transactional
public class ConsultingServiceController {

    @Autowired
    private IConsultingServiceService iConsultingServiceService;

    @Autowired
    private IReceptionistService iReceptionistService;

    @Autowired
    private SecurityUtil securityUtil;

    @RequestMapping(value = "/getOne", method = RequestMethod.GET)
    @ApiOperation(value = "查询单条电池回收咨询")
    public Result<ConsultingService> get(@RequestParam String id){
        return new ResultUtil<ConsultingService>().setData(iConsultingServiceService.getById(id));
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部电池回收咨询个数")
    public Result<Long> getCount(){
        return new ResultUtil<Long>().setData(iConsultingServiceService.count());
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部电池回收咨询")
    public Result<List<ConsultingService>> getAll(){
        return new ResultUtil<List<ConsultingService>>().setData(iConsultingServiceService.list());
    }

    @RequestMapping(value = "/getByPage", method = RequestMethod.GET)
    @ApiOperation(value = "查询电池回收咨询")
    public Result<IPage<ConsultingService>> getByPage(@ModelAttribute ConsultingService consultingService ,@ModelAttribute PageVo page){
        QueryWrapper<ConsultingService> qw = new QueryWrapper<>();
        if(!ZwzNullUtils.isNull(consultingService.getTitle())) {
            qw.like("title",consultingService.getTitle());
        }
        if(!ZwzNullUtils.isNull(consultingService.getContent())) {
            qw.like("content",consultingService.getContent());
        }
        IPage<ConsultingService> data = iConsultingServiceService.page(PageUtil.initMpPage(page),qw);
        return new ResultUtil<IPage<ConsultingService>>().setData(data);
    }

    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "增改电池回收咨询")
    public Result<ConsultingService> saveOrUpdate(ConsultingService consultingService){
        if(iConsultingServiceService.saveOrUpdate(consultingService)){
            return new ResultUtil<ConsultingService>().setData(consultingService);
        }
        return ResultUtil.error();
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation(value = "新增电池回收咨询")
    public Result<ConsultingService> insert(ConsultingService consultingService){
        User currUser = securityUtil.getCurrUser();
        consultingService.setUserId(currUser.getId());
        consultingService.setUserName(currUser.getNickname());
        consultingService.setReplyFlag(false);
        consultingService.setReplyId("");
        consultingService.setReplyName("");
        consultingService.setReplyContent("");
        consultingService.setReplyTime("");
        iConsultingServiceService.saveOrUpdate(consultingService);
        return new ResultUtil<ConsultingService>().setData(consultingService);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "编辑电池回收咨询")
    public Result<ConsultingService> update(ConsultingService consultingService){
        iConsultingServiceService.saveOrUpdate(consultingService);
        return new ResultUtil<ConsultingService>().setData(consultingService);
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "删除电池回收咨询")
    public Result<Object> delByIds(@RequestParam String[] ids){
        for(String id : ids){
            ConsultingService cs = iConsultingServiceService.getById(id);
            if(cs == null) {
                continue;
            }
            if(Objects.equals(true,cs.getReplyFlag())) {
                return ResultUtil.error("不能删除已回复的咨询");
            }
            iConsultingServiceService.removeById(id);
        }
        return ResultUtil.success();
    }

    @RequestMapping(value = "/reply", method = RequestMethod.POST)
    @ApiOperation(value = "回复咨询")
    public Result<Object> reply(@RequestParam String dataId,@RequestParam String userId,@RequestParam String content){
        ConsultingService cs = iConsultingServiceService.getById(dataId);
        if(cs == null) {
            return ResultUtil.error("咨询不存在");
        }
        if(Objects.equals(true,cs.getReplyFlag())) {
            return ResultUtil.error("咨询已被回复");
        }
        Receptionist receptionist = iReceptionistService.getById(userId);
        if(receptionist == null) {
            return ResultUtil.error("接待人员不存在");
        }
        cs.setReplyFlag(true);
        cs.setReplyId(receptionist.getId());
        cs.setReplyName(receptionist.getName());
        cs.setReplyContent(content);
        cs.setReplyTime(DateUtil.now());
        iConsultingServiceService.saveOrUpdate(cs);
        return ResultUtil.success();
    }
}
