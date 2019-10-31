package com.company;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Person {
    boolean isMale;
    String name;

    Set<Person> children = new HashSet<>();
    Person spouse;
    Person mom;

    public Person(boolean isMale, String name) {
        this.isMale = isMale;
        this.name = name;
    }

    public Person(boolean isMale, String name, Person spouse) {
        this.isMale = isMale;
        this.name = name;
        this.spouse = spouse;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Person> getChildren() {
        return children;
    }

    public void setChildren(Set<Person> children) {
        this.children = children;
    }

    public Person getSpouse() {
        return spouse;
    }

    public void setSpouse(Person spouse) {
        this.spouse = spouse;
    }

    public Person getMom() {
        return mom;
    }

    public void setMom(Person mom) {
        this.mom = mom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return isMale == person.isMale &&
                name.equals(person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isMale, name);
    }
}
