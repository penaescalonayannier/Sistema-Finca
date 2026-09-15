package com.kynsoft.share.core.infrastructure.specifications;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GenericSpecification<T> implements Specification<T> {
    private final SearchCriteria criteria;

    private static final Set<SearchOperation> VALUE_INDEPENDENT_OPS = EnumSet.of(
            SearchOperation.IS_NULL,
            SearchOperation.IS_NOT_NULL,
            SearchOperation.IS_TRUE,
            SearchOperation.IS_FALSE,
            SearchOperation.EXISTS,
            SearchOperation.NOT_EXISTS
    );

    public GenericSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        String[] keys = criteria.getKey().split("\\.");
        Path<Object> path = root.get(keys[0]);

        if (criteria.getKey().contains(".")) {
            path = root.get(keys[0]);
            for (int i = 1; i < keys.length; i++) {
                path = path.get(keys[i]);
            }
        } else {
            path = root.get(criteria.getKey());
        }

        // Verificar si el valor es un UUID y convertirlo a UUID si es necesario.
        Object value = criteria.getValue();

        if (!VALUE_INDEPENDENT_OPS.contains(criteria.getOperation())) {
            if (value == null || (value instanceof String str && str.isBlank())) {
                return null;
            }
        }

        if (value instanceof String && isValidUUID((String) value)) {
            value = UUID.fromString((String) value);
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        // Intentar convertir el valor a LocalDate o LocalDateTime si es necesario.
        try {
            value = LocalDate.parse(value.toString(), dateFormatter);
        } catch (DateTimeParseException ignored) {
            try {
                value = LocalDateTime.parse(value.toString(), dateTimeFormatter);
            } catch (DateTimeParseException ignored2) {
            }
        }

        return switch (criteria.getOperation()) {
            case LIKE -> {
                if (value instanceof UUID || UUID.class.equals(path.getJavaType())) {
                    // UUID fields cannot use lower() - use equality instead
                    yield builder.equal(path, value instanceof UUID ? value : UUID.fromString(value.toString()));
                }
                yield builder.like(builder.lower(path.as(String.class)), "%" + value.toString().toLowerCase() + "%");
            }
            case EQUALS -> {
                if (value instanceof LocalDate) {
                    yield builder.equal(path.as(LocalDate.class), (LocalDate) value);
                } else if (value instanceof LocalDateTime) {
                    yield builder.equal(path.as(LocalDateTime.class), (LocalDateTime) value);
                } else if (value instanceof UUID || UUID.class.equals(path.getJavaType())) {
                    yield builder.equal(path, value instanceof UUID ? value : UUID.fromString(value.toString()));
                } else if (value instanceof Boolean || Boolean.class.equals(path.getJavaType()) || boolean.class.equals(path.getJavaType())) {
                    boolean boolVal = value instanceof Boolean ? (Boolean) value : Boolean.parseBoolean(value.toString());
                    yield builder.equal(path, boolVal);
                } else if (value instanceof Number) {
                    yield builder.equal(path, value);
                } else {
                    yield builder.equal(
                            builder.lower(builder.function("replace", String.class, path.as(String.class), builder.literal(" "), builder.literal(""))),
                            value.toString().toLowerCase().replace(" ", "")
                    );
                }
            }

            case NOT_EQUALS -> {
                if (value instanceof LocalDate) {
                    yield builder.notEqual(path.as(LocalDate.class), (LocalDate) value);
                } else if (value instanceof LocalDateTime) {
                    yield builder.notEqual(path.as(LocalDateTime.class), (LocalDateTime) value);
                } else if (value instanceof UUID || UUID.class.equals(path.getJavaType())) {
                    yield builder.notEqual(path, value instanceof UUID ? value : UUID.fromString(value.toString()));
                } else if (value instanceof Boolean || Boolean.class.equals(path.getJavaType()) || boolean.class.equals(path.getJavaType())) {
                    boolean boolVal = value instanceof Boolean ? (Boolean) value : Boolean.parseBoolean(value.toString());
                    yield builder.notEqual(path, boolVal);
                } else if (value instanceof Number) {
                    yield builder.notEqual(path, value);
                } else {
                    yield builder.notEqual(
                            builder.lower(builder.function("replace", String.class, path.as(String.class), builder.literal(" "), builder.literal(""))),
                            value.toString().toLowerCase().replace(" ", "")
                    );
                }
            }

            case GREATER_THAN -> {
                if (value instanceof LocalDate) {
                    yield builder.greaterThan(path.as(LocalDate.class), (LocalDate) value);
                } else if (value instanceof LocalDateTime) {
                    yield builder.greaterThan(path.as(LocalDateTime.class), (LocalDateTime) value);
                } else if (value instanceof Integer) {
                    yield builder.greaterThan(path.as(Integer.class), (Integer) value);
                }
                else if (value instanceof Double) {
                    yield builder.greaterThan(path.as(Double.class), (Double) value);
                }
                else {
                    yield builder.greaterThan(path.as(String.class), value.toString());
                }
            }
            case LESS_THAN -> {
                if (value instanceof LocalDate) {
                    yield builder.lessThan(path.as(LocalDate.class), (LocalDate) value);
                } else if (value instanceof LocalDateTime) {
                    yield builder.lessThan(path.as(LocalDateTime.class), (LocalDateTime) value);
                } else if (value instanceof Integer) {
                    yield builder.lessThan(path.as(Integer.class), (Integer) value);
                }
                else if (value instanceof Double) {
                    yield builder.lessThan(path.as(Double.class), (Double) value);
                }
                else {
                    yield builder.lessThan(path.as(String.class), value.toString());
                }
            }
            case GREATER_THAN_OR_EQUAL_TO -> {
                if (value instanceof LocalDate) {
                    yield builder.greaterThanOrEqualTo(path.as(LocalDate.class), (LocalDate) value);
                } else if (value instanceof LocalDateTime) {
                    yield builder.greaterThanOrEqualTo(path.as(LocalDateTime.class), (LocalDateTime) value);
                } else if (value instanceof Integer) {
                    yield builder.greaterThanOrEqualTo(path.as(Integer.class), (Integer) value);
                }
                else if (value instanceof Double) {
                    yield builder.greaterThanOrEqualTo(path.as(Double.class), (Double) value);
                } else {
                    yield builder.greaterThanOrEqualTo(path.as(String.class), value.toString());
                }
            }
            case LESS_THAN_OR_EQUAL_TO -> {
                if (value instanceof LocalDate) {
                    yield builder.lessThanOrEqualTo(path.as(LocalDate.class), (LocalDate) value);
                } else if (value instanceof LocalDateTime) {
                    yield builder.lessThanOrEqualTo(path.as(LocalDateTime.class), (LocalDateTime) value);
                } else if (value instanceof Integer) {
                    yield builder.lessThanOrEqualTo(path.as(Integer.class), (Integer) value);
                } else if (value instanceof Double) {
                    yield builder.lessThanOrEqualTo(path.as(Double.class), (Double) value);
                }
                else {
                    yield builder.lessThanOrEqualTo(path.as(String.class), value.toString());
                }
            }
            case IN -> {
                CriteriaBuilder.In<Object> inClause = builder.in(path);
                if (value instanceof List) {
                    for (Object item : (List<?>) value) {
                        Object finalValue = convertToUUID(item.toString());
                        if (finalValue == null) {
                            finalValue = item.toString();
                        }
                        inClause.value(finalValue);
                    }
                } else {
                    Object finalValue = convertToUUID(value.toString());
                    if (finalValue == null) {
                        finalValue = value.toString();
                    }
                    inClause.value(finalValue);
                }
                yield inClause;
            }
            case NOT_IN -> {
                CriteriaBuilder.In<Object> inClause = builder.in(path);
                if (value instanceof List) {
                    for (Object item : (List<?>) value) {
                        Object finalValue = convertToUUID(item.toString());
                        if (finalValue == null) {
                            finalValue = item.toString();
                        }
                        inClause.value(finalValue);
                    }
                } else {
                    Object finalValue = convertToUUID(value.toString());
                    if (finalValue == null) {
                        finalValue = value.toString();
                    }
                    inClause.value(finalValue);
                }
                // Usamos builder.not para negar la cláusula "IN"
                yield builder.not(inClause);
            }
            case IS_NULL -> builder.isNull(path);
            case IS_NOT_NULL -> builder.isNotNull(path);
            case IS_TRUE -> builder.isTrue(path.as(Boolean.class));
            case IS_FALSE -> builder.isFalse(path.as(Boolean.class));
            case EXISTS -> {
                Join<T, ?> join = root.join(criteria.getKey(), JoinType.LEFT);
                yield builder.isNotNull(join.get("id"));
            }
            case NOT_EXISTS -> {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<T> subRoot = (Root<T>) subquery.from(root.getJavaType());
                subquery.select(builder.count(subRoot))
                        .where(builder.equal(subRoot.get(criteria.getKey()).get("id"), root.get("id")));
                yield builder.not(builder.exists(subquery));
            }
            default -> throw new IllegalArgumentException("Operación no soportada: " + criteria.getOperation());
        };
    }

    // Método auxiliar para verificar si un String es un UUID válido
    private boolean isValidUUID(String str) {
        try {
            UUID.fromString(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Método auxiliar para convertir un String a UUID
    private UUID convertToUUID(String str) {
        try {
            return UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
