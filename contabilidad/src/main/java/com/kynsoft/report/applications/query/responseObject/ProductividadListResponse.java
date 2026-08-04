package com.kynsoft.report.applications.query.responseObject;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductividadListResponse implements IResponse {
    private List<ProductividadResponse> items;
}
