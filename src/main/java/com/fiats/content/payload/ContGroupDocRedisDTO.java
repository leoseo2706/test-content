package com.fiats.content.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiats.tmgcoreutils.constant.Constant;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Setter
public class ContGroupDocRedisDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long Id;

    @NotNull(groups = Insert.class, message = "Code cont group name cannot be empty")
    private String code;

    @NotNull(groups = Insert.class, message = "name cont group name cannot be empty")
    private String name;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.FORMAT_SQLSERVER_SHORT, timezone = Constant.TIMEZONE_ICT)
    private Date createDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.FORMAT_SQLSERVER_SHORT, timezone = Constant.TIMEZONE_ICT)
    private Date updateDate;

    private Long makerId;

    private Long checkerId;

    private String activeVersion;

    private Integer status;

    @NotNull(groups = Insert.class, message = "templateId cont group name cannot be empty")
    private List<Long> templateId;

    @SerializedName("appliedDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.FORMAT_SQLSERVER_SHORT, timezone = Constant.TIMEZONE_ICT)
    @NotNull(groups = Insert.class, message = "AppliedDate cont group name cannot be empty")
    private Date appliedDate;

    private String codeTemplateContract;

    private List<String>lstCodeTemplate;

    private String index;

    public interface Insert extends Default {
    }

    ;

    public interface Update extends Default {
    }
}
