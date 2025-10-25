package com.sighs.generalfeedback.api;

import com.sighs.generalfeedback.init.Entry;
import com.sighs.generalfeedback.init.Form;

public interface FeedbackProvider {
    
    /**
     * 检查是否支持指定的Entry
     * @param entry 反馈配置条目
     * @return 如果支持返回true，否则返回false
     */
    boolean supports(Entry entry);
    
    /**
     * 发送反馈
     * @param entry 反馈配置条目
     * @param form 反馈表单数据
     */
    void sendFeedback(Entry entry, Form form);
}