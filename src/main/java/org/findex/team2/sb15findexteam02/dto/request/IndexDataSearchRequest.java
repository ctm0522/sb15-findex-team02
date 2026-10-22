package org.findex.team2.sb15findexteam02.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IndexDataSearchRequest {
//여기 한민님이 하셨던게 맞았네요 빨리 보다가 실수했나봅니다. 죄송합니다.
    Long indexInfoId;
    LocalDate startDate;
    LocalDate endDate;
    Long idAfter;
    String cursor;
    String sortField;
    String sortDirection;
    Integer size;
}
