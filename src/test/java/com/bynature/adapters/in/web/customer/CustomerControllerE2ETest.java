package com.bynature.adapters.in.web.customer;

import com.bynature.AbstractByNatureTest;
import com.bynature.domain.model.Customer;
import com.bynature.domain.service.CustomerService;
import com.bynature.domain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerControllerE2ETest extends AbstractByNatureTest {

    private static final UUID CUSTOMER_ID = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setUp() {
        // Authenticate before each test
        authenticateUser();
    }

    @Test
    public void whenGetExistingCustomer_shouldReturnCustomer_E2E() {
        Customer customer = customerService.getCustomer(CUSTOMER_ID);

        // Use exchange method with createAuthenticatedEntity
        ResponseEntity<CustomerRetrievalResponse> response = restTemplate.exchange(
                "/customers/{id}",
                HttpMethod.GET,
                createAuthenticatedEntity(),
                CustomerRetrievalResponse.class,
                CUSTOMER_ID
        );

        // Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(CUSTOMER_ID);
        assertThat(response.getBody().firstName()).isEqualTo(customer.getFirstName());
        assertThat(response.getBody().lastName()).isEqualTo(customer.getLastName());
        assertThat(response.getBody().email()).isEqualTo(customer.getEmail().email());
        assertThat(response.getBody().phoneNumber()).isEqualTo(customer.getPhoneNumber().number());
        assertThat(response.getBody().streetNumber()).isEqualTo(customer.getStreetNumber());
        assertThat(response.getBody().street()).isEqualTo(customer.getStreet());
        assertThat(response.getBody().city()).isEqualTo(customer.getCity());
        assertThat(response.getBody().region()).isEqualTo(customer.getRegion());
        assertThat(response.getBody().postalCode()).isEqualTo(customer.getPostalCode());
        assertThat(response.getBody().country()).isEqualTo(customer.getCountry());
        assertThat(response.getBody().createdAt()).isEqualTo(customer.getCreatedAt().toString());
        assertThat(response.getBody().updatedAt()).isEqualTo(customer.getUpdatedAt().toString());
    }

    @Test
    public void whenGetNonExistingCustomer_shouldReturn404_E2E() {
        // Use exchange with authenticated entity
        ResponseEntity<CustomerRetrievalResponse> response = restTemplate.exchange(
                "/customers/{id}",
                HttpMethod.GET,
                createAuthenticatedEntity(),
                CustomerRetrievalResponse.class,
                UUID.randomUUID()
        );

        // Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void whenCreateValidCustomer_shouldReturnCreated_E2E() {
        // Create valid customer request
        CustomerCreationRequest validRequest = new CustomerCreationRequest(
                UUID.fromString("b48ac10b-58cc-4372-a567-0e02b2c3d402"),
                "Jane",
                "Doe",
                "jane.doe@example.com",
                "+33612345679",
                "42",
                "Main Street",
                "Paris",
                "Île-de-France",
                "75001",
                "France"
        );

        // Use exchange with authenticated entity containing request body
        ResponseEntity<UUID> response = restTemplate.exchange(
                "/customers",
                HttpMethod.POST,
                createAuthenticatedEntity(validRequest),
                UUID.class
        );

        // Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        // Verify location header
        assertThat(response.getHeaders().getLocation()).isNotNull();

        // Verify the customer was created correctly by retrieving it
        UUID customerId = response.getBody();
        ResponseEntity<CustomerRetrievalResponse> getResponse = restTemplate.exchange(
                "/customers/{id}",
                HttpMethod.GET,
                createAuthenticatedEntity(),
                CustomerRetrievalResponse.class,
                customerId
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        CustomerRetrievalResponse customer = getResponse.getBody();
        assertThat(customer).isNotNull();
        assertThat(customer.firstName()).isEqualTo(validRequest.firstName());
        assertThat(customer.lastName()).isEqualTo(validRequest.lastName());
        assertThat(customer.email()).isEqualTo(validRequest.email());
    }

    private static Stream<Arguments> invalidCustomerRequests() {
        return Stream.of(
                // Case: Empty first name
                Arguments.of(
                        "Empty first name",
                        new CustomerCreationRequest(UUID.fromString("b47ac10b-58cc-4372-a567-0e02b2c3d402"),
                                "", "Doe", "john.doe@example.com", "+33612345678",
                                "42", "Main Street", "Paris", "Île-de-France", "75001", "France"
                        )
                ),
                // Case: Missing last name
                Arguments.of(
                        "Missing last name",
                        new CustomerCreationRequest(UUID.fromString("b47ac10b-58cc-4372-a567-0e02b2c3d402"),
                                "John", "", "john.doe@example.com", "+33612345678",
                                "42", "Main Street", "Paris", "Île-de-France", "75001", "France"
                        )
                ),
                // Case: Invalid email format
                Arguments.of(
                        "Invalid email format",
                        new CustomerCreationRequest(UUID.fromString("b47ac10b-58cc-4372-a567-0e02b2c3d402"),
                                "John", "Doe", "invalid-email", "+33612345678",
                                "42", "Main Street", "Paris", "Île-de-France", "75001", "France"
                        )
                ),
                // Case: Invalid phone format
                Arguments.of(
                        "Invalid phone format",
                        new CustomerCreationRequest(UUID.fromString("b47ac10b-58cc-4372-a567-0e02b2c3d402"),
                                "John", "Doe", "john.doe@example.com", "123456",
                                "42", "Main Street", "Paris", "Île-de-France", "75001", "France"
                        )
                ),
                // Case: Empty street number (will fail domain validation)
                Arguments.of(
                        "Empty street number",
                        new CustomerCreationRequest(UUID.fromString("b47ac10b-58cc-4372-a567-0e02b2c3d402"),
                                "John", "Doe", "john.doe@example.com", "+33612345678",
                                "", "Main Street", "Paris", "Île-de-France", "75001", "France"
                        )
                )
        );
    }

    @ParameterizedTest(name = "Invalid customer validation: {0}")
    @MethodSource("invalidCustomerRequests")
    void whenCreateInvalidCustomer_thenReturnBadRequest_E2E(String testName, CustomerCreationRequest invalidRequest) {
        // Use exchange with authenticated entity containing invalid request body
        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                "/customers",
                HttpMethod.POST,
                createAuthenticatedEntity(invalidRequest),
                ProblemDetail.class
        );

        // Verify HTTP 400 Bad Request status
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Should successfully map valid CustomerCreationRequest to domain model")
    void shouldMapValidRequestToDomainModel() {
        // Create a valid request
        CustomerCreationRequest validRequest = new CustomerCreationRequest(
                UUID.fromString("b47ac10b-58cc-4372-a567-0e02b2c3d402"),"Jane", "Smith", "jane.smith@example.com", "+33612345679",
                "123", "Rue Nationale", "Lyon", "Auvergne-Rhône-Alpes", "69002", "France"
        );

        // Map to domain model
        Customer customer = validRequest.toDomain(userService);

        // Verify mapping is correct
        assertThat(customer).isNotNull();
        assertThat(customer.getId()).isNotNull();
        assertThat(customer.getFirstName()).isEqualTo("Jane");
        assertThat(customer.getLastName()).isEqualTo("Smith");
        assertThat(customer.getEmail().email()).isEqualTo("jane.smith@example.com");
        assertThat(customer.getPhoneNumber().number()).isEqualTo("+33612345679");
        assertThat(customer.getStreetNumber()).isEqualTo("123");
        assertThat(customer.getStreet()).isEqualTo("Rue Nationale");
        assertThat(customer.getCity()).isEqualTo("Lyon");
        assertThat(customer.getRegion()).isEqualTo("Auvergne-Rhône-Alpes");
        assertThat(customer.getPostalCode()).isEqualTo("69002");
        assertThat(customer.getCountry()).isEqualTo("France");
    }

    private static Stream<Arguments> customerRequestValidationTestCases() {
        // Test case data - no changes needed here
        return Stream.of(
                // Existing test cases...
                Arguments.of("Empty first name",
                        new CustomerCreationRequest(UUID.fromString("b47ac10b-58cc-4372-a567-0e02b2c3d402"),"", "Doe", "john.doe@example.com", "+33612345678",
                                "42", "Main Street", "Paris", "Île-de-France", "75001", "France"),
                        "firstName", "First name is required"),
                // Rest of the test cases...
                Arguments.of("Empty country",
                        new CustomerCreationRequest(UUID.fromString("b47ac10b-58cc-4372-a567-0e02b2c3d402"),"John", "Doe", "john.doe@example.com", "+33612345678",
                                "42", "Main Street", "Paris", "Île-de-France", "75001", ""),
                        "country", "Country is required")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("customerRequestValidationTestCases")
    @DisplayName("Should validate CustomerCreationRequest fields")
    void shouldValidateCustomerCreationRequestFields(String testName,
                                                     CustomerCreationRequest invalidRequest,
                                                     String fieldName,
                                                     String expectedErrorMessage) {
        // Use exchange with authenticated entity containing the invalid request
        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                "/customers",
                HttpMethod.POST,
                createAuthenticatedEntity(invalidRequest),
                ProblemDetail.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();

        // Extract validation error details from response body
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody().getProperties();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody).containsKey("validationErrors");

        @SuppressWarnings("unchecked")
        List<String> violations = (List<String>) responseBody.get("validationErrors");

        // Assert that the validation contains the expected field error
        boolean hasExpectedViolation = violations.stream()
                .anyMatch(violation ->
                        violation.contains(fieldName));

        assertThat(hasExpectedViolation).isTrue();
    }

    @Test
    @DisplayName("Should reject request with multiple validation errors")
    void shouldRejectRequestWithMultipleValidationErrors() {
        // Creating request with multiple invalid fields
        CustomerCreationRequest invalidRequest = new CustomerCreationRequest(
                null,"", "", "", "",
                "", "", "", "", "", ""
        );

        // Use exchange with authenticated entity containing the invalid request
        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                "/customers",
                HttpMethod.POST,
                createAuthenticatedEntity(invalidRequest),
                ProblemDetail.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();

        // Extract validation errors
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody().getProperties();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody).containsKey("validationErrors");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> violations = (List<Map<String, Object>>) responseBody.get("validationErrors");

        // Should have at least 10 violations (one for each field)
        assertThat(violations).hasSizeGreaterThanOrEqualTo(10);
    }
}