package com.example.be.project.repository;

import static com.example.be.project.domain.QProject.project;

import com.example.be.project.domain.QProjectTech;
import com.example.be.project.dto.result.ProjectSummaryResult;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {

    /**
     * 요약 목록 정렬 화이트리스트. 클라이언트가 보낸 sort 파라미터는 이 매핑에 존재하는 프로퍼티만
     * 실제 정렬로 반영하고, 목록에 없는 컬럼은 무시한다(미검증 컬럼 정렬·인젝션 차단).
     */
    private static final Map<String, ComparableExpressionBase<?>> SORTABLE = Map.of(
            "id", project.id,
            "name", project.name,
            "category", project.category,
            "startDate", project.period.startDate
    );

    private final JPAQueryFactory queryFactory;

    /**
     * 프로젝트 요약 목록(Slice). 카드 스칼라 본문(카테고리·썸네일·제목·슬러그·소개·역할·기간)은 Projection으로
     * 조회하고, 카드 태그는 조회된 프로젝트 id들로 좁힌 한 번의 별도 쿼리로 Tech Stack의 기술명을 모아 조립한다.
     *
     * <p>전체 개수(count) 쿼리 없이 {@code limit + 1}건을 조회해 다음 페이지 존재 여부(hasNext)만 판단한다.
     * 태그는 별도 테이블 컬렉션이라 스칼라와 함께 조인하면 카드가 태그 수만큼 중복되므로, 페이지 안의 id로
     * 태그를 한 번에 모아(Map) 조립해 카테시안 곱과 행별 N+1을 모두 피한다.
     */
    @Override
    public Slice<ProjectSummaryResult> findSummaries(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        List<Tuple> rows = queryFactory
                .select(
                        project.id,
                        project.name,
                        project.slug,
                        project.summary,
                        project.category,
                        project.thumbnailUrl,
                        project.role,
                        project.period.startDate,
                        project.period.endDate
                )
                .from(project)
                .orderBy(toOrderSpecifiers(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageSize + 1L)
                .fetch();

        boolean hasNext = rows.size() > pageSize;
        List<Tuple> pageRows = hasNext ? rows.subList(0, pageSize) : rows;

        List<Long> projectIds = pageRows.stream().map(row -> row.get(project.id)).toList();
        Map<Long, List<String>> tagsById = findTagsByProjectIds(projectIds);

        List<ProjectSummaryResult> content = pageRows.stream()
                .map(row -> new ProjectSummaryResult(
                        row.get(project.id),
                        row.get(project.name),
                        row.get(project.slug),
                        row.get(project.summary),
                        row.get(project.category),
                        row.get(project.thumbnailUrl),
                        row.get(project.role),
                        tagsById.getOrDefault(row.get(project.id), List.of()),
                        row.get(project.period.startDate),
                        row.get(project.period.endDate)
                ))
                .toList();

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private Map<Long, List<String>> findTagsByProjectIds(List<Long> projectIds) {
        if (projectIds.isEmpty()) {
            return Map.of();
        }
        QProjectTech tech = new QProjectTech("tech");
        List<Tuple> rows = queryFactory
                .select(project.id, tech.technology)
                .from(project)
                .join(project.techStacks, tech)
                .where(project.id.in(projectIds))
                .fetch();

        Map<Long, List<String>> tagsById = new LinkedHashMap<>();
        for (Tuple row : rows) {
            tagsById
                    .computeIfAbsent(row.get(project.id), key -> new ArrayList<>())
                    .add(row.get(tech.technology));
        }
        return tagsById;
    }

    /**
     * {@code Pageable}의 정렬을 화이트리스트 기반 {@link OrderSpecifier}로 변환한다. 허용된 프로퍼티만
     * 반영하고 그 외 컬럼은 무시하며, 유효한 정렬이 없으면 기본 정렬(최신 시작일 우선, id 내림차순)을 사용해
     * 페이징 안정성을 보장한다.
     */
    private OrderSpecifier<?>[] toOrderSpecifiers(Sort sort) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();
        for (Sort.Order order : sort) {
            ComparableExpressionBase<?> path = SORTABLE.get(order.getProperty());
            if (path == null) {
                continue;
            }
            orders.add(order.isAscending() ? path.asc() : path.desc());
        }
        if (orders.isEmpty()) {
            orders.add(project.period.startDate.desc());
            orders.add(project.id.desc());
        }
        return orders.toArray(new OrderSpecifier[0]);
    }
}
