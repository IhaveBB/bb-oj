package com.nicebao.system.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nicebao.system.domain.user.User;
import com.nicebao.system.domain.user.dto.UserQueryDTO;
import com.nicebao.system.domain.user.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * UserMapper
 * <p>
 *
 * @author IhaveBB
 * @date 2025/3/25
 */
public interface UserMapper extends BaseMapper<User> {
	List<UserVO> selectUserList(UserQueryDTO userQueryDTO);
}
