package com.example.be.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDevelopmentPeriod is a Querydsl query type for DevelopmentPeriod
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QDevelopmentPeriod extends BeanPath<DevelopmentPeriod> {

    private static final long serialVersionUID = -526498271L;

    public static final QDevelopmentPeriod developmentPeriod = new QDevelopmentPeriod("developmentPeriod");

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public QDevelopmentPeriod(String variable) {
        super(DevelopmentPeriod.class, forVariable(variable));
    }

    public QDevelopmentPeriod(Path<? extends DevelopmentPeriod> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDevelopmentPeriod(PathMetadata metadata) {
        super(DevelopmentPeriod.class, metadata);
    }

}

