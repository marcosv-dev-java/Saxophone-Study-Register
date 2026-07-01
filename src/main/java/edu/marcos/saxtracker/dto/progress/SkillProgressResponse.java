package edu.marcos.saxtracker.dto.progress;

import edu.marcos.saxtracker.model.Skill;
import io.swagger.v3.oas.annotations.media.Schema;

public record SkillProgressResponse(
        Skill skill,
        @Schema(description = "Média das notas no período. Retorna null se não houver avaliações registradas nesse período.")
        Double average
) {
}
