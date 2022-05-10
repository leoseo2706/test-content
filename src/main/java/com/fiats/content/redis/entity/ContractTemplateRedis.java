package com.fiats.content.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;


@RedisHash("ContractTemplateRedis")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractTemplateRedis {

    @Id
    @Indexed
    private String index;

    @Indexed
    private String code;

    @Indexed
    private String name;

    private String description;

    private Long createdDate;

    private Long updateDate;

    private Long makerId;

    private Long checkerId;

    private String activeVersion;

    private String status;

    private String content;

    private Long Id;

}
