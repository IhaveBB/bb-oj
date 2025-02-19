package com.nicebao.system.controller;
import com.nicebao.system.Service.test.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {
	@Autowired
	private TestService testService;
	@GetMapping("/list")
	public List<?> list() {
		return testService.list();
	}

	@PostMapping("/add")
	public String add() {
		return testService.add();
	}
}
