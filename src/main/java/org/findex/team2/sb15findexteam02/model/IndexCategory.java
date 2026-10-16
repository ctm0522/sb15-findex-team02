package org.findex.team2.sb15findexteam02.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Table(name = "index_categories")
@Entity
@Getter
public class IndexCategory extends BaseUpdatableModel {

    @Column(nullable = false,unique = true,length = 20)
    private String name;

    protected IndexCategory(){

    }

}
