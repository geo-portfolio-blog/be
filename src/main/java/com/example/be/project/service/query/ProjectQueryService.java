package com.example.be.project.service.query;

import com.example.be.project.dto.result.ProjectResult;
import com.example.be.project.dto.result.ProjectSummaryResult;
import com.example.be.project.exception.ProjectNotFoundException;
import com.example.be.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectQueryService {

    private final ProjectRepository projectRepository;

    /**
     * 상세는 순서 있는 컬렉션(metrics/troubleshootingSolutions/learnings/techStacks)을 포함하는 애그리거트다.
     * 요소 컬렉션의 {@code @OrderColumn} 순서는 QueryDSL Projection으로 반영하기 어렵고 여러 컬렉션을 한 번에
     * fetch join하면 규칙에 어긋나므로, 단건 상세는 애그리거트를 slug로 로딩해 각 컬렉션 순서를 살려
     * {@link ProjectResult}로 변환한다(읽기 트랜잭션 안에서 변환을 끝내 지연 로딩 경계 밖 노출을 막는다).
     */
    public ProjectResult getBySlug(String slug) {
        return projectRepository.findBySlug(slug)
                .map(ProjectResult::from)
                .orElseThrow(() -> new ProjectNotFoundException(slug));
    }

    /**
     * 목록은 무한 스크롤/더 보기 성격이라 전체 개수(count) 없이 다음 페이지 존재 여부만 판단하는
     * {@link Slice}로 제공한다.
     */
    public Slice<ProjectSummaryResult> getSummaries(Pageable pageable) {
        return projectRepository.findSummaries(pageable);
    }
}
