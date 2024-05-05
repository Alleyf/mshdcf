package com.ruoyi.manage.repository;

import com.ruoyi.manage.domain.mo.MDocCase;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MDocCaseRepository extends MongoRepository<MDocCase, Long> {

    MDocCase findByName(String username);

}
