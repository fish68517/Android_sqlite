package com.hakimi.ai;

public class PlaceholderAiSymptomService implements AiSymptomService {

    @Override
    public String askSymptom(String symptomDescription) {
        String text = symptomDescription == null ? "" : symptomDescription.trim();
        if (text.contains("肚子疼") || text.contains("腹痛")) {
            return "可能病因：饮食刺激、胃肠炎、消化不良。\n应对措施：先观察并补水，避免辛辣生冷；若持续加重请尽快就医。\n建议科室：消化内科。";
        }
        if (text.contains("头痛")) {
            return "可能病因：睡眠不足、紧张性头痛、感冒相关不适。\n应对措施：先休息补水，避免熬夜；若剧烈疼痛或伴随呕吐请立即就医。\n建议科室：神经内科。";
        }
        if (text.contains("咳嗽") || text.contains("发烧")) {
            return "可能病因：上呼吸道感染、过敏刺激。\n应对措施：监测体温、保持休息与补水；高热不退请尽快就医。\n建议科室：呼吸内科。";
        }
        return "这是大模型接口占位回复。\n可能病因：需结合更多症状信息判断。\n应对措施：先观察并记录症状变化，必要时尽快就医。\n建议科室：全科/内科分诊。";
    }
}
