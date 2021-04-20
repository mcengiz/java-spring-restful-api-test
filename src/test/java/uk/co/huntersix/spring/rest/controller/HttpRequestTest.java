package uk.co.huntersix.spring.rest.controller;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.co.huntersix.spring.rest.model.Person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void init() {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        }
    }

    @Test
    public void shouldReturnPersonDetails() throws Exception {
        assertThat(
                this.restTemplate.getForObject(
                        getBaseUrl() + "/person/smith/mary",
                        String.class
                )
        ).contains("Mary");
    }

    @Test
    public void shouldReturnNotFoundHttpStatus_whenPersonNotFound_givenFirstNameAndLastName() {
        assertThat(
                this.restTemplate.getForEntity(
                        getBaseUrl() + "/person/smith/mary2",
                        String.class
                ).getStatusCode()
        ).isEqualTo(NOT_FOUND);
    }

    @Test
    public void shouldReturnPersonList_whenPersonFound_givenLastName() {
        ResponseEntity<Person[]> resp = this.restTemplate.getForEntity(getBaseUrl() + "/person/brown", Person[].class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().length).isEqualTo(1);
        assertThat(resp.getBody()[0].getLastName()).isEqualTo("Brown");
    }

    @Test
    public void shouldReturnNotFoundHttpStatus_whenPersonNotFound_givenLastName() {
        assertThat(this.restTemplate.getForEntity(getBaseUrl() + "/person/brown1", Person.class).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldReturnConflictHttpStatus_whenPersonAlreadyExist_givenFirstNameAndLastName() throws Exception {
        JSONObject request = new JSONObject();
        request.put("firstName", "Collin");
        request.put("lastName", "Brown");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(getBaseUrl() + "/person")
                .accept(MediaType.APPLICATION_JSON)
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    public void shouldAddPerson_whenPersonNotExist_givenUniqueNameAndSurname() throws Exception {
        JSONObject request = new JSONObject();
        request.put("firstName", "Mehmet");
        request.put("lastName", "Cengiz");

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                .post(getBaseUrl() + "/person")
                .accept(MediaType.APPLICATION_JSON)
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)).andReturn();

        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());

        assertThat(obj.getString("id")).isNotBlank();
        assertThat(obj.getString("firstName")).isEqualTo("Mehmet");
        assertThat(obj.getString("lastName")).isEqualTo("Cengiz");
    }

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }
}