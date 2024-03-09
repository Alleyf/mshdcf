package com.ruoyi.crawler;

import com.ruoyi.crawler.utils.GeneratorCode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fcs
 * @date 2024/2/18 22:13
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
@SpringBootTest
public class CodeGeneratorTest {

    @Resource
    private GeneratorCode generatorCode;

    @Test
    public void testLawCodeGenerator() {
        Map<String, Object> context = new HashMap<>();
        context.put("source_name", "OpenLaw");
        context.put("source_url", "http://openlaw.cn/law/");
        context.put("start_urls", Arrays.asList(context.get("source_url")));
        context.put("alias", "openLaw");
        String templatePath = "templates/spider-law-parser-template.py.j2";
        String outputPath = "/output.py";
        generatorCode.getLawRenderedTemplateToFile(templatePath, context, outputPath);
    }

    @Test
    public void testCaseCodeGenerator() {
        Map<String, Object> context = new HashMap<>();
        context.put("source_name", "OpenLaw");
        context.put("source_url", "http://openlaw.cn/law/");
        context.put("start_urls", Arrays.asList(context.get("source_url")));
        context.put("alias", "openLaw");
        String templatePath = "templates/spider-case-parser-template.py.j2";
        String outputPath = "/output.py";
        generatorCode.getLawRenderedTemplateToFile(templatePath, context, outputPath);
    }
}
