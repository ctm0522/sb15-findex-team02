package org.findex.team2.sb15findexteam02.repository;

import org.findex.team2.sb15findexteam02.model.IndexInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, Long> {

    Optional<IndexInfo> findByIndexNameAndIndexCategoryCategoryName(String indexName, String indexCategoryCategoryName);

    boolean existsByIndexNameAndIndexCategoryCategoryName(String indexName, String indexCategoryCategoryName);
}
