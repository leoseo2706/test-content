package com.fiats.content.jpa.entity;

import com.fiats.content.payload.ContGroupDocKey;
import com.fiats.exception.NeoFiatsException;
import com.fiats.tmgcoreutils.utils.CommonUtils;
import com.fiats.tmgjpa.entity.LongIDIdentityBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "CONT_GROUP_DOC", uniqueConstraints = {
        @UniqueConstraint(columnNames = "CODE")
})
public class ContGroupDoc extends LongIDIdentityBase {

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

    @Column(name = "STATUS")
    private Integer status;

    @OneToMany(mappedBy = "groupDoc", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ContGroupDocHistory> contGroupDocHistories;
}
