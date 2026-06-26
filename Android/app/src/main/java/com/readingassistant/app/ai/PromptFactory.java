package com.readingassistant.app.ai;

public final class PromptFactory {
    private PromptFactory() {
    }

    public static String systemPrompt() {
        return "你是一个阅读辅助总结器。请在内部完成充分理解和推理，但最终只输出最终结论。"
                + "输出必须满足：1.只输出一行，以“总结：”开头；2.不输出分析过程、推理步骤、引用列表、免责声明或多余前后缀；"
                + "3.总结不超过80个中文字符；4.如果内容无法识别，输出“总结：当前页面内容不足，暂无法总结。”";
    }

    public static String textUserPrompt(String pageText) {
        return "请总结当前阅读页面的核心意思。只返回最终总结。\n\n页面正文：\n" + pageText;
    }

    public static String imageUserPrompt() {
        return "请阅读图片中的页面内容，返回不超过80个中文字符的最终总结。不要输出推理过程。";
    }
}
