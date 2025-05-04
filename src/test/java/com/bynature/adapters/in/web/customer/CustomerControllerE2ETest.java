package com.bynature.adapters.in.web.customer;

import com.bynature.AbstractByNatureTest;
import com.bynature.domain.model.Customer;
import com.bynature.domain.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerService customerService;

    @Test
    public void whenGetExistingCustomer_shouldReturnCustomer_E2E() {
        Customer customer = customerService.getCustomer(CUSTOMER_ID);

        // Retrieve the customer via API
        ResponseEntity<CustomerRetrievalResponse> response = restTemplate.getForEntity(
                "/customers/" + CUSTOMER_ID.toString(),
                CustomerRetrievalResponse.class
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
        // Attempt to retrieve a non-existing customer
        ResponseEntity<CustomerRetrievalResponse> response = restTemplate.getForEntity(
                "/customers/" + UUID.randomUUID(),
                CustomerRetrievalResponse.class
        );

        // Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    @Test
    public void whenCreateValidCustomer_shouldReturnCreated_E2E() {
        // Create valid customer request
        CustomerCreationRequest validRequest = new CustomerCreationRequest(
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

        // Create customer via API
        ResponseEntity<UUID> response = restTemplate.postForEntity(
                "/customers",
                validRequest,
                UUID.class
        );

        // Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        // Verify location header
        assertThat(response.getHeaders().getLocation()).isNotNull();

        // Verify the customer was created correctly by retrieving it
        UUID customerId = response.getBody();
        ResponseEntity<CustomerRetrievalResponse> getResponse = restTemplate.getForEntity(
                "/customers/" + customerId,
                CustomerRetrievalResponse.class
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
                        new CustomerCreationRequest(
                                "", "Doe", "john.doe@example.com", "+33612345678",
                                "42", "Main Street", "Paris", "Île-de-France", "75001", "France"
                        )
                ),
                // Case: Missing last name
                Arguments.of(
                        "Missing last name",
                        new CustomerCreationRequest(
                                "John", "", "john.doe@example.com", "+33612345678",
                                "42", "Main Street", "Paris", "Île-de-France", "75001", "France"
                        )
                ),
                // Case: Invalid email format
                Arguments.of(
                        "Invalid email format",
                        new CustomerCreationRequest(
                                "John", "Doe", "invalid-email", "+33612345678",
                                "42", "Main Street", "Paris", "Île-de-France", "75001", "France"
                        )
                ),
                // Case: Invalid phone format
                Arguments.of(
                        "Invalid phone format",
                        new CustomerCreationRequest(
                                "John", "Doe", "john.doe@example.com", "123456",
                                "42", "Main Street", "Paris", "Île-de-France", "75001", "France"
                        )
                ),
                // Case: Empty street number (will fail domain validation)
                Arguments.of(
                        "Empty street number",
                        new CustomerCreationRequest(
                                "John", "Doe", "john.doe@example.com", "+33612345678",
                                "", "Main Street", "Paris", "Île-de-France", "75001", "France"
                        )
                )
        );
    }

    @ParameterizedTest(name = "Invalid customer validation: {0}")
    @MethodSource("invalidCustomerRequests")
    void whenCreateInvalidCustomer_thenReturnBadRequest_E2E(String testName, CustomerCreationRequest invalidRequest) {
        // Create HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Execute the POST request with invalid data
        ResponseEntity<ProblemDetail> response = restTemplate.postForEntity(
                "/customers",
                new HttpEntity<>(invalidRequest, headers),
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
                "Jane", "Smith", "jane.smith@example.com", "+33612345679",
                "123", "Rue Nationale", "Lyon", "Auvergne-Rhône-Alpes", "69002", "France"
        );

        // Map to domain model
        Customer customer = validRequest.toDomain();

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
        return Stream.of(
                // Testing all required fields with more detailed descriptions
                Arguments.of("Empty first name",
                        new CustomerCreationRequest("", "Doe", "john.doe@example.com", "+33612345678",
                                "42", "Main Street", "Paris", "Île-de-France", "75001", "France"),
                        "firstName", "First name is required"),

                Arguments.of("Blank first name",
                        new CustomerCreationRequest("   ", "Doe", "john.doe@example.com", "+33612345678",
                                "42", "Main Street", "Paris", "Île-de-France", "75001", "France"),
                        "firstName", "First name is required"),

                Arguments.of("Empty last name",
                        new CustomerCreationRequest("John", "", "john.doe@example.com", "+33612345678",
                                "42", "Main Street", "Paris", "Île-de-France", "75001", "France"),
                        "lastName", "Last name is required"),

                Arguments.of("Invalid email format",
                        new CustomerCreationRequest("John", "Doe", "not-an-email", "+33612345678",
                                "42", "Main Street", "Paris", "Île-de-France", "75001", "France"),
                        "email", "must be a well-formed email address"),

                Arguments.of("Empty email",
                        new CustomerCreationRequest("John", "Doe", "", "+33612345678",
                                "42", "Main Street", "Paris", "Île-de-France", "75001", "France"),
                        "email", "Email is required"),

                Arguments.of("Empty phone number",
                        new CustomerCreationRequest("John", "Doe", "john.doe@example.com", "",
                                "42", "Main Street", "Paris", "Île-de-France", "75001", "France"),
                        "phoneNumber", "Phone number is required"),

                Arguments.of("Invalid phone format",
                        new CustomerCreationRequest("John", "Doe", "john.doe@example.com", "not-a-phone",
                                "42", "Main Street", "Paris", "Île-de-France", "75001", "France"),
                        "phoneNumber", "Invalid phone number format"),

                // Address field validations
                Arguments.of("Empty street number",
                        new CustomerCreationRequest("John", "Doe", "john.doe@example.com", "+33612345678",
                                "", "Main Street", "Paris", "Île-de-France", "75001", "France"),
                        "streetNumber", "Street number number is required"),

                Arguments.of("Empty street",
                        new CustomerCreationRequest("John", "Doe", "john.doe@example.com", "+33612345678",
                                "42", "", "Paris", "Île-de-France", "75001", "France"),
                        "street", "Street is required"),

                Arguments.of("Empty city",
                        new CustomerCreationRequest("John", "Doe", "john.doe@example.com", "+33612345678",
                                "42", "Main Street", "", "Île-de-France", "75001", "France"),
                        "city", "City number is required"),

                Arguments.of("Empty region",
                        new CustomerCreationRequest("John", "Doe", "john.doe@example.com", "+33612345678",
                                "42", "Main Street", "Paris", "", "75001", "France"),
                        "region", "Region is required"),

                Arguments.of("Empty postal code",
                        new CustomerCreationRequest("John", "Doe", "john.doe@example.com", "+33612345678",
                                "42", "Main Street", "Paris", "Île-de-France", "", "France"),
                        "postalCode", "Postal code is required"),

                Arguments.of("Empty country",
                        new CustomerCreationRequest("John", "Doe", "john.doe@example.com", "+33612345678",
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
        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerCreationRequest> requestEntity = new HttpEntity<>(invalidRequest, headers);

        // Make request
        ResponseEntity<ProblemDetail> response = restTemplate.postForEntity(
                "/customers",
                requestEntity,
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
                "", "", "", "",
                "", "", "", "", "", ""
        );

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerCreationRequest> requestEntity = new HttpEntity<>(invalidRequest, headers);

        // Make request
        ResponseEntity<ProblemDetail> response = restTemplate.postForEntity(
                "/customers",
                requestEntity,
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