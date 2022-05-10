package com.fiats.content.jpa.specs;


import com.fiats.content.jpa.entity.ContTemplateDoc;
import com.fiats.content.jpa.entity.ContTemplateDocVersion_;
import com.fiats.content.jpa.entity.ContTemplateDoc_;
import com.fiats.content.payload.filter.ContTemplateFilter;
import com.fiats.tmgcoreutils.constant.Constant;
import com.fiats.tmgjpa.entity.RecordStatus;
import com.fiats.tmgjpa.specification.LongIDIdentityBaseSpecs;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Component
public class ContTemplateSpecs extends LongIDIdentityBaseSpecs<ContTemplateDoc, ContTemplateDoc_> {

    public Predicate hasCodeLikeTo(Root<ContTemplateDoc> root, CriteriaBuilder builder, String code) {
        return builder.like(builder.lower(root.get(ContTemplateDoc_.code)), "%" + code.toLowerCase() + "%");
    }

    public Predicate hasNameLikeTo(Root<ContTemplateDoc> root, CriteriaBuilder builder, String name) {
        return builder.like(builder.lower(root.get(ContTemplateDoc_.name)), "%" + name.toLowerCase() + "%");
    }

    public Predicate hasStatusEqualsTo(Root<ContTemplateDoc> root, CriteriaBuilder builder, Integer status) {
        return builder.equal(root.get(ContTemplateDoc_.status), status);
    }

    public Predicate hasActiveVersionEqualsTo(Root<ContTemplateDoc> root, CriteriaBuilder builder) {
        return builder.equal(root.join(ContTemplateDoc_.contTemplateDocVersions).get(ContTemplateDocVersion_.status), RecordStatus.ACTIVE.getStatus());

    }
    
    public Specification<ContTemplateDoc> buildContTemplateListSpecs(ContTemplateFilter filter) {

        return (root, query, builder) -> {

            query.distinct(true);

            query.distinct(Constant.ACTIVE);
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(filter.getCode())) {
                predicates.add(hasCodeLikeTo(root, builder, filter.getCode()));
            }

            if (StringUtils.hasText(filter.getName())) {
                predicates.add(hasNameLikeTo(root, builder, filter.getName()));
            }


            predicates.add(hasActiveVersionEqualsTo(root, builder));
            predicates.add(hasStatusEqualsTo(root, builder, RecordStatus.ACTIVE.getStatus()));

            return concatenatePredicate(predicates, builder);

        };

    }

    public Specification<ContTemplateDoc> buildValidateSpecs(ContTemplateFilter filter) {

        return (root, query, builder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(filter.getCode())) {
                predicates.add(builder.equal(builder.upper(builder.trim(root.get(ContTemplateDoc_.code))), filter.getCode().trim().toUpperCase()));
            }

            return concatenatePredicate(predicates, builder);

        };

    }
}
