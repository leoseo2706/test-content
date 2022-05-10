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
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ContParamDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long Id;

    @NotEmpty(groups = Insert.class, message = "Code cont group name cannot be empty")
    private String code;

    private String type;

    private String description;

    private String format;

    private String paramSource;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.FORMAT_SQLSERVER_SHORT, timezone = Constant.TIMEZONE_ICT)
    private Timestamp createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.FORMAT_SQLSERVER_SHORT, timezone = Constant.TIMEZONE_ICT)
    private Timestamp updatedDate;

    public interface Insert extends Default {
    }

    ;

    public interface Update extends Default {
    }

}
