package com.ruoyi.manage.domain.mo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "docCase")
public class MDocCase {

    @Id
    private Long id;
    private String name;
    private String content;
    private String stripContent;
    private String relatedCases;
    private String extra;
}
