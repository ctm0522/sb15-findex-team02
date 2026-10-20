package org.findex.team2.sb15findexteam02.service;

import org.findex.team2.sb15findexteam02.dto.request.IndexInfoCreateRequest;
import org.findex.team2.sb15findexteam02.dto.response.IndexInfoDto;

public interface IndexInfoService {
    IndexInfoDto create(IndexInfoCreateRequest indexInfoCreateRequest);


}
