package cn.zyc.res.controller;

import cn.zyc.basics.utils.PageUtil;
import cn.zyc.basics.utils.ResultUtil;
import cn.zyc.basics.baseVo.PageVo;
import cn.zyc.basics.baseVo.Result;
import cn.zyc.data.utils.ZwzNullUtils;
import cn.zyc.res.entity.ResourceType;
import cn.zyc.res.service.IResourceTypeService;
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
@Api(tags = "电池类型管理接口")
@RequestMapping("/zyc/resourceType")
@Transactional
public class ResourceTypeController {

    @Autowired
    private IResourceTypeService iResourceTypeService;

    @RequestMapping(value = "/getOne", method = RequestMethod.GET)
    @ApiOperation(value = "查询单条电池类型")
    public Result<ResourceType> get(@RequestParam String id){
        return new ResultUtil<ResourceType>().setData(iResourceTypeService.getById(id));
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部电池类型个数")
    public Result<Long> getCount(){
        return new ResultUtil<Long>().setData(iResourceTypeService.count());
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "查询全部电池类型")
    public Result<List<ResourceType>> getAll(){
        return new ResultUtil<List<ResourceType>>().setData(iResourceTypeService.list());
    }

    @RequestMapping(value = "/getByPage", method = RequestMethod.GET)
    @ApiOperation(value = "查询电池类型")
    public Result<IPage<ResourceType>> getByPage(@ModelAttribute ResourceType resourceType ,@ModelAttribute PageVo page){
        QueryWrapper<ResourceType> qw = new QueryWrapper<>();
        if(!ZwzNullUtils.isNull(resourceType.getTitle())) {
            qw.like("title",resourceType.getTitle());
        }
        IPage<ResourceType> data = iResourceTypeService.page(PageUtil.initMpPage(page),qw);
        return new ResultUtil<IPage<ResourceType>>().setData(data);
    }

    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "增改电池类型")
    public Result<ResourceType> saveOrUpdate(ResourceType resourceType){
        if(iResourceTypeService.saveOrUpdate(resourceType)){
            return new ResultUtil<ResourceType>().setData(resourceType);
        }
        return ResultUtil.error();
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation(value = "新增电池类型")
    public Result<ResourceType> insert(ResourceType resourceType){
        iResourceTypeService.saveOrUpdate(resourceType);
        return new ResultUtil<ResourceType>().setData(resourceType);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "编辑电池类型")
    public Result<ResourceType> update(ResourceType resourceType){
        iResourceTypeService.saveOrUpdate(resourceType);
        return new ResultUtil<ResourceType>().setData(resourceType);
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "删除电池类型")
    public Result<Object> delByIds(@RequestParam String[] ids){
        for(String id : ids){
            iResourceTypeService.removeById(id);
        }
        return ResultUtil.success();
    }
}
