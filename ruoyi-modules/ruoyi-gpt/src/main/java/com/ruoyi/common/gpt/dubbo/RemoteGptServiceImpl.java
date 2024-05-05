package com.ruoyi.common.gpt.dubbo;

import com.ruoyi.common.gpt.utils.OllamaUtils;
import com.ruoyi.gpt.api.RemoteGptService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * @author csFan
 * @version 1.0
 * @description: TODO
 * @date 2024/3/14 14:59
 */
@Service
@DubboService
@RequiredArgsConstructor
public class RemoteGptServiceImpl implements RemoteGptService {
    private final OllamaUtils ollamaUtils;


    /**
     * 生成指定文本的处理结果
     *
     * @param text 需要处理的原始文本。
     * @return 处理后的文本结果。该结果由ollamaUtils的generate方法生成。
     */
    @Override
    public String generate(String text) {
        return ollamaUtils.generate(text);
    }
}
