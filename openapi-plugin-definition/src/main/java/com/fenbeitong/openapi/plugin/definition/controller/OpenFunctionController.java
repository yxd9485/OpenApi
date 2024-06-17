package com.fenbeitong.openapi.plugin.definition.controller;

import com.fenbeitong.openapi.plugin.definition.dto.DefinitionResultDTO;
import com.fenbeitong.openapi.plugin.definition.dto.function.OpenFunctionReqDTO;
import com.fenbeitong.openapi.plugin.definition.service.OpenFunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;


/**   
 * 功能
 * Created by lizhen on 2020/01/13.
 */
@Controller
@RequestMapping("/definitions/function/openFunction")
public class OpenFunctionController {

	@Autowired
	public OpenFunctionService openFunctionService;
	
	/**
	 * 添加功能
	 */
	@PostMapping
    @ResponseBody
	public Object createOpenFunction(@Valid @RequestBody OpenFunctionReqDTO req) {
        return DefinitionResultDTO.success(openFunctionService.createOpenFunction(req));
    }

	/**
	 * 获取所有的功能
	 */
	@GetMapping
	@ResponseBody
	public Object getOpenFunction() {
		return DefinitionResultDTO.success(openFunctionService.getOpenFunction());
	}

}