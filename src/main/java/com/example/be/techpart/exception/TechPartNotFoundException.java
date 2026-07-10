package com.example.be.techpart.exception;

import com.example.be.common.exception.BusinessException;
import com.example.be.common.exception.ErrorCode;

/**
 * 요청한 기술 분류(Part)를 찾을 수 없을 때 발생한다. 공통 예외 인프라를 기반으로 404로 매핑된다.
 */
public class TechPartNotFoundException extends BusinessException {

    public TechPartNotFoundException(Long techPartId) {
        super(ErrorCode.RESOURCE_NOT_FOUND, "기술 분류를 찾을 수 없습니다. id=" + techPartId);
    }
}
