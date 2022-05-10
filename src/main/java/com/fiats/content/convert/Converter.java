package com.fiats.content.convert;

import com.fiats.content.jpa.entity.ContTemplateDoc;
import com.fiats.content.payload.ContTemplateDocDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class Converter {

    private static final String[] IGNORE_ATTRS = {"id", "createdDate", "updatedDate", "active"};


    public static Function<ContTemplateDoc, ContTemplateDocDTO> contTemplateDocDtoEntityToDTO() {
        return p -> {
            ContTemplateDocDTO pDTO = ContTemplateDocDTO.builder().build();
            BeanUtils.copyProperties(p, pDTO);
            return pDTO;
        };
    }

    public static List<ContTemplateDocDTO> contTemplateDocDtoEntityToDTOs(List<ContTemplateDoc> contTemplateDocs) {
        return contTemplateDocs.stream().map(p -> contTemplateDocDtoEntityToDTO().apply(p)).collect(Collectors.toList());
    }
}
