package com.ruoyi.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.service.ISysNoticeService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.websocket.api.RemoteWebSocketService;
import com.ruoyi.websocket.domain.WebscoketMessage;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 公告 信息操作处理
 *
 * @author csFan
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/notice")
public class SysNoticeController extends BaseController {

    @DubboReference
    private final RemoteWebSocketService remoteWebSocketService;
    private final ISysNoticeService noticeService;
    private final ISysUserService userService;

    /**
     * 获取通知公告列表
     */
    @SaCheckPermission("system:notice:list")
    @GetMapping("/list")
    public TableDataInfo<SysNotice> list(SysNotice notice, PageQuery pageQuery) {
        return noticeService.selectPageNoticeList(notice, pageQuery);
    }

    /**
     * 根据通知公告编号获取详细信息
     *
     * @param noticeId 通知ID
     */
    @SaCheckPermission("system:notice:query")
    @GetMapping(value = "/{noticeId}")
    public R<SysNotice> getInfo(@PathVariable Long noticeId) {
        return R.ok(noticeService.selectNoticeById(noticeId));
    }

    /**
     * 新增通知公告
     */
    @SaCheckPermission("system:notice:add")
    @Log(title = "通知公告", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysNotice notice) {
        return toAjax(noticeService.insertNotice(notice));
    }

    /**
     * 修改通知公告
     */
    @SaCheckPermission("system:notice:edit")
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysNotice notice) {
        return toAjax(noticeService.updateNotice(notice));
    }

    /**
     * 删除通知公告
     *
     * @param noticeIds 通知ID串
     */
    @SaCheckPermission("system:notice:remove")
    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{noticeIds}")
    public R<Void> remove(@PathVariable Long[] noticeIds) {
        return toAjax(noticeService.deleteNoticeByIds(noticeIds));
    }

    /**
     * 发送通知公告
     *
     * @param notice 通知
     */
    @SaCheckPermission("system:notice:query")
    @PutMapping("/send")
    public R<Void> send(@Validated @RequestBody SysNotice notice) {
        List<String> targetIds = Arrays.stream(notice.getTargetIds()).map(Convert::toStr).collect(Collectors.toList());
        if (targetIds.isEmpty()) {
            // 获取所有在线用户
            targetIds = remoteWebSocketService.selectOnlineClientList();
        }
        if (!targetIds.isEmpty()) {
            targetIds.forEach(userId -> {
                String targetUserId = "sys_user:" + userId;
                String msgType = ObjectUtil.equal(notice.getNoticeType(), "1") ? "通知" : "公告";
                WebscoketMessage message = new WebscoketMessage(IdUtil.simpleUUID(), msgType, notice.getNoticeTitle(), notice.getNoticeContent(), targetUserId);
                remoteWebSocketService.sendToOne(targetUserId, JSONObject.toJSONString(message));
            });
        } else {
            String msgType = ObjectUtil.equal(notice.getNoticeType(), "1") ? "通知" : "公告";
            WebscoketMessage message = new WebscoketMessage(IdUtil.simpleUUID(), msgType, notice.getNoticeTitle(), notice.getNoticeContent(), null);
            remoteWebSocketService.sendToAll(JSONObject.toJSONString(message));
        }
        return R.ok();
    }


}
