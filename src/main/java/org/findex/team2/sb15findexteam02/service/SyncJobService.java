package org.findex.team2.sb15findexteam02.service;

import org.findex.team2.sb15findexteam02.dto.request.IndexDataCreateRequest;
import org.findex.team2.sb15findexteam02.dto.request.IndexDataSyncRequest;
import org.findex.team2.sb15findexteam02.dto.request.IndexInfoCreateRequest;
import org.findex.team2.sb15findexteam02.dto.request.SyncJobCreateRequest;
import org.findex.team2.sb15findexteam02.dto.response.*;
import org.findex.team2.sb15findexteam02.model.ResultType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SyncJobService {
    SyncJobDto create(SyncJobCreateRequest request, String result);
    CursorPageResponseSyncJobDto findAll();

    IndexDataClientDto convertToIndexDataClientDtoDto(IndexDataSyncRequest request, String worker);

    List<SyncJobDto> syncInfo(List<PublicDataInfoResponse> requests, String worker, ResultType status);

    List<SyncJobDto> syncData(List<PublicDataResponse> requests, String worker, ResultType status);

}
