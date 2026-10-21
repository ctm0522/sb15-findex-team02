package org.findex.team2.sb15findexteam02.repository;

import org.findex.team2.sb15findexteam02.model.IndexData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface IndexDataRepository extends JpaRepository<IndexData, Long> {
    boolean existsByBaseDateAndIndexInfoIndexCategoryCategoryNameAndIndexInfoIndexName(LocalDate baseDate, String indexInfoIndexCategoryCategoryName, String indexInfoIndexName);

    IndexData findByBaseDateAndIndexInfoId(LocalDate baseDate, Long indexInfoId);

    boolean existsByBaseDateAndIndexInfoId(LocalDate baseDate, Long indexInfoId);
}
