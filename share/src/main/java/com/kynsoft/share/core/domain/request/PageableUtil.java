package com.kynsoft.share.core.domain.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class PageableUtil {

    public static Pageable createPageable(SearchRequest request) {
        Sort sort = Sort.unsorted();

        if (request.getSortBy() != null && !request.getSortBy().isEmpty()) {
            List<Sort.Order> orders = new ArrayList<>();

            String[] sortFields = request.getSortBy().split(",");
            Sort.Direction direction = request.getSortType().equals(SortTypeEnum.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;

            for (String field : sortFields) {
                orders.add(new Sort.Order(direction, field.trim()).ignoreCase());
            }

            sort = Sort.by(orders);
        }

        return PageRequest.of(request.getPage(), request.getPageSize(), sort);
    }
}