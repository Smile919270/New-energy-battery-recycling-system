package cn.zyc.res.serviceimpl;

import cn.zyc.res.mapper.ResourceTypeMapper;
import cn.zyc.res.entity.ResourceType;
import cn.zyc.res.service.IResourceTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 电池类型 服务层接口实现
 * @author 张奕昌
 */
@Slf4j
@Service
@Transactional
public class IResourceTypeServiceImpl extends ServiceImpl<ResourceTypeMapper, ResourceType> implements IResourceTypeService {

    @Autowired
    private ResourceTypeMapper resourceTypeMapper;
}