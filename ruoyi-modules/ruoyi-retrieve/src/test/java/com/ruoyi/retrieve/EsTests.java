package com.ruoyi.retrieve;

import cn.easyes.core.core.EsWrappers;
import com.ruoyi.retrieve.api.domain.CaseDoc;
import com.ruoyi.retrieve.esmapper.CaseDocMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author fcs
 * @date 2024/1/31 15:49
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
//@SpringBootTest
public class EsTests {

    //    @Resource
    private CaseDocMapper mapper;

    @Test
    public void testCreateIndex() {
        // 测试创建索引,框架会根据实体类及字段上加的自定义注解一键帮您生成索引 需确保索引托管模式处于manual手动挡(默认处于此模式),若为自动挡则会冲突
        boolean success = mapper.createIndex();
        Assertions.assertTrue(success);
    }

    @Test
    public void testInsert() {
        // 测试插入数据
        CaseDoc document = new CaseDoc();
        document.setName("老汉");
        document.setContent("推*技术过硬");
        int successCount = mapper.insert(document);
        System.out.println(successCount);
    }

    @Test
    public void testSelect() {
        // 测试查询 写法和MP一样 可以用链式,也可以非链式 根据使用习惯灵活选择即可
        String title = "老汉";
        CaseDoc document = EsWrappers.lambdaChainQuery(mapper)
            .eq(CaseDoc::getName, title)
            .one();
        System.out.println(document);
    }


}
