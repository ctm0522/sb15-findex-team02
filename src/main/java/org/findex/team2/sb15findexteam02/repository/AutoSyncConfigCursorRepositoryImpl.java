package org.findex.team2.sb15findexteam02.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.findex.team2.sb15findexteam02.model.AutoSyncConfig;
import org.findex.team2.sb15findexteam02.model.IndexInfo;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AutoSyncConfigCursorRepositoryImpl
        implements AutoSyncConfigCursorRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<AutoSyncConfig> searchCursor(
            Long indexInfoId,
            Boolean enabled,
            String sortField,
            String sortDirection,
            String cursorValue,
            Long cursorId,
            int limit
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AutoSyncConfig> query =
                cb.createQuery(AutoSyncConfig.class);

        Root<AutoSyncConfig> root = query.from(AutoSyncConfig.class);
        Join<AutoSyncConfig, IndexInfo> indexInfo =
                root.join("indexInfo");

        boolean ascending = "asc".equals(sortDirection);
        boolean sortByEnabled = "enabled".equals(sortField);

        List<Predicate> predicates = new ArrayList<>();

        if (indexInfoId != null) {
            predicates.add(
                    cb.equal(indexInfo.get("id"), indexInfoId)
            );
        }

        if (enabled != null) {
            predicates.add(
                    cb.equal(root.get("enabled"), enabled)
            );
        }

        if (cursorValue != null && cursorId != null) {
            Predicate valueAfter;
            Predicate sameValue;

            if (sortByEnabled) {
                Boolean value = Boolean.valueOf(cursorValue);

                if (ascending) {
                    valueAfter = Boolean.FALSE.equals(value)
                            ? cb.isTrue(root.get("enabled"))
                            : cb.disjunction();
                } else {
                    valueAfter = Boolean.TRUE.equals(value)
                            ? cb.isFalse(root.get("enabled"))
                            : cb.disjunction();
                }

                sameValue = cb.and(
                        cb.equal(root.get("enabled"), value),
                        cb.greaterThan(root.<Long>get("id"), cursorId)
                );
            } else {
                String value = cursorValue;

                valueAfter = ascending
                        ? cb.greaterThan(
                        indexInfo.<String>get("indexName"), value
                )
                        : cb.lessThan(
                        indexInfo.<String>get("indexName"), value
                );

                sameValue = cb.and(
                        cb.equal(indexInfo.get("indexName"), value),
                        cb.greaterThan(root.<Long>get("id"), cursorId)
                );
            }

            predicates.add(cb.or(valueAfter, sameValue));
        }

        query.select(root)
                .where(predicates.toArray(new Predicate[0]));

        if (sortByEnabled) {
            query.orderBy(
                    ascending
                            ? cb.asc(root.get("enabled"))
                            : cb.desc(root.get("enabled")),
                    cb.asc(root.get("id"))
            );
        } else {
            query.orderBy(
                    ascending
                            ? cb.asc(indexInfo.get("indexName"))
                            : cb.desc(indexInfo.get("indexName")),
                    cb.asc(root.get("id"))
            );
        }

        return entityManager.createQuery(query)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public long countByFilters(Long indexInfoId, Boolean enabled) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<AutoSyncConfig> root = query.from(AutoSyncConfig.class);

        List<Predicate> predicates = new ArrayList<>();

        if (indexInfoId != null) {
            predicates.add(
                    cb.equal(root.get("indexInfo").get("id"), indexInfoId)
            );
        }

        if (enabled != null) {
            predicates.add(
                    cb.equal(root.get("enabled"), enabled)
            );
        }

        query.select(cb.count(root))
                .where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getSingleResult();
    }
}