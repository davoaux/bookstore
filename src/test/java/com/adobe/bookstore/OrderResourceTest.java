package com.adobe.bookstore;

import com.adobe.bookstore.dto.OrderResponseDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

// TODO Configure a testRestTemplate bean to set the root url https://stackoverflow.com/a/59143242
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD) // Using this annotation is quite expensive performance-wise. Find better way
public class OrderResourceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Sql(statements = "DELETE FROM book_order WHERE true")
    public void shouldReturnEmptyListOfBookOrders() {
        var result = restTemplate.getForEntity("http://localhost:" + port + "/orders/", OrderResponseDTO[].class);
        OrderResponseDTO[] orders = result.getBody();

        assertThat(orders).isEmpty();
    }

    @Test
    @Sql(statements = "INSERT INTO book_order (id) VALUES (1L);" +
            "INSERT INTO book_order (id) VALUES (2L);")
    public void shouldReturnTwoBookOrders() {
        var result = restTemplate.getForEntity("http://localhost:" + port + "/orders/", OrderResponseDTO[].class);
        OrderResponseDTO[] orders = result.getBody();

        assertThat(orders).hasSize(2);
        assertThat(orders[0].getClass()).isEqualTo(OrderResponseDTO.class);
    }

    @Test
    public void createOrderShouldReturnOrderId() throws JSONException {
        JSONObject jsonBook1 = new JSONObject();
        jsonBook1.put("book", "58716995-b335-4bb0-89c1-3503bc003118");  // Has a quantity of 5
        jsonBook1.put("quantity", 1);
        JSONObject jsonBook2 = new JSONObject();
        jsonBook2.put("book", "e415e3af-e87e-47e6-9bf2-f08c72e2f281"); // Has a quantity of 4
        jsonBook2.put("quantity", 4);
        JSONArray json = new JSONArray(List.of(jsonBook1, jsonBook2));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<String>(json.toString(), headers);

        var result = restTemplate.postForEntity("http://localhost:" + port + "/orders/", entity, Long.class);

        assertThat(result.getBody()).isEqualTo(1L);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void createOrderShouldReturnNotEnoughStockError() throws JSONException {
        JSONObject jsonBook1 = new JSONObject();
        jsonBook1.put("book", "e415e3af-e87e-47e6-9bf2-f08c72e2f281"); // Has a quantity of 4
        jsonBook1.put("quantity", 5);
        JSONArray json = new JSONArray(List.of(jsonBook1));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<String>(json.toString(), headers);

        var result = restTemplate.postForEntity("http://localhost:" + port + "/orders/", entity, String.class);

        assertThat(result.getBody()).isOfAnyClassIn(String.class);
        assertThat(result.getBody()).isEqualTo("There is not enough stock for the book: e415e3af-e87e-47e6-9bf2-f08c72e2f281");
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    public void createOrderOfSameBookMultipleTimesShouldReturnNotEnoughStockError() throws JSONException {
        JSONObject jsonBook1 = new JSONObject();
        jsonBook1.put("book", "e415e3af-e87e-47e6-9bf2-f08c72e2f281"); // Has a quantity of 4
        jsonBook1.put("quantity", 3);
        JSONObject jsonBook2 = new JSONObject();
        jsonBook2.put("book", "e415e3af-e87e-47e6-9bf2-f08c72e2f281"); // Same book
        jsonBook2.put("quantity", 4);
        JSONArray json = new JSONArray(List.of(jsonBook1, jsonBook2));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<String>(json.toString(), headers);

        var result = restTemplate.postForEntity("http://localhost:" + port + "/orders/", entity, String.class);

        assertThat(result.getBody()).isOfAnyClassIn(String.class);
        assertThat(result.getBody()).isEqualTo("There is not enough stock for the book: e415e3af-e87e-47e6-9bf2-f08c72e2f281");
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    public void createOrderShouldReturnItemsAmountError() throws JSONException {
        JSONArray json = new JSONArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<String>(json.toString(), headers);

        var result = restTemplate.postForEntity("http://localhost:" + port + "/orders/", entity, String.class);

        assertThat(result.getBody()).isOfAnyClassIn(String.class);
        assertThat(result.getBody()).isEqualTo("An order must have at least 1 order item");
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
