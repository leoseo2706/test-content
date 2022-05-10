package com.fiats.content.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiats.tmgcoreutils.constant.Constant;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ContGroupDocDTO implements Serializable {

    private Long id;

    @NotEmpty(groups = Insert.class, message = "Code cont group name cannot be empty")
    private String code;

    private String name;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.FORMAT_SQLSERVER_SHORT, timezone = Constant.TIMEZONE_ICT)
    private Timestamp createDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.FORMAT_SQLSERVER_SHORT, timezone = Constant.TIMEZONE_ICT)
    private Timestamp updateDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.FORMAT_SQLSERVER_SHORT, timezone = Constant.TIMEZONE_ICT)
    private Timestamp appliedDate;

    private Long makerId;

    private Long checkerId;

    private Integer activeVersion;

    private Integer status;

    private List<ContTemplateDocDTO> contTemplateDocDto;

    private List<ContTemplateDocVersionDTO> contTemplateDocVersions;

    @SerializedName("templateId")
    @NotNull(groups = {Insert.class, Update.class}, message = "Template id  cannot be empty")
    private List<Long> templateId;

    private List<Long> versionIds;

    private List<ContGroupDocHistoryDTO> contGroupDocHistories;

    private List<String> lstCodeTemplate;

    private String index;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.FORMAT_SQLSERVER_SHORT, timezone = Constant.TIMEZONE_ICT)
    private Timestamp updatedDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.FORMAT_SQLSERVER_SHORT, timezone = Constant.TIMEZONE_ICT)
    private Timestamp createdDate;

    public interface Insert extends Default {
    }

    ;

    public interface Update extends Default {
    }


}
