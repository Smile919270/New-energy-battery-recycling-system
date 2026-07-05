package cn.zyc.test.serviceimpl;

import cn.zyc.test.mapper.StudentMapper;
import cn.zyc.test.entity.Student;
import cn.zyc.test.service.IStudentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

 
@Slf4j
@Service
@Transactional
public class IStudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements IStudentService {

    @Autowired
    private StudentMapper studentMapper;
}