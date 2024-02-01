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
import com.ruoyi.manage.domain.LawRegulation;
import com.ruoyi.manage.domain.vo.LawRegulationImportVo;
import com.ruoyi.manage.service.ILawRegulationService;
import com.ruoyi.retrieve.api.RemoteRetrieveService;
import com.ruoyi.retrieve.api.domain.LawDoc;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;

import java.util.List;

/**
 * @author fcs
 * @date 2024/2/1 13:14
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description lawRegulation导入
 */
@Slf4j
public class LawRegulationImportListener extends AnalysisEventListener<LawRegulationImportVo> implements ExcelListener<LawRegulationImportVo> {

    private final ILawRegulationService lawRegulationService;
    private final Boolean isUpdateSupport;
    private final String operName;
    private final StringBuilder successMsg = new StringBuilder();
    private final StringBuilder failureMsg = new StringBuilder();
    @DubboReference(version = "1.0", group = "law")
    private RemoteRetrieveService<LawDoc> remoteRetrieveService;
    private int successNum = 0;
    private int failureNum = 0;

    public LawRegulationImportListener(Boolean isUpdateSupport) {
//        初始化导入数据对象的一些属性
        this.lawRegulationService = SpringUtils.getBean(ILawRegulationService.class);
        this.isUpdateSupport = isUpdateSupport;
        this.operName = LoginHelper.getUsername();
    }

    @Override
    public void invoke(LawRegulationImportVo lawRegulationImportVo, AnalysisContext context) {
//        根据法条名称查询法条
        LawRegulation lawRegulation = this.lawRegulationService.selectLawRegulationByName(lawRegulationImportVo.getName());
        try {
            // 验证是否存在这个法条
            if (ObjectUtil.isNull(lawRegulation)) {
//                不存在该法条则新建一个法条并导入
                lawRegulation = BeanUtil.toBean(lawRegulationImportVo, LawRegulation.class);
                ValidatorUtils.validate(lawRegulation);
                lawRegulation.setCreateBy(operName);
                boolean sqlFlag = lawRegulationService.save(lawRegulation);
                boolean esFlag = remoteRetrieveService.exist(lawRegulation.getName());
                boolean flag = sqlFlag && esFlag;
                if (flag) {
//                查询刚刚新建的法条
                    LawRegulation newLaw = lawRegulationService.selectLawRegulationByName(lawRegulation.getName());
//                添加es索引
                    remoteRetrieveService.insert(BeanCopyUtils.copy(newLaw, LawDoc.class));
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、法条 ").append(lawRegulation.getName()).append(" 导入成功");
                }
            } else if (isUpdateSupport) {
//                存在该法条，更新法条信息
                Long lawRegulationId = lawRegulation.getId();
                lawRegulation = BeanUtil.toBean(lawRegulationImportVo, LawRegulation.class);
                lawRegulation.setId(lawRegulationId);
                ValidatorUtils.validate(lawRegulation);
                lawRegulation.setUpdateBy(operName);
                boolean sqlFlag = lawRegulationService.updateById(lawRegulation);
                boolean esFlag = remoteRetrieveService.exist(lawRegulation.getName());
                boolean flag = sqlFlag && esFlag;
                if (flag) {
//                更新es索引
                    remoteRetrieveService.update(BeanCopyUtils.copy(lawRegulation, LawDoc.class));
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、法条 ").append(lawRegulation.getName()).append(" 更新成功");
                }
            } else {
                failureNum++;
                failureMsg.append("<br/>").append(failureNum).append("、法条 ").append(lawRegulation.getName()).append(" 已存在");
            }
        } catch (Exception e) {
            failureNum++;
            String msg = "<br/>" + failureNum + "、法条 " + lawRegulation.getName() + " 导入失败：";
            failureMsg.append(msg).append(e.getMessage());
            log.error(msg, e);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }

    @Override
    public ExcelResult<LawRegulationImportVo> getExcelResult() {
        return new ExcelResult<LawRegulationImportVo>() {

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
            public List<LawRegulationImportVo> getList() {
                return null;
            }

            @Override
            public List<String> getErrorList() {
                return null;
            }
        };
    }
}
