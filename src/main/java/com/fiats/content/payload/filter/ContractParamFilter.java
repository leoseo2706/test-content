package com.fiats.content.payload.filter;

import com.fiats.tmgjpa.filter.BaseFilter;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class ContractParamFilter extends BaseFilter {

    private static final long serialVersionUID = 1L;

    private String code;

}
