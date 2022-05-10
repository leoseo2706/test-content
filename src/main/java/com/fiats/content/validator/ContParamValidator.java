package com.fiats.content.validator;


import com.fiats.content.jpa.entity.ContParam;
import com.fiats.content.jpa.repo.ContParamRepository;
import com.fiats.content.payload.ContParamDTO;
import com.fiats.exception.ValidationException;
import com.fiats.tmgcoreutils.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component
@Slf4j
public class ContParamValidator {
    @Autowired
    private ContParamRepository contParamRepository;

    public ContParam validateExistence(ContParamDTO contParamDto, boolean expectation) {

        Optional<ContParam> groupDocOptional = contParamDto != null && contParamDto.getId() != null
                ? contParamRepository.findById(contParamDto.getId())
                : contParamRepository.findByCode(contParamDto.getCode());

        boolean actual = groupDocOptional.isPresent();

        if (expectation != actual) {
            throw new ValidationException(CommonUtils.format("ContTemplate existence {0} unexpected!",
                    contParamDto.getCode()));
        }

        return actual ? groupDocOptional.get() : null;
    }

    public ContParam validateExistence(String code, boolean expectation) {

        if (!StringUtils.hasText(code)) {
            return null;
        }

        return validateExistence(ContParamDTO.builder().code(code).build(), expectation);
    }

}
