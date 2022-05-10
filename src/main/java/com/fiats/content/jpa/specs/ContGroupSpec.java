package com.fiats.content.jpa.specs;

import com.fiats.content.jpa.entity.*;
import com.fiats.content.payload.filter.ContGroupDocFilter;
import com.fiats.content.payload.filter.ContTemplateFilter;
import com.fiats.tmgcoreutils.constant.Constant;
import com.fiats.tmgjpa.entity.RecordStatus;
import com.fiats.tmgjpa.specification.LongIDIdentityBaseSpecs;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Component
public class ContGroupSpec extends LongIDIdentityBaseSpecs<ContGroupDoc, ContGroupDoc_> {

    public Predicate hasNameEqualsTo(Root<ContGroupDoc> root, CriteriaBuilder builder, String name) {
        return builder.like(builder.lower(root.get(ContGroupDoc_.name)), "%" + name.toLowerCase() + "%");
    }

    public Predicate hasCodeEqualsTo(Root<ContGroupDoc> root, CriteriaBuilder builder, String code) {
        return builder.like(builder.lower(root.get(ContGroupDoc_.code)), "%" + code.toLowerCase() + "%");
    }

    public Predicate hasStatusEqualsTo(Root<ContGroupDoc> root, CriteriaBuilder builder) {
        return builder.equal(root.join(ContGroupDoc_.contGroupDocHistories, JoinType.INNER).get(ContGroupDocHistory_.status), RecordStatus.ACTIVE.getStatus());

    }

    public Specification<ContGroupDoc> buildContGroupListSpecs(ContGroupDocFilter filter) {

        return (root, query, builder) -> {

            query.distinct(Constant.ACTIVE);

            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(filter.getName())) {
                predicates.add(hasNameEqualsTo(root, builder, filter.getName()));
            }

            if (StringUtils.hasText(filter.getCode())) {
                predicates.add(hasCodeEqualsTo(root, builder, filter.getCode()));
            }
            predicates.add(hasStatusEqualsTo(root,builder));

            return concatenatePredicate(predicates, builder);

        };

    }

    public Specification<ContGroupDoc> buildValidateSpecs(ContGroupDocFilter filter) {

        return (root, query, builder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(filter.getCode())) {
                predicates.add(builder.equal(builder.upper(builder.trim(root.get(ContGroupDoc_.code))), filter.getCode().trim().toUpperCase()));
            }

            return concatenatePredicate(predicates, builder);

        };

    }
}
