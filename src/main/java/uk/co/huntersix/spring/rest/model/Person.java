package uk.co.huntersix.spring.rest.model;

import javax.validation.constraints.NotNull;
import java.util.concurrent.atomic.AtomicLong;

public class Person {
    //should be static
    private final static AtomicLong counter = new AtomicLong();

    private Long id;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;

    private Person() {
        // empty
    }

    public Person(String firstName, String lastName) {
        this.id = counter.incrementAndGet();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void bindId() {
        if (this.id == null) {
            this.id = counter.incrementAndGet();
        }
    }
}
