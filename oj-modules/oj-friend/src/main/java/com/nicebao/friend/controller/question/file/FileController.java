package com.nicebao.friend.controller.question.file;

import com.nicebao.common.core.controller.BaseController;
import com.nicebao.common.core.domain.R;
import com.nicebao.common.file.domain.OSSResult;
import com.nicebao.friend.service.file.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController extends BaseController {

    @Autowired
    private IFileService sysFileService;

    @PostMapping("/upload")
    public R<OSSResult> upload(@RequestBody MultipartFile file) {
        return R.ok(sysFileService.upload(file));
    }
}