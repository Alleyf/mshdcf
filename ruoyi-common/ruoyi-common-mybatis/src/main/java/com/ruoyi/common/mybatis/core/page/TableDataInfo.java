package com.ruoyi.common.mybatis.core.page;

import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 表格分页数据对象
 *
 * @author csFan
 */

@Data
@NoArgsConstructor
public class TableDataInfo<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 列表数据
     */
    private List<T> rows;

    /**
     * 消息状态码
     */
    private int code;

    /**
     * 消息内容
     */
    private String msg;

    /**
     * 分页
     *
     * @param list  列表数据
     * @param total 总记录数
     */
    public TableDataInfo(List<T> list, long total) {
        this.rows = list;
        this.total = total;
    }

    public static <T> TableDataInfo<T> create() {
        return new TableDataInfo<>();
    }


    /**
     * 生成TableDataInfo对象
     *
     * @param page IPage对象
     * @param <T>  泛型类型
     * @return TableDataInfo对象
     */
    public static <T> TableDataInfo<T> build(IPage<T> page) {
        // 创建TableDataInfo对象
        TableDataInfo<T> rspData = TableDataInfo.create();

        // 设置code属性为HttpStatus.HTTP_OK
        rspData.setCode(HttpStatus.HTTP_OK);

        // 设置msg属性为"查询成功"
        rspData.setMsg("查询成功");

        // 设置rows属性为page.getRecords()
        rspData.setRows(page.getRecords());

        // 设置total属性为page.getTotal()
        rspData.setTotal(page.getTotal());

        // 返回TableDataInfo对象
        return rspData;
    }

    public static <T> TableDataInfo<T> build(List<T> list) {
        TableDataInfo<T> rspData = new TableDataInfo<>();
        rspData.setCode(HttpStatus.HTTP_OK);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(list.size());
        return rspData;
    }

    public static <T> TableDataInfo<T> build() {
        TableDataInfo<T> rspData = new TableDataInfo<>();
        rspData.setCode(HttpStatus.HTTP_OK);
        rspData.setMsg("查询成功");
        return rspData;
    }

    public static <T> TableDataInfo<T> build(List<T> list, Long total) {
        TableDataInfo<T> rspData = new TableDataInfo<>();
        rspData.setCode(HttpStatus.HTTP_OK);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(total);
        return rspData;
    }
}
