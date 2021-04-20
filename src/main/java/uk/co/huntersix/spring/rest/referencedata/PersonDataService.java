package uk.co.huntersix.spring.rest.referencedata;

import org.springframework.stereotype.Service;
import uk.co.huntersix.spring.rest.exception.PersonAlreadyExistException;
import uk.co.huntersix.spring.rest.exception.PersonNotFoundException;
import uk.co.huntersix.spring.rest.model.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonDataService {
    public static final List<Person> PERSON_DATA = new ArrayList<>(Arrays.asList(
            new Person("Mary", "Smith"),
            new Person("Brian", "Archer"),
            new Person("Collin", "Brown")
    ));

    public Person findPerson(String lastName, String firstName) {
        return PERSON_DATA.stream()
                .filter(p -> p.getFirstName().equalsIgnoreCase(firstName)
                        && p.getLastName().equalsIgnoreCase(lastName)).findFirst().orElseThrow(() -> new PersonNotFoundException());
    }

    public List<Person> findPerson(String lastName) {
        List<Person> people = PERSON_DATA.stream()
                .filter(p -> p.getLastName().equalsIgnoreCase(lastName))
                .collect(Collectors.toList());
        if (people.isEmpty()) {
            throw new PersonNotFoundException("Person with given surname is not found");
        }
        return people;
    }

    public Person addPerson(Person person) throws PersonAlreadyExistException {
        if (isPersonAlreadyExist(person)) {
            throw new PersonAlreadyExistException("Person with given firstName and lastName is already exist");
        }
        person.bindId();
        PERSON_DATA.add(person);
        return person;
    }

    private boolean isPersonAlreadyExist(Person person) {
        try {
            findPerson(person.getLastName(), person.getFirstName());
            return true;
        } catch (PersonNotFoundException ex) {
            return false;
        }
    }
}
