package edu.marcos.saxtracker.dto.progress;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumo do progresso semanal.Pode retornar null se não houver nenhum registro na semana")
public record WeeklyProgressResponse(
        String date,
        Double average
) {
}
