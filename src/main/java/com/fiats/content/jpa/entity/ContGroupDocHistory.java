package com.fiats.content.jpa.entity;

import com.fiats.content.payload.ContGroupDocKey;
import com.fiats.exception.NeoFiatsException;
import com.fiats.tmgcoreutils.utils.CommonUtils;
import com.fiats.tmgjpa.entity.LongIDIdentityBase;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "CONT_GROUP_DOC_HISTORY")
public class ContGroupDocHistory extends LongIDIdentityBase {

    @Column(name = "VERSION_ID")
    private Long versionId;

    @Column(name = "APPLIED_DATE")
    private Timestamp appliedDate;

    @Column(name = "MAKER_ID")
    private Long makerId;

    @Column(name = "CHECKER_ID")
    private Long checkerId;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "GROUP_ID")
    private Long groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID" , nullable = false, insertable = false, updatable = false)
    private ContGroupDoc groupDoc;

    @Column(name = "TEMPLATE_ID")
    private Long templateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEMPLATE_ID" , nullable = false, insertable = false, updatable = false)
    private ContTemplateDoc contTemplateDoc;

    public ContGroupDocHistory(Long id, Timestamp createdDate, Timestamp updatedDate, Long groupId, Long templateId, Long versionId, Timestamp appliedDate, Long makerId, Long checkerId, Integer status) {
        super(id, createdDate, updatedDate);
        this.templateId = templateId;
        this.versionId = versionId;
        this.appliedDate = appliedDate;
        this.makerId = makerId;
        this.checkerId = checkerId;
        this.status = status;
    }

    public static ContGroupDocKey buildKey(ContGroupDocHistory entity) {

        if (entity == null || CommonUtils.isInvalidPK(entity.getTemplateId())
                || entity.getContTemplateDoc() == null
                || entity.getContTemplateDoc().getActiveVersion() == null) {
            throw new NeoFiatsException(
                    CommonUtils.format("Cannot build unique key for null attributes for {0}",
                            entity != null ? entity.getId() : null));
        }

        return ContGroupDocKey.builder()
                .templateId(entity.getTemplateId())
                .version(entity.getContTemplateDoc().getActiveVersion())
                .build();
    }
}
