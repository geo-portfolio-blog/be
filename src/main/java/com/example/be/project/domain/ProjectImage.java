package com.example.be.project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프로젝트 사진. 대표 사진과 그 외 사진을 하나의 테이블로 정규화하고,
 * 대표 사진 여부는 representative 플래그로 표현한다.
 */
@Entity
@Table(name = "project_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "image_url", nullable = false, length = 1000)
    private String imageUrl;

    @Column(name = "representative", nullable = false)
    private boolean representative;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    private ProjectImage(Project project, String imageUrl, boolean representative, int sortOrder) {
        validateImageUrl(imageUrl);
        this.project = project;
        this.imageUrl = imageUrl;
        this.representative = representative;
        this.sortOrder = sortOrder;
    }

    public static ProjectImage representative(Project project, String imageUrl, int sortOrder) {
        return new ProjectImage(project, imageUrl, true, sortOrder);
    }

    public static ProjectImage of(Project project, String imageUrl, int sortOrder) {
        return new ProjectImage(project, imageUrl, false, sortOrder);
    }

    private void validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("이미지 URL은 필수입니다.");
        }
    }
}
