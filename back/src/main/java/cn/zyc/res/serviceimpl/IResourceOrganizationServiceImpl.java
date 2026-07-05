package cn.zyc.res.serviceimpl;

import cn.zyc.res.mapper.ResourceOrganizationMapper;
import cn.zyc.res.entity.ResourceOrganization;
import cn.zyc.res.service.IResourceOrganizationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 电池回收机构 服务层接口实现
 * @author 张奕昌
 */
@Slf4j
@Service
@Transactional
public class IResourceOrganizationServiceImpl extends ServiceImpl<ResourceOrganizationMapper, ResourceOrganization> implements IResourceOrganizationService {

    @Autowired
    private ResourceOrganizationMapper resourceOrganizationMapper;
}