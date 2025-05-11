package com.nicebao.friend.service.file.impl;

import com.nicebao.common.core.enums.ResultCode;
import com.nicebao.common.file.domain.OSSResult;
import com.nicebao.common.file.service.OSSService;
import com.nicebao.common.security.exception.ServiceException;
import com.nicebao.friend.service.file.IFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * FileServiceImpl
 * <p>
 *
 * @author IhaveBB
 * @date 2025/4/13
 */
@Service
@Slf4j
public class FileServiceImpl implements IFileService {

	@Autowired
	private OSSService ossService;

	@Override
	public OSSResult upload(MultipartFile file) {
		try {
			return ossService.uploadFile(file);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ServiceException(ResultCode.FAILED_FILE_UPLOAD);
		}
	}
}
