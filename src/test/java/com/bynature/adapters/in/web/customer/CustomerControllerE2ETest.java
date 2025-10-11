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
        String requestJson = """
        {
            "firstName": "Jane",
            "lastName": "Doe",
            "password": "Str0ngP@ssword123!",
            "email": "jane.doe@example.com",
            "phoneNumber": "+33612345679",
            "streetNumber": "42",
            "street": "Main Street",
            "city": "Paris",
            "region": "Île-de-France",
            "postalCode": "75001",
            "country": "France"
        }
        """;

        // Use exchange with authenticated entity containing request body
        ResponseEntity<UUID> response = restTemplate.exchange(
                "/customers",
                HttpMethod.POST,
                createAuthenticatedEntity(requestJson),
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
        assertThat(customer.firstName()).isEqualTo("Jane");
        assertThat(customer.lastName()).isEqualTo("Doe");
        assertThat(customer.email()).isEqualTo("jane.doe@example.com");
    }

    private static Stream<Arguments> invalidCustomerRequests() {
        return Stream.of(
                // Case: Empty first name
                Arguments.of(
                        "Empty first name",
                        """
                        {
                            "firstName": "",
                            "lastName": "Doe",
                            "password": "Str0ngP@ssword123!",
                            "email": "john.doe@example.com",
                            "phoneNumber": "+33612345678",
                            "streetNumber": "42",
                            "street": "Main Street",
                            "city": "Paris",
                            "region": "Île-de-France",
                            "postalCode": "75001",
                            "country": "France"
                        }
                        """
                ),
                // Case: Missing last name
                Arguments.of(
                        "Missing last name",
                        """
                        {
                            "firstName": "John",
                            "lastName": "",
                            "password": "Str0ngP@ssword123!",
                            "email": "john.doe@example.com",
                            "phoneNumber": "+33612345678",
                            "streetNumber": "42",
                            "street": "Main Street",
                            "city": "Paris",
                            "region": "Île-de-France",
                            "postalCode": "75001",
                            "country": "France"
                        }
                        """
                ),
                // Case: Invalid email format
                Arguments.of(
                        "Invalid email format",
                        """
                        {
                            "firstName": "John",
                            "lastName": "Doe",
                            "password": "Str0ngP@ssword123!",
                            "email": "invalid-email",
                            "phoneNumber": "+33612345678",
                            "streetNumber": "42",
                            "street": "Main Street",
                            "city": "Paris",
                            "region": "Île-de-France",
                            "postalCode": "75001",
                            "country": "France"
                        }
                        """
                ),
                // Case: Invalid phone format
                Arguments.of(
                        "Invalid phone format",
                        """
                        {
                            "firstName": "John",
                            "lastName": "Doe",
                            "password": "Str0ngP@ssword123!",
                            "email": "john.doe@example.com",
                            "phoneNumber": "123456",
                            "streetNumber": "42",
                            "street": "Main Street",
                            "city": "Paris",
                            "region": "Île-de-France",
                            "postalCode": "75001",
                            "country": "France"
                        }
                        """
                ),
                // Case: Empty street number
                Arguments.of(
                        "Empty street number",
                        """
                        {
                            "firstName": "John",
                            "lastName": "Doe",
                            "password": "Str0ngP@ssword123!",
                            "email": "john.doe@example.com",
                            "phoneNumber": "+33612345678",
                            "streetNumber": "",
                            "street": "Main Street",
                            "city": "Paris",
                            "region": "Île-de-France",
                            "postalCode": "75001",
                            "country": "France"
                        }
                        """
                )
        );
    }

    @ParameterizedTest(name = "Invalid customer validation: {0}")
    @MethodSource("invalidCustomerRequests")
    void whenCreateInvalidCustomer_thenReturnBadRequest_E2E(String testName, String invalidRequestJson) {
        // Use exchange with authenticated entity containing invalid request body
        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                "/customers",
                HttpMethod.POST,
                createAuthenticatedEntity(invalidRequestJson),
                ProblemDetail.class
        );

        // Verify HTTP 400 Bad Request status
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
    }


    @Test
    @DisplayName("Should successfully map valid CustomerCreationRequest to domain model")
    void shouldMapValidRequestToDomainModel() {
        // Create valid request as JSON string
        String validRequestJson = """
    {
        "firstName": "Jane",
        "lastName": "Smith",
        "password": "Str0ngP@ssword123!",
        "email": "jane.smith@example.com",
        "phoneNumber": "+33612345679",
        "streetNumber": "123",
        "street": "Rue Nationale",
        "city": "Lyon",
        "region": "Auvergne-Rhône-Alpes",
        "postalCode": "69002",
        "country": "France"
    }
    """;

        // Make actual API call to create the customer
        ResponseEntity<UUID> createResponse = restTemplate.exchange(
                "/customers",
                HttpMethod.POST,
                createAuthenticatedEntity(validRequestJson),
                UUID.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();

        UUID customerId = createResponse.getBody();

        // Get the created customer from the API
        ResponseEntity<CustomerRetrievalResponse> getResponse = restTemplate.exchange(
                "/customers/{id}",
                HttpMethod.GET,
                createAuthenticatedEntity(),
                CustomerRetrievalResponse.class,
                customerId
        );

        // Verify mapping is correct by checking the retrieved customer
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        CustomerRetrievalResponse customer = getResponse.getBody();
        assertThat(customer).isNotNull();
        assertThat(customer.firstName()).isEqualTo("Jane");
        assertThat(customer.lastName()).isEqualTo("Smith");
        assertThat(customer.email()).isEqualTo("jane.smith@example.com");
        assertThat(customer.phoneNumber()).isEqualTo("+33612345679");
        assertThat(customer.streetNumber()).isEqualTo("123");
        assertThat(customer.street()).isEqualTo("Rue Nationale");
        assertThat(customer.city()).isEqualTo("Lyon");
        assertThat(customer.region()).isEqualTo("Auvergne-Rhône-Alpes");
        assertThat(customer.postalCode()).isEqualTo("69002");
        assertThat(customer.country()).isEqualTo("France");
    }


    private static Stream<Arguments> customerRequestValidationTestCases() {
        return Stream.of(
                // Empty first name
                Arguments.of("Empty first name",
                        """
                        {
                            "firstName": "",
                            "lastName": "Doe",
                            "password": "Str0ngP@ssword123!",
                            "email": "john.doe@example.com",
                            "phoneNumber": "+33612345678",
                            "streetNumber": "42",
                            "street": "Main Street",
                            "city": "Paris",
                            "region": "Île-de-France",
                            "postalCode": "75001",
                            "country": "France"
                        }
                        """,
                        "firstName", "First name is required"),
                // Empty country (add other test cases similarly)
                Arguments.of("Empty country",
                        """
                        {
                            "firstName": "John",
                            "lastName": "Doe",
                            "password": "Str0ngP@ssword123!",
                            "email": "john.doe@example.com",
                            "phoneNumber": "+33612345678",
                            "streetNumber": "42",
                            "street": "Main Street",
                            "city": "Paris",
                            "region": "Île-de-France",
                            "postalCode": "75001",
                            "country": ""
                        }
                        """,
                        "country", "Country is required")
                // Add other test cases as needed
        );
    }


    @ParameterizedTest(name = "{0}")
    @MethodSource("customerRequestValidationTestCases")
    @DisplayName("Should validate CustomerCreationRequest fields")
    void shouldValidateCustomerCreationRequestFields(String testName,
                                                     String invalidRequestJson,
                                                     String fieldName,
                                                     String expectedErrorMessage) {
        // Use exchange with authenticated entity containing the invalid request
        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                "/customers",
                HttpMethod.POST,
                createAuthenticatedEntity(invalidRequestJson),
                ProblemDetail.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();

        // Extract validation error details from response body
        Map<String, Object> responseBody = response.getBody().getProperties();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody).containsKey("validationErrors");

        @SuppressWarnings("unchecked")
        List<String> violations = (List<String>) responseBody.get("validationErrors");

        // Assert that the validation contains the expected field error
        boolean hasExpectedViolation = violations.stream()
                .anyMatch(violation -> violation.contains(fieldName));

        assertThat(hasExpectedViolation).isTrue();
    }

}