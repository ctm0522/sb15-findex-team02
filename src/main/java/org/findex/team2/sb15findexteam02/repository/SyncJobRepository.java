package org.findex.team2.sb15findexteam02.repository;

import org.findex.team2.sb15findexteam02.model.SyncJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SyncJobRepository extends JpaRepository<SyncJob, Long>, JpaSpecificationExecutor<SyncJob>{


}
