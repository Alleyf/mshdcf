package com.ruoyi.crawler.utils;

import com.hubspot.jinjava.Jinjava;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

/**
 * 生成python爬虫解析器模板
 *
 * @author fcs
 * @date 2024/2/18 21:16
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
@Component
public class GeneratorCode {
    /**
     * 法律模板路径
     */
    private static final String LAW_TEMPLATE_PATH = "templates/spider-law-parser-template.py.j2";
    /**
     * 案件模板路径
     */
    private static final String CASE_TEMPLATE_PATH = "templates/spider-case-parser-template.py.j2";
    /**
     * jinjava模板引擎
     */
    private final Jinjava JINJAVA = new Jinjava();
    /**
     * 资源加载器（jar包部署时无法使用）
     */
    @Autowired
    private ResourceLoader resourceLoader;


    public String renderTemplate(String templatePath, Map<String, Object> context) {
        try {
            Resource resource = new ClassPathResource(templatePath);
            String templateContent = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
            return JINJAVA.render(templateContent, context);
        } catch (IOException e) {
            throw new RuntimeException("Failed to render template", e);
        }
    }

    public void getLawRenderedTemplateToFile(String templateName, Map<String, Object> context, String outputPath) {
        String renderedTemplate = renderTemplate(LAW_TEMPLATE_PATH, context);
        try {
            // 获取resources目录的路径
            Resource resource = resourceLoader.getResource("classpath:");
            File resourceDir = resource.getFile().getParentFile();

            // 构建codes目录的路径
            File codesDir = new File(resourceDir, "codes");
            if (!codesDir.exists()) {
                codesDir.mkdirs(); // 确保codes目录存在
            }

            // 构建输出文件的完整路径
            File outputFile = new File(codesDir, outputPath);

            // 写入文件
            Files.write(outputFile.toPath(), renderedTemplate.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write rendered template to file", e);
        }
    }

    public void getCaseRenderedTemplateToFile(String templateName, Map<String, Object> context, String outputPath) {
        String renderedTemplate = renderTemplate(CASE_TEMPLATE_PATH, context);
        try {
            // 获取resources目录的路径
            Resource resource = resourceLoader.getResource("classpath:");
            File resourceDir = resource.getFile().getParentFile();

            // 构建codes目录的路径
            File codesDir = new File(resourceDir, "codes");
            if (!codesDir.exists()) {
                codesDir.mkdirs(); // 确保codes目录存在
            }

            // 构建输出文件的完整路径
            File outputFile = new File(codesDir, outputPath);

            // 写入文件
            Files.write(outputFile.toPath(), renderedTemplate.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write rendered template to file", e);
        }
    }

}








