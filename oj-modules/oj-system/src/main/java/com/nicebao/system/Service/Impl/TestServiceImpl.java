package com.nicebao.system.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nicebao.system.Service.TestService;
import com.nicebao.system.domain.TestDomain;
import com.nicebao.system.mapper.TestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TestServiceImpl
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/18
 */
@Service
public class TestServiceImpl implements TestService {
	@Autowired
	private TestMapper testMapper;

	@Override
	public List<TestDomain> list() {
		List<TestDomain> testDomains = testMapper.selectList(new LambdaQueryWrapper<>());
		System.out.printf("");
		return testDomains;
	}
}
