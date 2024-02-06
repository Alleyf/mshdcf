package com.ruoyi.manage.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.core.utils.ValidatorUtils;
import com.ruoyi.common.excel.core.ExcelListener;
import com.ruoyi.common.excel.core.ExcelResult;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.manage.domain.DocCase;
import com.ruoyi.manage.domain.vo.DocCaseImportVo;
import com.ruoyi.manage.service.IDocCaseService;
import com.ruoyi.retrieve.api.RemoteCaseDocRetrieveService;
import com.ruoyi.retrieve.api.RemoteRetrieveService;
import com.ruoyi.retrieve.api.domain.CaseDoc;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;

import java.util.List;

/**
 * @author fcs
 * @date 2024/2/1 13:14
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description DocCase导入
 */
@Slf4j
public class DocCaseImportListener extends AnalysisEventListener<DocCaseImportVo> implements ExcelListener<DocCaseImportVo> {

    private final IDocCaseService docCaseService;
    private final Boolean isUpdateSupport;
    private final String operName;
    private final StringBuilder successMsg = new StringBuilder();
    private final StringBuilder failureMsg = new StringBuilder();
    @DubboReference(version = "1.0", group = "case")
    private RemoteCaseDocRetrieveService remoteCaseDocRetrieveService;
    private int successNum = 0;
    private int failureNum = 0;

    public DocCaseImportListener(Boolean isUpdateSupport) {
//        初始化导入数据对象的一些属性
        this.docCaseService = SpringUtils.getBean(IDocCaseService.class);
        this.isUpdateSupport = isUpdateSupport;
        this.operName = LoginHelper.getUsername();
    }

    @Override
    public void invoke(DocCaseImportVo docCaseImportVo, AnalysisContext context) {
//        根据案例名称查询案例
        DocCase docCase = this.docCaseService.selectDocCaseByName(docCaseImportVo.getName());
        try {
            // 验证是否存在这个案例
            if (ObjectUtil.isNull(docCase)) {
//                不存在该案例则新建一个案例并导入
                docCase = BeanUtil.toBean(docCaseImportVo, DocCase.class);
                ValidatorUtils.validate(docCase);
                docCase.setCreateBy(operName);
                boolean sqlFlag = docCaseService.save(docCase);
                boolean esFlag = remoteCaseDocRetrieveService.exist(docCase.getName());
                boolean flag = sqlFlag && esFlag;
                if (flag) {
//                查询刚刚新建的案例
                    DocCase newCase = docCaseService.selectDocCaseByName(docCase.getName());
//                添加es索引
                    remoteCaseDocRetrieveService.insert(BeanCopyUtils.copy(newCase, CaseDoc.class));
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、案例 ").append(docCase.getName()).append(" 导入成功");
                }
            } else if (isUpdateSupport) {
//                存在该案例，更新案例信息
                Long docCaseId = docCase.getId();
                docCase = BeanUtil.toBean(docCaseImportVo, DocCase.class);
                docCase.setId(docCaseId);
                ValidatorUtils.validate(docCase);
                docCase.setUpdateBy(operName);
                boolean sqlFlag = docCaseService.updateById(docCase);
                boolean esFlag = remoteCaseDocRetrieveService.exist(docCase.getName());
                boolean flag = sqlFlag && esFlag;
                if (flag) {
//                更新es索引
                    remoteCaseDocRetrieveService.update(BeanCopyUtils.copy(docCase, CaseDoc.class));
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、案例 ").append(docCase.getName()).append(" 更新成功");
                }
            } else {
                failureNum++;
                failureMsg.append("<br/>").append(failureNum).append("、案例 ").append(docCase.getName()).append(" 已存在");
            }
        } catch (Exception e) {
            failureNum++;
            String msg = "<br/>" + failureNum + "、案例 " + docCase.getName() + " 导入失败：";
            failureMsg.append(msg).append(e.getMessage());
            log.error(msg, e);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }

    @Override
    public ExcelResult<DocCaseImportVo> getExcelResult() {
        return new ExcelResult<DocCaseImportVo>() {

            @Override
            public String getAnalysis() {
                if (failureNum > 0) {
                    failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
                    throw new ServiceException(failureMsg.toString());
                } else {
                    successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
                }
                return successMsg.toString();
            }

            @Override
            public List<DocCaseImportVo> getList() {
                return null;
            }

            @Override
            public List<String> getErrorList() {
                return null;
            }
        };
    }
}
