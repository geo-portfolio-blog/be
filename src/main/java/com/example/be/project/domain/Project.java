package com.example.be.project.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 포트폴리오 프로젝트 애그리거트 루트.
 *
 * <p>목록 카드(카테고리·썸네일·제목·소개·역할·태그·기간)와 케이스스터디 상세(메타·히어로·Overview·
 * 아키텍처·결론·Troubleshooting·배운 점·Tech Stack 표)를 모두 만족하도록 설계한다.
 *
 * <p>순서가 의미를 갖는 목록(배운 점·해결 불릿·지표·Tech Stack 행)은 {@link OrderColumn}으로 순서를
 * 보존한다. 결론 지표는 여러 속성을 갖는 값 객체 목록이므로 {@link Metric}, Tech Stack 표의 행은
 * {@link ProjectTech} 값 객체 목록으로 둔다. 사진은 대표 여부·정렬 순서 등 속성을 가지므로
 * {@link ProjectImage} 엔티티로 분리한다(대표 1장 + 본문 N장).
 */
@Entity
@Table(name = "project")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** 상세 페이지 라우팅용 슬러그(예: {@code /projects/[slug]}). 리소스 식별에 쓰이므로 유일하다. */
    @Column(name = "slug", nullable = false, unique = true, length = 200)
    private String slug;

    @Column(name = "summary", length = 200)
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private ProjectCategory category;

    /**
     * 목록 화면에 노출하는 대표 사진 미니 썸네일 URL.
     * 상세 화면의 대표 사진(full)은 {@link ProjectImage}의 representative 이미지로 관리한다.
     */
    @Column(name = "thumbnail_url", length = 1000)
    private String thumbnailUrl;

    /** 상세 메타의 팀 표기(예: "4인 (DevOps Lead)"). 인원수와 메모를 포함한 표시 문자열로 저장한다. */
    @Column(name = "team", length = 100)
    private String team;

    /** 목록 카드와 상세 메타에 노출하는 역할. */
    @Column(name = "role", length = 100)
    private String role;

    /** 상세 메타의 GitHub 저장소 URL. */
    @Column(name = "github_url", length = 1000)
    private String githubUrl;

    @Embedded
    private DevelopmentPeriod period;

    @Column(name = "overview", columnDefinition = "LONGTEXT")
    private String overview;

    /** 아키텍처 설계 이유(해결 방법) 섹션 본문. */
    @Column(name = "architecture", columnDefinition = "LONGTEXT")
    private String architecture;

    /** 결론 섹션의 서술형 본문. 지표(metrics)가 없을 수도 있으므로 텍스트만으로 결론을 채울 수 있다. */
    @Column(name = "conclusion", columnDefinition = "LONGTEXT")
    private String conclusion;

    @ElementCollection
    @CollectionTable(name = "project_metric", joinColumns = @JoinColumn(name = "project_id"))
    @OrderColumn(name = "metric_order")
    private List<Metric> metrics = new ArrayList<>();

    /** Troubleshooting의 "상황 & 원인" 본문. */
    @Column(name = "troubleshooting_situation", columnDefinition = "LONGTEXT")
    private String troubleshootingSituation;

    /** Troubleshooting의 "해결 & 결과" 불릿. */
    @ElementCollection
    @CollectionTable(name = "project_troubleshooting_solution", joinColumns = @JoinColumn(name = "project_id"))
    @OrderColumn(name = "solution_order")
    @Column(name = "content", nullable = false, length = 1000)
    private List<String> troubleshootingSolutions = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "project_learning", joinColumns = @JoinColumn(name = "project_id"))
    @OrderColumn(name = "learning_order")
    @Column(name = "content", nullable = false, length = 1000)
    private List<String> learnings = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "project_tech", joinColumns = @JoinColumn(name = "project_id"))
    @OrderColumn(name = "tech_order")
    private List<ProjectTech> techStacks = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder asc")
    private List<ProjectImage> images = new ArrayList<>();

    private Project(
            String name,
            String slug,
            String summary,
            ProjectCategory category,
            String thumbnailUrl,
            String team,
            String role,
            DevelopmentPeriod period,
            String githubUrl,
            String overview,
            String architecture,
            String conclusion,
            String troubleshootingSituation
    ) {
        validateName(name);
        validateSlug(slug);
        validateCategory(category);
        validatePeriod(period);
        this.name = name;
        this.slug = slug;
        this.summary = summary;
        this.category = category;
        this.thumbnailUrl = thumbnailUrl;
        this.team = team;
        this.role = role;
        this.period = period;
        this.githubUrl = githubUrl;
        this.overview = overview;
        this.architecture = architecture;
        this.conclusion = conclusion;
        this.troubleshootingSituation = troubleshootingSituation;
    }

    public static Project create(
            String name,
            String slug,
            String summary,
            ProjectCategory category,
            String team,
            String role,
            DevelopmentPeriod period,
            String githubUrl,
            String overview,
            String architecture,
            String conclusion,
            List<Metric> metrics,
            String troubleshootingSituation,
            List<String> troubleshootingSolutions,
            List<String> learnings,
            List<ProjectTech> techStacks,
            String thumbnailUrl,
            String representativeImageUrl,
            List<String> imageUrls
    ) {
        Project project = new Project(
                name, slug, summary, category, thumbnailUrl, team, role, period, githubUrl,
                overview, architecture, conclusion, troubleshootingSituation
        );
        project.replaceMetrics(metrics);
        project.replaceTroubleshootingSolutions(troubleshootingSolutions);
        project.replaceLearnings(learnings);
        project.replaceTechStacks(techStacks);
        project.replaceImages(representativeImageUrl, imageUrls);
        return project;
    }

    /**
     * 프로젝트 전체를 새 값으로 교체한다(PUT 의미). 불변식은 변경 시점에 다시 검증한다.
     */
    public void update(
            String name,
            String slug,
            String summary,
            ProjectCategory category,
            String team,
            String role,
            DevelopmentPeriod period,
            String githubUrl,
            String overview,
            String architecture,
            String conclusion,
            List<Metric> metrics,
            String troubleshootingSituation,
            List<String> troubleshootingSolutions,
            List<String> learnings,
            List<ProjectTech> techStacks,
            String thumbnailUrl,
            String representativeImageUrl,
            List<String> imageUrls
    ) {
        validateName(name);
        validateSlug(slug);
        validateCategory(category);
        validatePeriod(period);
        this.name = name;
        this.slug = slug;
        this.summary = summary;
        this.category = category;
        this.thumbnailUrl = thumbnailUrl;
        this.team = team;
        this.role = role;
        this.period = period;
        this.githubUrl = githubUrl;
        this.overview = overview;
        this.architecture = architecture;
        this.conclusion = conclusion;
        this.troubleshootingSituation = troubleshootingSituation;
        replaceMetrics(metrics);
        replaceTroubleshootingSolutions(troubleshootingSolutions);
        replaceLearnings(learnings);
        replaceTechStacks(techStacks);
        replaceImages(representativeImageUrl, imageUrls);
    }

    public String getRepresentativeImageUrl() {
        return images.stream()
                .filter(ProjectImage::isRepresentative)
                .map(ProjectImage::getImageUrl)
                .findFirst()
                .orElse(null);
    }

    public List<String> getExtraImageUrls() {
        return images.stream()
                .filter(image -> !image.isRepresentative())
                .map(ProjectImage::getImageUrl)
                .toList();
    }

    private void replaceMetrics(List<Metric> metrics) {
        this.metrics.clear();
        if (metrics != null) {
            for (Metric metric : metrics) {
                if (metric != null) {
                    this.metrics.add(metric);
                }
            }
        }
    }

    private void replaceTroubleshootingSolutions(List<String> solutions) {
        this.troubleshootingSolutions.clear();
        addNonBlank(this.troubleshootingSolutions, solutions);
    }

    private void replaceLearnings(List<String> learnings) {
        this.learnings.clear();
        addNonBlank(this.learnings, learnings);
    }

    private void replaceTechStacks(List<ProjectTech> techStacks) {
        this.techStacks.clear();
        if (techStacks != null) {
            for (ProjectTech tech : techStacks) {
                if (tech != null) {
                    this.techStacks.add(tech);
                }
            }
        }
    }

    private void replaceImages(String representativeImageUrl, List<String> imageUrls) {
        this.images.clear();
        int order = 0;
        if (representativeImageUrl != null && !representativeImageUrl.isBlank()) {
            this.images.add(ProjectImage.representative(this, representativeImageUrl, order++));
        }
        if (imageUrls != null) {
            for (String imageUrl : imageUrls) {
                if (imageUrl != null && !imageUrl.isBlank()) {
                    this.images.add(ProjectImage.of(this, imageUrl, order++));
                }
            }
        }
    }

    private void addNonBlank(List<String> target, List<String> values) {
        if (values == null) {
            return;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                target.add(value);
            }
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("프로젝트명은 필수입니다.");
        }
    }

    private void validateSlug(String slug) {
        if (slug == null || slug.isBlank()) {
            throw new IllegalArgumentException("프로젝트 슬러그는 필수입니다.");
        }
    }

    private void validateCategory(ProjectCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("프로젝트 카테고리는 필수입니다.");
        }
    }

    private void validatePeriod(DevelopmentPeriod period) {
        if (period == null) {
            throw new IllegalArgumentException("개발 기간은 필수입니다.");
        }
    }
}
