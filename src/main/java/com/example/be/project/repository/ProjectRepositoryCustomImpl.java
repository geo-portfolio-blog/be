package com.example.be.project.repository;

import static com.example.be.project.domain.QProject.project;
import static com.example.be.project.domain.QProjectImage.projectImage;

import com.example.be.project.dto.result.ProjectResult;
import com.example.be.project.dto.result.ProjectSummaryResult;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {

    /**
     * 요약 목록 정렬 화이트리스트. 클라이언트가 보낸 sort 파라미터는 이 매핑에 존재하는 프로퍼티만
     * 실제 정렬로 반영하고, 목록에 없는 컬럼은 무시한다(미검증 컬럼 정렬·인젝션 차단).
     */
    private static final Map<String, ComparableExpressionBase<?>> SORTABLE = Map.of(
            "id", project.id,
            "name", project.name,
            "startDate", project.period.startDate
    );

    private final JPAQueryFactory queryFactory;

    /**
     * 프로젝트 상세는 스칼라 본문 + 컬렉션 3개(members / techStacks / images)를 함께 반환하는 복합 조회다.
     *
     * <p>{@code Project}의 세 컬렉션은 모두 별도 테이블에 매핑된 List(bag)이므로, 한 쿼리에서 둘 이상을
     * fetch join하면 카테시안 곱이 발생하고 Hibernate {@code MultipleBagFetchException}까지 유발한다.
     * 따라서 저장소·성능 컨벤션이 요구하는 "복합 조회는 Projection 우선, 컬렉션 2개 이상 fetch join 금지"에
     * 맞춰, 스칼라 본문은 Projection으로, 각 컬렉션은 project_id로 좁힌 별도 쿼리로 조회해 조립한다.
     * 단건 상세이므로 각 쿼리는 인덱스 키(project_id) 단건 범위로 제한되고 카테시안 폭발·N+1이 없다.
     */
    @Override
    public Optional<ProjectResult> findResultById(Long projectId) {
        Tuple body = queryFactory
                .select(
                        project.id,
                        project.name,
                        project.summary,
                        project.period.startDate,
                        project.period.endDate,
                        project.overview,
                        project.contribution,
                        project.conclusion,
                        project.troubleshooting
                )
                .from(project)
                .where(project.id.eq(projectId))
                .fetchOne();

        if (body == null) {
            return Optional.empty();
        }

        List<String> members = findElements(projectId, project.members, "member");
        List<String> techStacks = findElements(projectId, project.techStacks, "techStack");
        List<Tuple> images = findImages(projectId);

        return Optional.of(new ProjectResult(
                body.get(project.id),
                body.get(project.name),
                body.get(project.summary),
                members,
                body.get(project.period.startDate),
                body.get(project.period.endDate),
                body.get(project.overview),
                body.get(project.contribution),
                body.get(project.conclusion),
                body.get(project.troubleshooting),
                techStacks,
                representativeImageUrl(images),
                extraImageUrls(images)
        ));
    }

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
                .orderBy(toOrderSpecifiers(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(project.count())
                .from(project);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private List<String> findElements(
            Long projectId,
            ListPath<String, StringPath> collection,
            String alias
    ) {
        StringPath element = Expressions.stringPath(alias);
        return queryFactory
                .select(element)
                .from(project)
                .join(collection, element)
                .where(project.id.eq(projectId))
                .fetch();
    }

    private List<Tuple> findImages(Long projectId) {
        return queryFactory
                .select(projectImage.imageUrl, projectImage.representative)
                .from(projectImage)
                .where(projectImage.project.id.eq(projectId))
                .orderBy(projectImage.sortOrder.asc())
                .fetch();
    }

    private String representativeImageUrl(List<Tuple> images) {
        return images.stream()
                .filter(image -> Boolean.TRUE.equals(image.get(projectImage.representative)))
                .map(image -> image.get(projectImage.imageUrl))
                .findFirst()
                .orElse(null);
    }

    private List<String> extraImageUrls(List<Tuple> images) {
        return images.stream()
                .filter(image -> !Boolean.TRUE.equals(image.get(projectImage.representative)))
                .map(image -> image.get(projectImage.imageUrl))
                .toList();
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
