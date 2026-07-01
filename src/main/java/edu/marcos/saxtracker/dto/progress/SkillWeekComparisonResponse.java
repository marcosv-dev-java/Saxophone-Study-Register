package edu.marcos.saxtracker.dto.progress;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Média das notas da semana atual e anterior.Pode retornar null se não houver valor(caso algum for null o campo difference também será null)")
public record SkillWeekComparisonResponse(
        SkillProgressResponse actual,
        SkillProgressResponse previous
) {
    public Double difference() {
        if (actual.average() == null || previous.average() == null) return null;
        return actual.average() - previous.average();
    }
}
