package org.findex.team2.sb15findexteam02.service;

import org.findex.team2.sb15findexteam02.dto.request.IndexInfoCreateRequest;
import org.findex.team2.sb15findexteam02.dto.request.IndexInfoSearchRequest;
import org.findex.team2.sb15findexteam02.dto.request.IndexInfoUpdateRequest;
import org.findex.team2.sb15findexteam02.dto.response.CursorPageResponseIndexInfoDto;
import org.findex.team2.sb15findexteam02.dto.response.IndexInfoDto;
import org.findex.team2.sb15findexteam02.model.SourceType;

public interface IndexInfoService {
    CursorPageResponseIndexInfoDto findAll(IndexInfoSearchRequest request);
    IndexInfoDto create(IndexInfoCreateRequest indexInfoCreateRequest, SourceType sourceType);
    IndexInfoDto find(Long id);
    void delete(Long id);
    IndexInfoDto update(Long id, IndexInfoUpdateRequest indexInfoUpdateRequest, SourceType sourceType);



}
