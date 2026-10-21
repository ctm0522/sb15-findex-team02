package org.findex.team2.sb15findexteam02.specification;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import org.findex.team2.sb15findexteam02.dto.request.SyncjobSearchRequest;
import org.findex.team2.sb15findexteam02.model.SyncJob;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SyncJobSpecification {

    public static Specification<SyncJob> search(
            SyncjobSearchRequest request
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request.getJobType() != null) {
                predicates.add(cb.equal(
                        root.get("jobType"),
                        request.getJobType()
                ));
            }

            if (request.getIndexInfoId() != null) {
                predicates.add(cb.equal(
                        root.get("indexInfo").get("id"),
                        request.getIndexInfoId()
                ));
            }

            if (request.getBaseDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("targetDate"),
                        request.getBaseDateFrom()
                ));
            }

            if (request.getBaseDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("targetDate"),
                        request.getBaseDateTo()
                ));
            }

            if (request.getWorker() != null
                    && !request.getWorker().isBlank()) {
                predicates.add(cb.equal(
                        root.get("worker"),
                        request.getWorker()
                ));
            }

            if (request.getJobTimeFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("jobTime"),
                        request.getJobTimeFrom()
                ));
            }

            if (request.getJobTimeTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("jobTime"),
                        request.getJobTimeTo()
                ));
            }

            if (request.getStatus() != null) {
                predicates.add(cb.equal(
                        root.get("status"),
                        request.getStatus()
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<SyncJob> cursor(
            String sortField,
            String sortDirection,
            String cursor,
            Long idAfter
    ) {
        return (root, query, cb) -> {

            if (cursor == null && idAfter == null) {
                return cb.conjunction();
            }

            if (idAfter == null) {
                throw new IllegalArgumentException(
                        "커서 조회에는 idAfter가 필요합니다."
                );
            }

            boolean ascending = sortDirection.equals("asc");

            if (sortField.equals("jobTime")) {
                if (cursor == null) {
                    throw new IllegalArgumentException(
                            "jobTime 정렬에는 cursor가 필요합니다."
                    );
                }

                return compare(
                        root.get("jobTime"),
                        LocalDateTime.parse(cursor),
                        root.get("id"),
                        idAfter,
                        ascending,
                        cb
                );
            }

            Path<LocalDate> datePath = root.get("targetDate");
            Path<Long> idPath = root.get("id");

            // null인 targetDate 구간의 커서
            if (cursor == null) {
                return cb.and(
                        cb.isNull(datePath),
                        idCompare(idPath, idAfter, ascending, cb)
                );
            }

            LocalDate dateCursor = LocalDate.parse(cursor);

            // 날짜가 있는 구간의 다음 데이터 + null 구간
            return cb.or(
                    compare(
                            datePath,
                            dateCursor,
                            idPath,
                            idAfter,
                            ascending,
                            cb
                    ),
                    cb.isNull(datePath)
            );
        };
    }

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

        Predicate sameValue = cb.and(
                cb.equal(path, cursor),
                idCompare(idPath, idAfter, ascending, cb)
        );

        return cb.or(valueCompare, sameValue);
    }

    private static Predicate idCompare(
            Path<Long> idPath,
            Long idAfter,
            boolean ascending,
            CriteriaBuilder cb
    ) {
        return ascending
                ? cb.greaterThan(idPath, idAfter)
                : cb.lessThan(idPath, idAfter);
    }
}