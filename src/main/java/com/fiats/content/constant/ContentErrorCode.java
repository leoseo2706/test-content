package com.fiats.content.constant;

import com.neo.exception.INeoErrorCode;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;

public enum ContentErrorCode implements INeoErrorCode {
    SERVER_ERROR("100", "content.error", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    DOC_TEMPLATE_CODE_DUPLICATE("101", "content.doc.template.code.duplicate", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    DOC_GROUP_CODE_DUPLICATE("102", "content.doc.group.code.duplicate", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    TEMPLATE_CONTENT_EMPTY("103", "content.template.content.empty", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    CACHE_EVICT_PAYLOAD_EMPTY("104", "content.cache.evict.payload.empty", HttpStatus.BAD_REQUEST.value()),
    ;


    private String code;

    private String messageCode;
    private Integer httpStatus;

    ContentErrorCode(String errorCode, String messageCode) {
        this.code = errorCode;
        this.messageCode = messageCode;
        this.httpStatus = HttpServletResponse.SC_BAD_REQUEST;
    }

    ContentErrorCode(String errorCode, String messageCode, Integer httpStatus) {
        this.code = errorCode;
        this.messageCode = messageCode;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }
}
