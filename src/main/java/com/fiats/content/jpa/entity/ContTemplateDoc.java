package com.fiats.content.jpa.entity;


import com.fiats.tmgjpa.entity.LongIDIdentityBase;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "CONT_TEMPLATE_DOC", uniqueConstraints = {
        @UniqueConstraint(columnNames = "CODE")
})
public class ContTemplateDoc extends LongIDIdentityBase {

    @Column(name = "CODE")
    private String code;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "MAKER_ID")
    private Long makerId;

    @Column(name = "CHECKER_ID")
    private Long checkerId;

    @Column(name = "ACTIVE_VERSION")
    private Integer activeVersion;

    @Column(name = "STATUS")
    private Integer status;

    @OneToMany(mappedBy = "contTemplateDoc", cascade = CascadeType.ALL)
    private List<ContGroupDocHistory> contGroupDocHistories;

    @OneToMany(mappedBy = "contTemplateDoc", cascade = CascadeType.ALL)
    private List<ContTemplateDocVersion> contTemplateDocVersions;

    public ContTemplateDoc(String code, String name, String description, Long makerId, Long checkerId, Integer activeVersion, Integer status) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.makerId = makerId;
        this.checkerId = checkerId;
        this.activeVersion = activeVersion;
        this.status = status;
    }
}
