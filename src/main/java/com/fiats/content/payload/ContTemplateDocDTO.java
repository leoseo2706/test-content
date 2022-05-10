package com.fiats.content.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiats.tmgcoreutils.constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.groups.Default;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ContTemplateDocDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long Id;

    @NotEmpty(groups = Insert.class, message = "Code cannot be empty")
    private String code;

    private String name;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.FORMAT_SQLSERVER_SHORT,
            timezone = Constant.TIMEZONE_ICT)
    private Timestamp createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.FORMAT_SQLSERVER_SHORT,
            timezone = Constant.TIMEZONE_ICT)
    private Timestamp updatedDate;

    private Long makerId;

    private Long checkerId;

    private Integer activeVersion;

    private Integer status;

    private String content;

    private Timestamp appliedDate;

    private String index;

    public interface Insert extends Default {
    }

    ;

    public interface Update extends Default {
    }

}
