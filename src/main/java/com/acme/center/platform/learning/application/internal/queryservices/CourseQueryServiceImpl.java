package com.acme.center.platform.learning.application.internal.queryservices;

import com.acme.center.platform.learning.domain.exceptions.CourseNotFoundException;
import com.acme.center.platform.learning.domain.model.aggregates.Course;
import com.acme.center.platform.learning.domain.model.entities.LearningPathItem;
import com.acme.center.platform.learning.domain.model.queries.GetAllCoursesQuery;
import com.acme.center.platform.learning.domain.model.queries.GetCourseByIdQuery;
import com.acme.center.platform.learning.domain.model.queries.GetLearningPathItemByCourseIdAndTutorialIdQuery;
import com.acme.center.platform.learning.domain.services.CourseQueryService;
import com.acme.center.platform.learning.infrastructure.persistence.jpa.repositories.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the CourseQueryService interface.
 */
@Service
public class CourseQueryServiceImpl implements CourseQueryService {
    private final CourseRepository courseRepository;

    /**
     * Constructor.
     *
     * @param courseRepository the course repository
     * @see CourseRepository
     */
    public CourseQueryServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // inherited javadoc
    @Override
    public Optional<Course> handle(GetCourseByIdQuery query) {
        return courseRepository.findById(query.courseId());
    }

    // inherited javadoc
    @Override
    public List<Course> handle(GetAllCoursesQuery query) {
        return courseRepository.findAll();
    }


    // inherited javadoc
    @Override
    public Optional<LearningPathItem> handle(GetLearningPathItemByCourseIdAndTutorialIdQuery query) {
        if (!courseRepository.existsById(query.courseId())) throw new CourseNotFoundException(query.courseId());
        return courseRepository.findById(query.courseId())
                .map(course -> course.getLearningPath()
                        .getLearningPathItemWithTutorialId(query.tutorialId()));
    }
}
