package com.ruoyi.retrieve.dubbo;

import com.ruoyi.retrieve.api.RemoteCaseDocRetrieveService;
import com.ruoyi.retrieve.api.domain.CaseDoc;
import com.ruoyi.retrieve.service.ICaseDocService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fcs
 * @date 2024/1/31 23:48
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
@Service
@DubboService(version = "1.0", group = "case")
@RequiredArgsConstructor
public class RemoteCaseDocRetrieveServiceImpl implements RemoteCaseDocRetrieveService {

    private final ICaseDocService caseDocService;


    @Override
    public Integer insert(CaseDoc caseDoc) {
        return caseDocService.insert(caseDoc);
    }

    @Override
    public Integer save(CaseDoc caseDoc) {
        Boolean exist = exist(caseDoc.getName());
        if (!exist)
            return caseDocService.insert(caseDoc);
        else
            return caseDocService.update(caseDoc);
    }


    @Override
    public Integer insertBatch(List<CaseDoc> entityList) {
        return caseDocService.insertBatch(entityList);
    }

    @Override
    public Integer update(CaseDoc caseDoc) {
        return caseDocService.update(caseDoc);
    }

    @Override
    public Integer delete(Long id) {
        return caseDocService.delete(id);
    }

    @Override
    public Integer deleteBatch(Long[] ids) {
        return caseDocService.deleteBatch(ids);
    }

    @Override
    public Boolean exist(Long id) {
        return caseDocService.selectById(id) != null;
    }

    @Override
    public Boolean exist(String name) {
        return caseDocService.selectByName(name) != null;
    }
}
