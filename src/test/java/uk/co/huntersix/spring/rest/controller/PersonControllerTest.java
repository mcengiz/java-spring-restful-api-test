package uk.co.huntersix.spring.rest.controller;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.huntersix.spring.rest.exception.PersonAlreadyExistException;
import uk.co.huntersix.spring.rest.exception.PersonNotFoundException;
import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonDataService personDataService;

    @Test
    public void shouldReturnPersonFromService() throws Exception {
        when(personDataService.findPerson(any(), any())).thenReturn(new Person("Mary", "Smith"));
        this.mockMvc.perform(get("/person/smith/mary"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("firstName").value("Mary"))
            .andExpect(jsonPath("lastName").value("Smith"));
    }

    @Test
    public void shouldReturnNotFoundHttpStatus_whenPersonDoesNotExist_givenFirstNameAndLastName() throws Exception {
        when(personDataService.findPerson(any(), any())).thenThrow(new PersonNotFoundException());
        this.mockMvc.perform(get("/person/smith/mary2"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnPersonListFromService_givenLastName() throws Exception {
        Person person1 = new Person("name1", "surname");
        Person person2 = new Person("name2", "surname");
        Person person3 = new Person("name3", "surname");

        when(personDataService.findPerson(any())).thenReturn(Arrays.asList(person1, person2, person3));
        this.mockMvc.perform(get("/person/surname"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].firstName", is("name1")))
                .andExpect(jsonPath("$[0].lastName", is("surname")))
                .andExpect(jsonPath("$[1].firstName", is("name2")))
                .andExpect(jsonPath("$[1].lastName", is("surname")))
                .andExpect(jsonPath("$[2].firstName", is("name3")))
                .andExpect(jsonPath("$[2].lastName", is("surname")));
    }

    @Test
    public void shouldReturnPersonFromService_givenLastName() throws Exception {
        Person person = new Person("name1", "surname");

        when(personDataService.findPerson(any())).thenReturn(Arrays.asList(person));
        this.mockMvc.perform(get("/person/surname"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("name1")))
                .andExpect(jsonPath("$[0].lastName", is("surname")));
    }

    @Test
    public void shouldReturnNotFoundHttpStatus_whenPersonDoesNotExist_givenLastName() throws Exception {
        when(personDataService.findPerson(any(), any())).thenThrow(new PersonNotFoundException("Error message"));
        this.mockMvc.perform(get("/person/smith/not_found"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldAddThePerson_givenFirstNameAndLastName() throws Exception {
        Person person = new Person("firstName", "lastName");
        when(personDataService.addPerson(any(Person.class))).thenReturn(person);

        this.mockMvc.perform(post("/person")
                .content(convertPersonToJson(person))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(person.getId()))
                .andExpect(jsonPath("$.firstName", is("firstName")))
                .andExpect(jsonPath("$.lastName", is("lastName")));
    }

    @Test
    public void shouldReturnConflictHttpStatus_givenFirstNameAndLastNameIsDuplicated() throws Exception {

        when(personDataService.addPerson(any(Person.class))).thenThrow(new PersonAlreadyExistException());

        this.mockMvc.perform(post("/person")
                .content(convertPersonToJson(new Person("firstName", "lastName")))
                .header(CONTENT_TYPE, APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    private String convertPersonToJson(Person person){
        Gson gson = new Gson();
        return gson.toJson(person);
    }
}