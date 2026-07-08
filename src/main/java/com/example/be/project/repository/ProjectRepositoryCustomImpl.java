package com.example.be.project.repository;

import static com.example.be.project.domain.QProject.project;

import com.example.be.project.dto.result.ProjectSummaryResult;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProjectSummaryResult> findSummaries(Pageable pageable) {
        // 목록의 대표 사진은 Project.thumbnailUrl(미니 썸네일) 단일 컬럼에서 바로 Projection하므로
        // 이미지 테이블 join이 필요 없다. 개발 인원 총 수는 members ElementCollection의 SIZE로 집계한다.
        List<ProjectSummaryResult> content = queryFactory
                .select(Projections.constructor(
                        ProjectSummaryResult.class,
                        project.id,
                        project.name,
                        project.summary,
                        project.thumbnailUrl,
                        project.members.size(),
                        project.contribution,
                        project.period.startDate,
                        project.period.endDate
                ))
                .from(project)
                .orderBy(project.period.startDate.desc(), project.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(project.count())
                .from(project);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
