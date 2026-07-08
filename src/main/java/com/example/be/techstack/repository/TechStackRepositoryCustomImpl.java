package com.example.be.techstack.repository;

import static com.example.be.techstack.domain.QTechStack.techStack;

import com.example.be.techstack.dto.result.TechStackResult;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TechStackRepositoryCustomImpl implements TechStackRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TechStackResult> findAllResults() {
        // 목록 조회 시점에 Result로 직접 Projection한다. 이력 화면에서 분류별로 묶고
        // 실력 점수가 높은 순으로 노출할 수 있도록 category, proficiency 기준으로 정렬한다.
        return queryFactory
                .select(Projections.constructor(
                        TechStackResult.class,
                        techStack.id,
                        techStack.name,
                        techStack.category,
                        techStack.imageUrl,
                        techStack.proficiency
                ))
                .from(techStack)
                .orderBy(techStack.category.asc(), techStack.proficiency.desc(), techStack.name.asc())
                .fetch();
    }
}
