package com.fiats.content.validator;

import com.fiats.content.jpa.entity.ContTemplateNotification;
import com.fiats.content.jpa.repo.ContTemplateNotificationRepository;
import com.fiats.content.payload.ContTemplateNotificationDTO;
import com.fiats.exception.ValidationException;
import com.fiats.tmgcoreutils.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;
@Component
@Slf4j
public class ContTemplateNotificationValidator {

    @Autowired
    private ContTemplateNotificationRepository contTemplateNotificationRepository;

    public ContTemplateNotification validateExistence(ContTemplateNotificationDTO contTemplateNotificationDto, boolean expectation) {

        Optional<ContTemplateNotification> templateDocDtoOptional = contTemplateNotificationDto != null && contTemplateNotificationDto.getId() != null
                ? contTemplateNotificationRepository.findById(contTemplateNotificationDto.getId())
                : contTemplateNotificationRepository.findByCode(contTemplateNotificationDto.getCode());

        boolean actual = templateDocDtoOptional.isPresent();

        if (expectation != actual) {
            throw new ValidationException(CommonUtils.format("ContTemplateNotification existence {0} unexpected!",
                    contTemplateNotificationDto.getCode()));
        }

        return actual ? templateDocDtoOptional.get() : null;
    }

    public ContTemplateNotification validateExistence(String code, boolean expectation) {

        if (!StringUtils.hasText(code)) {
            return null;
        }

        return validateExistence(ContTemplateNotificationDTO.builder().code(code).build(), expectation);
    }

    public void validateCodeIsEmpty(String code) {
        if (!StringUtils.hasText(code)) {
            throw new ValidationException(CommonUtils.format(" Code cannot be empty",
                    code));
        }
    }

    public void validateCodeIsChange(String codeNew,String codeOld) {
        if (!(codeOld).equals(codeNew)) {
            throw new ValidationException(CommonUtils.format(" Code cannot change",
                    codeNew));
        }
    }
}
