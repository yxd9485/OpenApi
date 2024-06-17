package com.fenbeitong.openapi.plugin.demo.service;

import com.fenbeitong.openapi.plugin.demo.dao.DemoDAOOpenApi;
import com.fenbeitong.openapi.plugin.demo.entity.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * Created by log.chang on 2019/12/5.
 */
@ServiceAspect
@Service
public class DbDemoService {

    @Autowired
    private DemoDAOOpenApi demoDAO;

    public Test testDao() {
        return demoDAO.getById(1);
    }

}
