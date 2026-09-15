package com.kynsoft.report.applications.query.responseObject;

import com.kynsoft.share.core.domain.bus.query.IResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FincaProductoListResponse implements IResponse {
    private List<FincaProductoResponse> items;
    private Long totalElements;
    private Integer totalPages;
    private Integer currentPage;
    private Integer pageSize;

    public FincaProductoListResponse(List<FincaProductoResponse> items) {
        this.items = items;
        this.totalElements = (long) items.size();
        this.totalPages = 1;
        this.currentPage = 0;
        this.pageSize = items.size();
    }

    public FincaProductoListResponse(List<FincaProductoResponse> items, Long totalElements, Integer totalPages, 
                                     Integer currentPage, Integer pageSize) {
        this.items = items;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }
}