package org.findex.team2.sb15findexteam02.service.basic;

import lombok.RequiredArgsConstructor;
import org.findex.team2.sb15findexteam02.dto.request.IndexInfoCreateRequest;
import org.findex.team2.sb15findexteam02.dto.request.IndexInfoUpdateRequest;
import org.findex.team2.sb15findexteam02.dto.response.CursorPageResponseIndexInfoDto;
import org.findex.team2.sb15findexteam02.dto.response.IndexInfoDto;
import org.findex.team2.sb15findexteam02.model.IndexCategory;
import org.findex.team2.sb15findexteam02.model.IndexInfo;
import org.findex.team2.sb15findexteam02.model.SourceType;
import org.findex.team2.sb15findexteam02.repository.IndexCategoryRepository;
import org.findex.team2.sb15findexteam02.repository.IndexInfoRepository;
import org.findex.team2.sb15findexteam02.service.IndexInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicIndexInfoService implements IndexInfoService {

    private final IndexInfoRepository indexInfoRepository;
    private final IndexCategoryRepository indexCategoryRepository;

    @Override
    public CursorPageResponseIndexInfoDto findAll() {
        return null;
    }

    @Override
    @Transactional
    public IndexInfoDto create(IndexInfoCreateRequest request, SourceType sourceType) {
        IndexCategory category;
        if(indexCategoryRepository.existsByCategoryName(request.getIndexClassification()))
        {
            category = indexCategoryRepository.findByCategoryName(request.getIndexClassification());
        }
        else {
            category = new IndexCategory(request.getIndexClassification());
        }

        IndexInfo indexInfo = new IndexInfo(category,request.getIndexName(),request.getEmployedItemsCount(),
                request.getBasePointInTime(),request.getBaseIndex(),sourceType,request.getFavorite());
        indexInfoRepository.save(indexInfo);

        return IndexInfoDto.from(indexInfo);
    }// todo 나중에 오토싱크 생성 추가 삭제는 ddl cascade가 할 것

    @Override
    @Transactional(readOnly = true)
    public IndexInfoDto find(Long id) {
        IndexInfo indexInfo = indexInfoRepository.findById(id).orElseThrow(() -> new NoSuchElementException("해당 id 지수없음 : "+id));
        return IndexInfoDto.from(indexInfo);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        indexInfoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public IndexInfoDto update(Long id, IndexInfoUpdateRequest request, SourceType sourceType) {
        IndexInfo indexInfo = indexInfoRepository.findById(id).orElseThrow(() -> new NoSuchElementException("해당 id 지수없음 : "+id));
        indexInfo.update(request.getEmployedItemsCount(),request.getBasePointInTime(),request.getBaseIndex(),request.getFavorite(),sourceType);
        return IndexInfoDto.from(indexInfo);
    }
}
