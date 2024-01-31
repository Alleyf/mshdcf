package com.ruoyi.manage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hankcs.hanlp.restful.HanLPClient;
import com.ruoyi.manage.domain.DocCase;
import com.ruoyi.manage.service.IDocCaseService;
import com.ruoyi.manage.service.impl.DocCaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * @author fcs
 * @date 2024/1/31 11:49
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
@SpringBootTest
@Slf4j
public class DataProcessTest {

    @Resource
    private HanLPClient hanLP;

    @Resource
    private IDocCaseService docCaseService;

    @Test
    public void test() throws IOException {
//        System.out.println("hello world");
//        HanLPClient hanLP = new HanLPClient("https://www.hanlp.com/api", null); // auth不填则匿名，zh中文，mul多语种
        DocCase docCase = docCaseService.selectDocCaseByName("新疆巴音郭楞蒙古自治州住房和城乡建设局、新疆裕邦房地产开发有限公司行政案由执行实施执行裁定书");
//        Map<String, Double> map = hanLP.extractiveSummarization(docCase.getContent(), 3);
        String correction = hanLP.grammaticalErrorCorrection(docCase.getContent());
        prettyPrint(correction);
    }

    void prettyPrint(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        log.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object));
    }
}
