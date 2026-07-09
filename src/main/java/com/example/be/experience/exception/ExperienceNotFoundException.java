package com.example.be.experience.exception;

import com.example.be.common.exception.BusinessException;
import com.example.be.common.exception.ErrorCode;

/**
 * 요청한 경력/학력 항목을 찾을 수 없을 때 발생한다. 공통 예외 인프라를 기반으로 404로 매핑된다.
 */
public class ExperienceNotFoundException extends BusinessException {

    public ExperienceNotFoundException(Long experienceId) {
        super(ErrorCode.RESOURCE_NOT_FOUND, "경력/학력 항목을 찾을 수 없습니다. id=" + experienceId);
    }
}
