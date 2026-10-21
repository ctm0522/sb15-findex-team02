package org.findex.team2.sb15findexteam02.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import org.findex.team2.sb15findexteam02.dto.request.IndexDataSearchRequest;
import org.findex.team2.sb15findexteam02.model.IndexData;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IndexDataSpecification {

    // 검색 조건
    public static Specification<IndexData> search(
            IndexDataSearchRequest request
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request.getIndexInfoId() != null) {
                predicates.add(cb.equal(
                        root.get("indexInfo").get("id"),
                        request.getIndexInfoId()
                ));
            }

            if (request.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("baseDate"),
                        request.getStartDate()
                ));
            }

            if (request.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("baseDate"),
                        request.getEndDate()
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // 커서 조건
    public static Specification<IndexData> cursor(
            String sortField,
            String sortDirection,
            String cursor,
            Long idAfter
    ) {
        return (root, query, cb) -> {

            if (cursor == null && idAfter == null) {
                return cb.conjunction();
            }

            if (cursor == null || idAfter == null) {
                throw new IllegalArgumentException(
                        "cursor와 idAfter는 함께 전달해야 합니다."
                );
            }

            boolean ascending = sortDirection.equals("asc");

            return switch (sortField) {

                case "baseDate" -> compare(
                        root.get("baseDate"),
                        LocalDate.parse(cursor),
                        root.get("id"),
                        idAfter,
                        ascending,
                        cb
                );

                case "marketPrice", "closingPrice",
                     "highPrice", "lowPrice",
                     "versus", "fluctuationRate" -> compare(
                        root.<BigDecimal>get(sortField),
                        new BigDecimal(cursor),
                        root.get("id"),
                        idAfter,
                        ascending,
                        cb
                );

                case "tradingQuantity", "tradingPrice",
                     "marketTotalAmount" -> compare(
                        root.<Long>get(sortField),
                        Long.valueOf(cursor),
                        root.get("id"),
                        idAfter,
                        ascending,
                        cb
                );

                default -> throw new IllegalArgumentException(
                        "지원하지 않는 정렬 필드: " + sortField
                );
            };
        };
    }

    // 정렬 필드 + ID 복합 커서 비교
    private static <T extends Comparable<? super T>> Predicate compare(
            Path<T> path,
            T cursor,
            Path<Long> idPath,
            Long idAfter,
            boolean ascending,
            CriteriaBuilder cb
    ) {

        Predicate valueCompare = ascending
                ? cb.greaterThan(path, cursor)
                : cb.lessThan(path, cursor);

        Predicate idCompare = ascending
                ? cb.greaterThan(idPath, idAfter)
                : cb.lessThan(idPath, idAfter);

        Predicate sameValue = cb.and(
                cb.equal(path, cursor),
                idCompare
        );

        return cb.or(valueCompare, sameValue);
    }
}