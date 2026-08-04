package com.kynsoft.report.applications.query.responseObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RankingResponse {
    private String cargo;
    private List<TrabajadorRankingResponse> topPerformers;
    private List<TrabajadorRankingResponse> bottomPerformers;
    private Double promedioCargo;
    private Integer totalTrabajadores;
}
