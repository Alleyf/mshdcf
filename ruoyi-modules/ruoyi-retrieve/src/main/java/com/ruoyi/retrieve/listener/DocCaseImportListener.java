package com.ruoyi.retrieve.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.core.utils.ValidatorUtils;
import com.ruoyi.common.excel.core.ExcelListener;
import com.ruoyi.common.excel.core.ExcelResult;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.retrieve.domain.DocCase;
import com.ruoyi.retrieve.domain.vo.DocCaseImportVo;
import com.ruoyi.retrieve.service.IDocCaseService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 系统用户自定义导入
 *
 * @author Lion Li
 */
@Slf4j
public class DocCaseImportListener extends AnalysisEventListener<DocCaseImportVo> implements ExcelListener<DocCaseImportVo> {

    private final IDocCaseService docCaseService;


    private final Boolean isUpdateSupport;

    private final String operName;
    private final StringBuilder successMsg = new StringBuilder();
    private final StringBuilder failureMsg = new StringBuilder();
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
        DocCase docCase = this.docCaseService.selectDocCaseByName(docCaseImportVo.getName());
        try {
            // 验证是否存在这个案例
            if (ObjectUtil.isNull(docCase)) {
                docCase = BeanUtil.toBean(docCaseImportVo, DocCase.class);
                ValidatorUtils.validate(docCase);
                docCase.setCreateBy(operName);
                docCaseService.save(docCase);
                successNum++;
                successMsg.append("<br/>").append(successNum).append("、案例 ").append(docCase.getName()).append(" 导入成功");
            } else if (isUpdateSupport) {
                Long docCaseId = docCase.getId();
                docCase = BeanUtil.toBean(docCaseImportVo, DocCase.class);
                docCase.setId(docCaseId);
                ValidatorUtils.validate(docCase);
                docCase.setUpdateBy(operName);
                docCaseService.updateById(docCase);
                successNum++;
                successMsg.append("<br/>").append(successNum).append("、案例 ").append(docCase.getName()).append(" 更新成功");
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
