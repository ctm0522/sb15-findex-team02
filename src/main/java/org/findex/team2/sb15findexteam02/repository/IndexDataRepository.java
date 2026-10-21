package org.findex.team2.sb15findexteam02.repository;

import org.findex.team2.sb15findexteam02.model.IndexData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;

public interface IndexDataRepository extends JpaRepository<IndexData, Long>,
        JpaSpecificationExecutor<IndexData> {

    IndexData findByBaseDateAndIndexInfoId(LocalDate baseDate, Long indexInfoId);

    boolean existsByBaseDateAndIndexInfoId(LocalDate baseDate, Long indexInfoId);
}
