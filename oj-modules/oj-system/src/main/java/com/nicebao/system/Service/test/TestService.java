package com.nicebao.system.Service.test;

import com.nicebao.system.domain.TestDomain;

import java.util.List;

/**
 * TestService
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/18
 */
public interface TestService {
	List<TestDomain> list();

	String add();
}
