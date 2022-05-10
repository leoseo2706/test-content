package com.fiats.content.redis.entity;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.data.annotation.Id;


import java.util.List;

@RedisHash("ContGroupDocRedis")
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class ContGroupDocRedis {

    @Id
    @Indexed
    private String index;

    private Long Id;

    @Indexed
    private String code;

    @Indexed
    private String name;

    private String description;

    private Long createDate;

    private Long updateDate;

    private Long makerId;

    private Long checkerId;

    private String activeVersion;

    private Integer status;

    private List<Long> templateId;

    private Long appliedDate;

}
