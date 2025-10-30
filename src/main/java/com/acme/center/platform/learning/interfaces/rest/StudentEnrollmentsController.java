package com.acme.center.platform.learning.interfaces.rest;

import com.acme.center.platform.learning.domain.model.queries.ExistsByAcmeStudentRecordIdQuery;
import com.acme.center.platform.learning.domain.model.queries.GetAllEnrollmentsByAcmeStudentRecordIdQuery;
import com.acme.center.platform.learning.domain.model.valueobjects.AcmeStudentRecordId;
import com.acme.center.platform.learning.domain.services.EnrollmentQueryService;
import com.acme.center.platform.learning.domain.services.StudentQueryService;
import com.acme.center.platform.learning.interfaces.rest.resources.EnrollmentResource;
import com.acme.center.platform.learning.interfaces.rest.transform.EnrollmentResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * StudentsController
 *
 * <p>Controller that handles the endpoints for students.
 * It uses the {@link EnrollmentQueryService} to handle the queries
 * for enrollments.
 * <ul>
 *     <li>GET /api/v1/students/{studentRecordId}/enrollments</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping(value = "/api/v1/students/{studentRecordId}/enrollments", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Students")
public class StudentEnrollmentsController {
    private final EnrollmentQueryService enrollmentQueryService;
    private final StudentQueryService studentQueryService;


    /**
     * Constructor
     *
     * @param enrollmentQueryService The {@link EnrollmentQueryService} service
     */
    public StudentEnrollmentsController(EnrollmentQueryService enrollmentQueryService, StudentQueryService studentQueryService) {
        this.enrollmentQueryService = enrollmentQueryService;
        this.studentQueryService = studentQueryService;
    }

    /**
     * GET /api/v1/students/{studentRecordId}/enrollments
     *
     * <p>Endpoint that returns the enrollments for a student</p>
     *
     * @param studentRecordId the student record ID
     * @return the {@link List} of {@link EnrollmentResource} enrollments for the student
     * @see EnrollmentResource
     */
    @GetMapping
    @Operation(summary = "Get enrollments for a student", description = "Get the enrollments for a student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enrollments retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Student not found")})
    public ResponseEntity<List<EnrollmentResource>> getEnrollmentsForStudentWithStudentRecordId(@PathVariable String studentRecordId) {
        var acmeStudentRecordId = new AcmeStudentRecordId(studentRecordId);
        var existByAcmeStudentRecordIdQuery = new ExistsByAcmeStudentRecordIdQuery(acmeStudentRecordId);
        if (!studentQueryService.handle(existByAcmeStudentRecordIdQuery)) return ResponseEntity.notFound().build();
        var getAllEnrollmentsByAcmeStudentRecordIdQuery = new GetAllEnrollmentsByAcmeStudentRecordIdQuery(acmeStudentRecordId);
        var enrollments = enrollmentQueryService.handle(getAllEnrollmentsByAcmeStudentRecordIdQuery);
        var enrollmentResources = enrollments.stream().map(EnrollmentResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(enrollmentResources);
    }

}