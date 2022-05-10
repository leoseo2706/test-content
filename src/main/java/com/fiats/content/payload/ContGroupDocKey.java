package com.fiats.content.payload;

import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Data
@Builder
public class ContGroupDocKey {

    private Integer version;

    private Long templateId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContGroupDocKey)) return false;
        ContGroupDocKey that = (ContGroupDocKey) o;
        return Objects.equals(getVersion(), that.getVersion()) &&
                Objects.equals(getTemplateId(), that.getTemplateId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVersion(), getTemplateId());
    }
}
