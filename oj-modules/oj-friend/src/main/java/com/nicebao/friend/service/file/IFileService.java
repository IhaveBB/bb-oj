package com.nicebao.friend.service.file;

import com.nicebao.common.file.domain.OSSResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * IFileService
 * <p>
 *
 * @author IhaveBB
 * @date 2025/4/13
 */
public interface IFileService {
	OSSResult upload(MultipartFile file);
}

