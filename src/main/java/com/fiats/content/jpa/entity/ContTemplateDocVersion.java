package com.fiats.content.jpa.entity;

import com.fiats.tmgjpa.entity.LongIDIdentityBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "CONT_TEMPLATE_DOC_VERSION")
public class ContTemplateDocVersion extends LongIDIdentityBase {

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "VERSION")
    private Integer version;

    @Column(name = "MAKER_ID")
    private Long makerId;

    @Column(name = "CHECKER_ID")
    private Long checkerId;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "TEMPLATE_ID")
    private Long templateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEMPLATE_ID" , nullable = false, insertable = false, updatable = false)
    private ContTemplateDoc contTemplateDoc;

    public ContTemplateDocVersion(Long templateId, String content, Integer version, Long makerId, Long checkerId, Integer status) {
        this.templateId = templateId;
        this.content = content;
        this.version = version;
        this.makerId = makerId;
        this.checkerId = checkerId;
        this.status = status;
    }
}


