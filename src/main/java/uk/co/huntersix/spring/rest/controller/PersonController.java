package uk.co.huntersix.spring.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
public class PersonController {
    private PersonDataService personDataService;

    public PersonController(@Autowired PersonDataService personDataService) {
        this.personDataService = personDataService;
    }

    @GetMapping("/person/{lastName}/{firstName}")
    public Person person(@PathVariable(value = "lastName") String lastName,
                         @PathVariable(value = "firstName") String firstName) {
        return personDataService.findPerson(lastName, firstName);
    }

    @GetMapping("/person/{lastName}")
    @ResponseStatus(HttpStatus.OK)
    public List<Person> person(@PathVariable(value = "lastName") String lastName) {
        return personDataService.findPerson(lastName);
    }

    @PostMapping("/person")
    @ResponseStatus(HttpStatus.CREATED)
    public Person addPerson(@RequestBody @NotNull @Valid Person person) {
        return personDataService.addPerson(person);
    }
}