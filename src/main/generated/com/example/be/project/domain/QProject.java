package com.example.be.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProject is a Querydsl query type for Project
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProject extends EntityPathBase<Project> {

    private static final long serialVersionUID = -711783266L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProject project = new QProject("project");

    public final StringPath conclusion = createString("conclusion");

    public final StringPath contribution = createString("contribution");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<ProjectImage, QProjectImage> images = this.<ProjectImage, QProjectImage>createList("images", ProjectImage.class, QProjectImage.class, PathInits.DIRECT2);

    public final ListPath<String, StringPath> members = this.<String, StringPath>createList("members", String.class, StringPath.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final StringPath overview = createString("overview");

    public final QDevelopmentPeriod period;

    public final StringPath summary = createString("summary");

    public final ListPath<String, StringPath> techStacks = this.<String, StringPath>createList("techStacks", String.class, StringPath.class, PathInits.DIRECT2);

    public final StringPath troubleshooting = createString("troubleshooting");

    public QProject(String variable) {
        this(Project.class, forVariable(variable), INITS);
    }

    public QProject(Path<? extends Project> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProject(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProject(PathMetadata metadata, PathInits inits) {
        this(Project.class, metadata, inits);
    }

    public QProject(Class<? extends Project> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.period = inits.isInitialized("period") ? new QDevelopmentPeriod(forProperty("period")) : null;
    }

}

