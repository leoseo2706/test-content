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
@Table(name = "CONT_PARAM", uniqueConstraints = {
        @UniqueConstraint(columnNames = "CODE")
})
public class ContParam extends LongIDIdentityBase {

    @Column(name = "CODE")
    private String code;

    @Column(name = "DATA_TYPE")
    private String type;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "DATA_FORMAT")
    private String format;

    @Column(name = "PARAM_SOURCE")
    private String paramSource;

}
