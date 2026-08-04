package com.kynsoft.report.applications.query.responseObject;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.FincaDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FincaResponse implements IResponse {
    private UUID id;
    private String code;
    private String name;
    private String description;

    public FincaResponse(FincaDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
    }
}