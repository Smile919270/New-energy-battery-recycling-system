package cn.zyc.data.serviceimpl;

import cn.zyc.data.dao.mapper.FileMapper;
import cn.zyc.data.entity.File;
import cn.zyc.data.service.IFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

 
@Service
public class IFileServiceImpl extends ServiceImpl<FileMapper, File> implements IFileService {

}
