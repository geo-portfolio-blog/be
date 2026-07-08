package com.example.be.project.exception;

import com.example.be.common.exception.BusinessException;
import com.example.be.common.exception.ErrorCode;

/**
 * 요청한 프로젝트를 찾을 수 없을 때 발생한다. 공통 예외 인프라를 기반으로 404로 매핑된다.
 */
public class ProjectNotFoundException extends BusinessException {

    public ProjectNotFoundException(Long projectId) {
        super(ErrorCode.RESOURCE_NOT_FOUND, "프로젝트를 찾을 수 없습니다. id=" + projectId);
    }
}
