package com.example.be.experience.repository;

import static com.example.be.experience.domain.QExperience.experience;

import com.example.be.experience.domain.Experience;
import com.example.be.experience.dto.result.ExperienceResult;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExperienceRepositoryCustomImpl implements ExperienceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 타임라인 전체 목록 조회.
     *
     * <p>불릿 목록({@code points})은 항목마다 노출 순서가 의미를 가지는 단일 컬렉션이다. 컬렉션이 하나뿐이므로
     * {@code MultipleBagFetchException} 위험 없이 fetch join으로 한 번에 로딩하고, {@code @OrderColumn}이
     * 불릿 순서를 보존한다. join으로 중복된 루트 행은 식별자 기준으로 Java에서 제거한다(SQL distinct 대신).
     * 항목 노출 순서는 유형 → sortOrder → 시작일 내림차순 → id 내림차순으로 안정 정렬한다.
     */
    @Override
    public List<ExperienceResult> findAllResults() {
        return queryFactory
                .selectFrom(experience)
                .leftJoin(experience.points).fetchJoin()
                .orderBy(
                        experience.type.asc(),
                        experience.sortOrder.asc(),
                        experience.period.startDate.desc(),
                        experience.id.desc()
                )
                .fetch()
                .stream()
                .distinct()
                .map(ExperienceResult::from)
                .toList();
    }
}
