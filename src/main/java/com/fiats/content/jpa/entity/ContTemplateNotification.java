package com.fiats.content.jpa.entity;

import com.fiats.tmgjpa.entity.LongIDIdentityBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "CONT_TEMPLATE_NOTIFICATION", uniqueConstraints = {
        @UniqueConstraint(columnNames = "CODE")
})
public class ContTemplateNotification extends LongIDIdentityBase {

    @Column(name = "CODE")
    private String code;

    @Column(name = "NOTI_TYPE")
    private String notiType;

    @Column(name = "TRANSACTION_TYPE")
    private String transType;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "NAME")
    private String name;

}
