package cn.zyc.res.serviceimpl;

import cn.zyc.res.mapper.ResourceMapper;
import cn.zyc.res.entity.Resource;
import cn.zyc.res.service.IResourceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 电池品类 服务层接口实现
 * @author 张奕昌
 */
@Slf4j
@Service
@Transactional
public class IResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements IResourceService {

    @Autowired
    private ResourceMapper resourceMapper;
}