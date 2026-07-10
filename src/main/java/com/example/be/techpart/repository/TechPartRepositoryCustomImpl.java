package com.example.be.techpart.repository;

import static com.example.be.techpart.domain.QTechPart.techPart;

import com.example.be.techpart.dto.result.TechPartResult;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TechPartRepositoryCustomImpl implements TechPartRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * Technical Expertise 전체 조회.
     *
     * <p>스킬 목록은 순서가 의미를 갖는 단일 컬렉션이므로 {@code MultipleBagFetchException} 위험 없이
     * fetch join으로 한 번에 로딩하고, {@code @OrderColumn}이 스킬 순서를 보존한다. join으로 중복된
     * Part 루트는 식별자 기준으로 Java에서 제거한다(SQL distinct 대신). Part 노출 순서는
     * sortOrder → name으로 안정 정렬한다.
     */
    @Override
    public List<TechPartResult> findAllResults() {
        return queryFactory
                .selectFrom(techPart)
                .leftJoin(techPart.skills).fetchJoin()
                .orderBy(techPart.sortOrder.asc(), techPart.name.asc())
                .fetch()
                .stream()
                .distinct()
                .map(TechPartResult::from)
                .toList();
    }
}
