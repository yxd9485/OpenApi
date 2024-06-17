package com.fenbeitong.openapi.plugin.definition.controller;

import com.fenbeitong.openapi.plugin.definition.dto.DefinitionResultDTO;
import com.fenbeitong.openapi.plugin.definition.dto.function.OpenGroupReqDTO;
import com.fenbeitong.openapi.plugin.definition.service.OpenGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**   
 * 功能簇
 * Created by lizhen on 2020/01/10.
 */
@Controller
@RequestMapping("/definitions/function/openGroup")
public class OpenGroupController {

	@Autowired
	public OpenGroupService openGroupService;
	
	/**
	 * 添加功能簇
	 */
	@PostMapping
    @ResponseBody
	public Object createOpenGroup(@Valid @RequestBody OpenGroupReqDTO req) {
        return DefinitionResultDTO.success(openGroupService.createOpenGroup(req));
    }

	/**
	 * 获取所有的功能簇
	 */
	@GetMapping
	@ResponseBody
	public Object getOpenGroup() {
		return DefinitionResultDTO.success(openGroupService.getOpenGroup());
	}

}