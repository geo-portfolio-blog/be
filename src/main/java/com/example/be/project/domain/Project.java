package com.example.be.project.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 포트폴리오 프로젝트 애그리거트 루트.
 *
 * <p>개발 인원과 사용 기술은 단일 스칼라 값의 목록이므로 {@link ElementCollection}으로 정규화하고,
 * 사진은 대표 여부·정렬 순서 등 여러 속성을 가지므로 {@link ProjectImage} 엔티티로 분리한다.
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

    @Column(name = "summary", length = 200)
    private String summary;

    /**
     * 목록 화면에 노출하는 대표 사진 미니 썸네일 URL.
     * 상세 화면의 대표 사진(full)은 {@link ProjectImage}의 representative 이미지로 관리한다.
     */
    @Column(name = "thumbnail_url", length = 1000)
    private String thumbnailUrl;

    @ElementCollection
    @CollectionTable(name = "project_member", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "member_name", nullable = false, length = 100)
    private List<String> members = new ArrayList<>();

    @Embedded
    private DevelopmentPeriod period;

    @Lob
    @Column(name = "overview")
    private String overview;

    @Lob
    @Column(name = "contribution")
    private String contribution;

    @Lob
    @Column(name = "conclusion")
    private String conclusion;

    @Lob
    @Column(name = "troubleshooting")
    private String troubleshooting;

    @ElementCollection
    @CollectionTable(name = "project_tech_stack", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "tech_name", nullable = false, length = 100)
    private List<String> techStacks = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder asc")
    private List<ProjectImage> images = new ArrayList<>();

    private Project(
            String name,
            String summary,
            String thumbnailUrl,
            DevelopmentPeriod period,
            String overview,
            String contribution,
            String conclusion,
            String troubleshooting
    ) {
        validateName(name);
        this.name = name;
        this.summary = summary;
        this.thumbnailUrl = thumbnailUrl;
        this.period = period;
        this.overview = overview;
        this.contribution = contribution;
        this.conclusion = conclusion;
        this.troubleshooting = troubleshooting;
    }

    public static Project create(
            String name,
            String summary,
            List<String> members,
            DevelopmentPeriod period,
            String overview,
            String contribution,
            String conclusion,
            String troubleshooting,
            List<String> techStacks,
            String thumbnailUrl,
            String representativeImageUrl,
            List<String> imageUrls
    ) {
        Project project = new Project(name, summary, thumbnailUrl, period, overview, contribution, conclusion, troubleshooting);
        project.replaceMembers(members);
        project.replaceTechStacks(techStacks);
        project.registerImages(representativeImageUrl, imageUrls);
        return project;
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

    private void replaceMembers(List<String> members) {
        this.members.clear();
        if (members != null) {
            this.members.addAll(members);
        }
    }

    private void replaceTechStacks(List<String> techStacks) {
        this.techStacks.clear();
        if (techStacks != null) {
            this.techStacks.addAll(techStacks);
        }
    }

    private void registerImages(String representativeImageUrl, List<String> imageUrls) {
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

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("프로젝트명은 필수입니다.");
        }
    }
}
