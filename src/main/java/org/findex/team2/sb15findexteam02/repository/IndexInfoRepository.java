package org.findex.team2.sb15findexteam02.repository;

import org.findex.team2.sb15findexteam02.model.IndexInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, Long>, JpaSpecificationExecutor<IndexInfo> {

    Optional<IndexInfo> findByIndexNameAndIndexCategoryCategoryName(String indexName, String indexCategoryCategoryName);

    boolean existsByIndexNameAndIndexCategoryCategoryName(String indexName, String indexCategoryCategoryName);

    @Override
    @EntityGraph(attributePaths = {"indexCategory"})
    Page<IndexInfo> findAll(
            Specification<IndexInfo> spec,
            Pageable pageable
    );

}
