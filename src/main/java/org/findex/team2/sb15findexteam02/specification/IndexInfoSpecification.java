package org.findex.team2.sb15findexteam02.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.findex.team2.sb15findexteam02.dto.request.IndexInfoSearchRequest;
import org.findex.team2.sb15findexteam02.model.IndexCategory;
import org.findex.team2.sb15findexteam02.model.IndexInfo;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class IndexInfoSpecification {

    // 검색 조건
    public static Specification<IndexInfo> search(
            IndexInfoSearchRequest request
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // 지수 분류명
            if (request.getIndexClassification() != null
                    && !request.getIndexClassification().isBlank()) {
                Join<IndexInfo, IndexCategory> category =
                        root.join("indexCategory");

                predicates.add(cb.equal(
                        category.get("categoryName"),
                        request.getIndexClassification()
                ));
            }

            // 지수명 부분 검색
            if (request.getIndexName() != null
                    && !request.getIndexName().isBlank()) {
                predicates.add(cb.like(
                        root.get("indexName"),
                        "%" + request.getIndexName() + "%"
                ));
            }

            // 즐겨찾기 여부
            if (request.getFavorite() != null) {
                predicates.add(cb.equal(
                        root.get("favorite"),
                        request.getFavorite()
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // 커서 조건
    public static Specification<IndexInfo> cursor(
            String sortField,
            String sortDirection,
            String cursor,
            Long idAfter
    ) {
        return (root, query, cb) -> {

            // 첫 페이지
            if (cursor == null && idAfter == null) {
                return cb.conjunction();
            }

            boolean ascending = sortDirection.equals("asc");

            if (idAfter == null) {
                throw new IllegalArgumentException(
                        "커서 조회 시 idAfter가 필요합니다."
                );
            }

            // 정렬값 커서가 없는 경우: ID 정렬만 허용
            if (cursor == null) {
                if (!sortField.equals("id")) {
                    throw new IllegalArgumentException(
                            "해당 정렬 필드에는 cursor가 필요합니다."
                    );
                }

                return ascending
                        ? cb.greaterThan(root.get("id"), idAfter)
                        : cb.lessThan(root.get("id"), idAfter);
            }

            // 정렬 필드에 맞는 커서 조건 생성
            return switch (sortField) {
                case "indexClassification" -> {
                    Path<String> path = root
                            .join("indexCategory")
                            .get("categoryName");

                    yield compare(
                            path, cursor, root.get("id"),
                            idAfter, ascending, cb
                    );
                }

                case "indexName" -> compare(
                        root.get("indexName"),
                        cursor,
                        root.get("id"),
                        idAfter,
                        ascending,
                        cb
                );

                case "employedItemsCount" -> compare(
                        root.get("employedItemsCount"),
                        Integer.valueOf(cursor),
                        root.get("id"),
                        idAfter,
                        ascending,
                        cb
                );

                case "id" -> ascending
                        ? cb.greaterThan(root.get("id"), idAfter)
                        : cb.lessThan(root.get("id"), idAfter);

                default -> throw new IllegalArgumentException(
                        "지원하지 않는 정렬 필드: " + sortField
                );
            };
        };
    }

    // 정렬 필드와 ID를 함께 비교
    private static <T extends Comparable<? super T>> Predicate compare(
            Path<T> path,
            T cursor,
            Path<Long> idPath,
            Long idAfter,
            boolean ascending,
            jakarta.persistence.criteria.CriteriaBuilder cb
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
