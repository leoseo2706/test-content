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
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ContTemplateNotificationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long Id;

    private String code;

    @NotEmpty(groups = Insert.class, message = "notitype cannot be empty")
    private String notiType;

    private String transType;

    private String description;

    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.FORMAT_SQLSERVER_SHORT,
            timezone = Constant.TIMEZONE_ICT)
    private Date createDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.FORMAT_SQLSERVER_SHORT,
            timezone = Constant.TIMEZONE_ICT)
    private Date updateDate;

    private Integer status;

    @NotEmpty(groups =Insert.class, message = "name cannot be empty")
    private String name;

    public interface Insert extends Default {
    }

    ;

    public interface Update extends Default {
    }
}
