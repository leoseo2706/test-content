package com.fiats.content.jpa.specs;


import com.fiats.content.jpa.entity.ContParam;
import com.fiats.content.jpa.entity.ContParam_;
import com.fiats.content.payload.filter.ContractParamFilter;
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
public class ContParamSpecs extends LongIDIdentityBaseSpecs<ContParam, ContParam_> {

    public Predicate hasCodeEqualsTo(Root<ContParam> root, CriteriaBuilder builder, String code) {
        return builder.like(builder.lower(root.get(ContParam_.code)), "%" + code.toLowerCase() + "%");
    }

    public Specification<ContParam> buildContParamListSpecs(ContractParamFilter filter) {

        return (root, query, builder) -> {

            List<Predicate> predicates = new ArrayList<>();


            if (StringUtils.hasText(filter.getCode())) {
                predicates.add(hasCodeEqualsTo(root, builder, filter.getCode()));
            }

            return concatenatePredicate(predicates, builder);

        };

    }
}
