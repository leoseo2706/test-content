package com.fiats.content.validator;

import com.fiats.content.constant.ContentErrorCode;
import com.fiats.content.jpa.entity.ContGroupDoc;
import com.fiats.content.jpa.repo.ContGroupDocRepository;
import com.fiats.content.jpa.specs.ContGroupSpec;
import com.fiats.content.payload.ContGroupDocDTO;
import com.fiats.content.payload.filter.ContGroupDocFilter;
import com.fiats.exception.ValidationException;
import com.fiats.tmgcoreutils.utils.CommonUtils;
import com.fiats.tmgcoreutils.validator.CommonValidator;
import com.neo.exception.NeoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component
@Slf4j
public class ContGroupValidator extends CommonValidator {
    @Autowired
    private ContGroupDocRepository repository;

    @Autowired
    private ContGroupSpec contGroupSpec;

    public ContGroupDoc validateExistence(ContGroupDocDTO contGroupDocDto, boolean expectation) {

        Optional<ContGroupDoc> groupDocOptional = contGroupDocDto != null && contGroupDocDto.getId() != null
                ? repository.findById(contGroupDocDto.getId())
                : repository.findByCode(contGroupDocDto.getCode());

        boolean actual = groupDocOptional.isPresent();

        if (expectation != actual) {
            throw new ValidationException(CommonUtils.format("Contract group  existence {0} unexpected!",
                    contGroupDocDto.getCode()));
        }

        return actual ? groupDocOptional.get() : null;
    }

    public ContGroupDoc validateExistence(String code, boolean expectation) {

        if (!StringUtils.hasText(code)) {
            return null;
        }

        return validateExistence(ContGroupDocDTO.builder().code(code).build(), expectation);
    }

    public boolean validateExistence(ContGroupDocDTO dto) {
        ContGroupDocFilter filter = null;
        if (dto != null) {
            if(StringUtils.hasText(dto.getCode())) {
                filter = ContGroupDocFilter.builder().code(dto.getCode()).build();
                ContGroupDoc template = repository.findAll(contGroupSpec.buildValidateSpecs(filter)).stream().findAny().orElse(null);
                if (template != null && (dto.getId() == null || !dto.getId().equals(template.getId()))) {
                    throw new NeoException(null, ContentErrorCode.DOC_GROUP_CODE_DUPLICATE,
                            CommonUtils.format("ContGroup existence {0} unexpected!",
                                    dto.getCode()));
                }
            }
        }

        return true;
    }

    public void validateModifyingStatus(ContGroupDoc contGroupDoc, Integer status) {

        if (status == contGroupDoc.getStatus()) {
            throw new ValidationException(CommonUtils.format("Cont group ID {0} status is already {1}",
                    contGroupDoc.getStatus(), status));
        }
    }

}
