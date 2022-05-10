package com.fiats.content.jpa.specs;


import com.fiats.content.jpa.entity.ContTemplateNotification;
import com.fiats.content.jpa.entity.ContTemplateNotification_;
import com.fiats.content.payload.filter.ContTemplateNotificationFilter;
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
public class ContTemplateNotificationSpecs extends LongIDIdentityBaseSpecs<ContTemplateNotification, ContTemplateNotification_> {

    public Predicate hasNameEqualsTo(Root<ContTemplateNotification> root, CriteriaBuilder builder, String name) {
        return builder.equal(root.get(ContTemplateNotification_.name), name);
    }

    public Predicate hasCodeEqualsTo(Root<ContTemplateNotification> root, CriteriaBuilder builder, String code) {
        return builder.like(builder.lower(root.get(ContTemplateNotification_.code)), "%" + code.toLowerCase() + "%");
    }

    public Predicate hasStatusEqualsTo(Root<ContTemplateNotification> root, CriteriaBuilder builder, Integer status) {
        return builder.equal(root.get(ContTemplateNotification_.status), status);
    }

    public Predicate hasNotiTypeEqualsTo(Root<ContTemplateNotification> root, CriteriaBuilder builder, String notiType) {
        return builder.equal(root.get(ContTemplateNotification_.notiType), notiType);
    }

    public Predicate hasTransTypeEqualsTo(Root<ContTemplateNotification> root, CriteriaBuilder builder, String transType) {
        return builder.equal(root.get(ContTemplateNotification_.transType), transType);
    }

    public Specification<ContTemplateNotification> buildPropRuleListSpecs(ContTemplateNotificationFilter filter) {

        return (root, query, builder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(filter.getName())) {
                predicates.add(hasNameEqualsTo(root, builder, filter.getName()));
            }

            if (StringUtils.hasText(filter.getCode())) {
                predicates.add(hasCodeEqualsTo(root, builder, filter.getCode()));
            }

            if (filter.getStatus() != null) {
                predicates.add(hasStatusEqualsTo(root, builder, filter.getStatus()));
            }

            if (StringUtils.hasText(filter.getNotiType())) {
                predicates.add(hasNotiTypeEqualsTo(root, builder, filter.getNotiType()));
            }

            if (StringUtils.hasText(filter.getTransactionType())) {
                predicates.add(hasTransTypeEqualsTo(root, builder, filter.getTransactionType()));
            }

            return concatenatePredicate(predicates, builder);

        };

    }
}
