package com.kynsoft.report.applications.query.responseObject;

import com.kynsoft.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.BloqueDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BloqueResponse implements IResponse {
    private UUID id;
    private String code;
    private String name;
    private UUID fincaId;
    private String fincaNombre;

    public BloqueResponse(BloqueDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.fincaId = dto.getFincaId();
        this.fincaNombre = dto.getFincaNombre();
    }
}