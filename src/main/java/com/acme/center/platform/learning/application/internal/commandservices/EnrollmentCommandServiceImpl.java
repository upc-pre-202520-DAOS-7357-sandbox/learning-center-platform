package com.acme.center.platform.learning.application.internal.commandservices;

import com.acme.center.platform.learning.domain.exceptions.CourseNotFoundException;
import com.acme.center.platform.learning.domain.exceptions.EnrollmentNotFoundException;
import com.acme.center.platform.learning.domain.exceptions.EnrollmentRequestException;
import com.acme.center.platform.learning.domain.exceptions.StudentNotFoundException;
import com.acme.center.platform.learning.domain.model.aggregates.Enrollment;
import com.acme.center.platform.learning.domain.model.commands.*;
import com.acme.center.platform.learning.domain.services.EnrollmentCommandService;
import com.acme.center.platform.learning.infrastructure.persistence.jpa.repositories.CourseRepository;
import com.acme.center.platform.learning.infrastructure.persistence.jpa.repositories.EnrollmentRepository;
import com.acme.center.platform.learning.infrastructure.persistence.jpa.repositories.StudentRepository;
import org.springframework.stereotype.Service;

/**
 * Implementation of the EnrollmentCommandService interface.
 * <p>This class is responsible for handling the commands related to the Enrollment aggregate. It requires a CourseRepository, a StudentRepository and an EnrollmentRepository.</p>
 * @see EnrollmentCommandService
 * @see CourseRepository
 * @see StudentRepository
 * @see EnrollmentRepository
 */
@Service
public class EnrollmentCommandServiceImpl implements EnrollmentCommandService {
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    /**
     * Constructor of the class.
     * @param courseRepository the repository to be used by the class.
     * @param studentRepository the repository to be used by the class.
     * @param enrollmentRepository the repository to be used by the class.
     */
    public EnrollmentCommandServiceImpl(CourseRepository courseRepository, StudentRepository studentRepository, EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    // inherit javadoc
    @Override
    public Long handle(RequestEnrollmentCommand command) {
        if(!studentRepository.existsByAcmeStudentRecordId(command.studentRecordId())) {
            throw new StudentNotFoundException(command.studentRecordId());
        }
        var course = courseRepository.findById(command.courseId()).orElseThrow(() -> new CourseNotFoundException(command.courseId()));
        try {
            var enrollment = new Enrollment(command.studentRecordId(), course);
            enrollmentRepository.save(enrollment);
            return enrollment.getId();
        } catch (Exception e) {
            throw new EnrollmentRequestException(e.getMessage());
        }
    }

    // inherit javadoc
    @Override
    public Long handle(ConfirmEnrollmentCommand command) {
        enrollmentRepository.findById(command.enrollmentId()).map(enrollment -> {
            enrollment.confirm();
            enrollmentRepository.save(enrollment);
            return enrollment.getId();
        }).orElseThrow(() -> new EnrollmentNotFoundException(command.enrollmentId()));
        return null;
    }

    // inherit javadoc
    @Override
    public Long handle(RejectEnrollmentCommand command) {
        enrollmentRepository.findById(command.enrollmentId()).map(enrollment -> {
            enrollment.reject();
            enrollmentRepository.save(enrollment);
            return enrollment.getId();
        }).orElseThrow(() -> new EnrollmentNotFoundException(command.enrollmentId()));
        return null;
    }

    // inherit javadoc
    @Override
    public Long handle(CancelEnrollmentCommand command) {
        enrollmentRepository.findById(command.enrollmentId()).map(enrollment -> {
            enrollment.cancel();
            enrollmentRepository.save(enrollment);
            return enrollment.getId();
        }).orElseThrow(() -> new EnrollmentNotFoundException(command.enrollmentId()));
        return null;
    }

    // inherit javadoc
    @Override
    public Long handle(CompleteTutorialForEnrollmentCommand command) {
        enrollmentRepository.findById(command.enrollmentId()).map(enrollment -> {
            enrollment.completeTutorial(command.tutorialId());
            enrollmentRepository.save(enrollment);
            return enrollment.getId();
        }).orElseThrow(() -> new EnrollmentNotFoundException(command.enrollmentId()));
        return null;
    }
}
