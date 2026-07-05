package cn.zyc.res.controller;

import cn.zyc.basics.utils.PageUtil;
import cn.zyc.basics.utils.ResultUtil;
import cn.zyc.basics.baseVo.PageVo;
import cn.zyc.basics.baseVo.Result;
import cn.zyc.data.utils.ZwzNullUtils;
import cn.zyc.res.entity.ResourceOrganization;
import cn.zyc.res.service.IResourceOrganizationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

 
@Slf4j
@RestController
@Api(tags = "电池回收机构管理接口")
@RequestMapping("/zyc/resourceOrganization")
@Transactional
public class ResourceOrganizationController {

    @Autowired
    private IResourceOrganizationService iResourceOrganizationService;

    @RequestMapping(value = "/getOne", method = RequestMethod.GET)
    @ApiOperation(value = "查询单条电池回收机构")
    public Result<ResourceOrganization> get(@RequestParam String id){
        return new ResultUtil<ResourceOrganization>().setData(iResourceOrganizationService.getById(id));
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部电池回收机构个数")
    public Result<Long> getCount(){
        return new ResultUtil<Long>().setData(iResourceOrganizationService.count());
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部电池回收机构")
    public Result<List<ResourceOrganization>> getAll(){
        return new ResultUtil<List<ResourceOrganization>>().setData(iResourceOrganizationService.list());
    }

    @RequestMapping(value = "/getByPage", method = RequestMethod.GET)
    @ApiOperation(value = "查询电池回收机构")
    public Result<IPage<ResourceOrganization>> getByPage(@ModelAttribute ResourceOrganization resourceOrganization ,@ModelAttribute PageVo page){
        QueryWrapper<ResourceOrganization> qw = new QueryWrapper<>();
        if(!ZwzNullUtils.isNull(resourceOrganization.getTitle())) {
            qw.like("title",resourceOrganization.getTitle());
        }
        if(!ZwzNullUtils.isNull(resourceOrganization.getAddress())) {
            qw.like("address",resourceOrganization.getAddress());
        }
        IPage<ResourceOrganization> data = iResourceOrganizationService.page(PageUtil.initMpPage(page),qw);
        return new ResultUtil<IPage<ResourceOrganization>>().setData(data);
    }

    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "增改电池回收机构")
    public Result<ResourceOrganization> saveOrUpdate(ResourceOrganization resourceOrganization){
        if(iResourceOrganizationService.saveOrUpdate(resourceOrganization)){
            return new ResultUtil<ResourceOrganization>().setData(resourceOrganization);
        }
        return ResultUtil.error();
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation(value = "新增电池回收机构")
    public Result<ResourceOrganization> insert(ResourceOrganization resourceOrganization){
        iResourceOrganizationService.saveOrUpdate(resourceOrganization);
        return new ResultUtil<ResourceOrganization>().setData(resourceOrganization);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "编辑电池回收机构")
    public Result<ResourceOrganization> update(ResourceOrganization resourceOrganization){
        iResourceOrganizationService.saveOrUpdate(resourceOrganization);
        return new ResultUtil<ResourceOrganization>().setData(resourceOrganization);
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "删除电池回收机构")
    public Result<Object> delByIds(@RequestParam String[] ids){
        for(String id : ids){
            iResourceOrganizationService.removeById(id);
        }
        return ResultUtil.success();
    }
}
