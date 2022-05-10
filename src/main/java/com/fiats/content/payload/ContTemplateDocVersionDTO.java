package com.fiats.content.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ContTemplateDocVersionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long templateId;

    private String templateName;

    private String templateCode;

    private String content;

    private Integer version;

    private Long makerId;

    private Long checkerId;

    private Integer status;

}
