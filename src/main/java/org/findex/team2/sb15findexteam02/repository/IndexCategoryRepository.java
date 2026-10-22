package org.findex.team2.sb15findexteam02.repository;

import org.findex.team2.sb15findexteam02.model.IndexCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IndexCategoryRepository extends JpaRepository<IndexCategory, Long> {
    boolean existsByCategoryName(String categoryName);


    IndexCategory findByCategoryName(String categoryName);
}
