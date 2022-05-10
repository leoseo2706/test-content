package com.fiats.content.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiats.tmgcoreutils.constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ContGroupDocHistoryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long templateId;

    private Long versionId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.FORMAT_SQLSERVER_SHORT, timezone = Constant.TIMEZONE_ICT)
    private Timestamp appliedDate;

    private Long makerId;

    private Long checkerId;

    private Integer status;

    private Integer  activeVersion;

}
