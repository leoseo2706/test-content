package com.fiats.content.jpa.entity;

import com.fiats.tmgjpa.entity.LongIDIdentityBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "CONT_TEMPLATE_DOC_HISTORY")
public class ContTemplateDocHistory extends LongIDIdentityBase {

    @Column(name = "TEMPLATE_ID")
    private Long templateId;

    @Column(name = "VERSION_ID")
    private Long versionId;

    @Column(name = "APPLIED_DATE")
    private Date appliedDate;

    @Column(name = "MAKER_ID")
    private Long makerId;

    @Column(name = "CHECKER_ID")
    private Long checkerId;

    @Column(name = "STATUS")
    private Integer status;

    public ContTemplateDocHistory(Long templateId, Long versionId, Date appliedDate, Long makerId, Long checkerId, Integer status) {
        this.templateId = templateId;
        this.versionId = versionId;
        this.appliedDate = appliedDate;
        this.makerId = makerId;
        this.checkerId = checkerId;
        this.status = status;
    }
}
