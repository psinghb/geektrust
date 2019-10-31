package com.company;

import com.company.exceptions.OperationFailedException;
import com.company.exceptions.PersonNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

public class FamilyTree {

    private Map<String, Person> family = new HashMap<>();

    public int getSize() {
        return family.size();
    }

    public void buildInitialTree() {
        Person king = addPerson(true, "King Shan");
        Person queen = addPerson(false, "Queen Anga", king);

        addMaleChild("Chit", queen);
        addMaleChild("Ish", queen);
        addMaleChild("Aras", queen);
        addMaleChild("Vich", queen);
        addFeMaleChild("Satya", queen);


        Person amba = setSpouse("Chit", "Amba");
        Person lika = setSpouse("Vich", "Lika");
        Person chitra = setSpouse("Aras", "Chitra");
        setSpouse("Satya", "Vyan");

        addMaleChild("Vritha", amba);
        addFeMaleChild("Dritha", amba);
        addFeMaleChild("Tritha", amba);

        Person jaya = setSpouse("Dritha", "Jaya");
        addMaleChild("Yodhan", "Dritha");

        addFeMaleChild("Vila", lika);
        addFeMaleChild("Chika", lika);

        addFeMaleChild("Jnki", chitra);
        addMaleChild("Ahit", chitra);
        setSpouse("Jnki", "Arit");
        addMaleChild("Laki", "Jnki");
        addFeMaleChild("Lavnya", "Jnki");


        addMaleChild("Asva", "Satya");
        addMaleChild("Vyas", "Satya");
        addFeMaleChild("Atya", "Satya");
        setSpouse("Asva", "Satvy");
        setSpouse("Vyas", "Krpi");

        addMaleChild("Vasa", "Satvy");
        addMaleChild("Kriya","Krpi");
        addFeMaleChild("Krithi","Krpi");
    }

    public Set<Person> getRelations(String of, Relationship relationship) {
        if(!family.containsKey(of)) {
            return null;// person not found
        }

        Person ofPerson = family.get(of);
        switch (relationship) {
            case Son:
                Person momSpouse = ofPerson;
                if(ofPerson.isMale) {
                    momSpouse = ofPerson.getSpouse();
                }

                if(momSpouse == null) {
                    return null;
                }

                return momSpouse.getChildren().stream().filter(it -> it.isMale).collect(Collectors.toSet());
            case Daughter:
                momSpouse = ofPerson;
                if(ofPerson.isMale) {
                    momSpouse = ofPerson.getSpouse();
                }
                if(momSpouse == null) {
                    return null;
                }

                return momSpouse.getChildren().stream().filter(it -> !it.isMale).collect(Collectors.toSet());
            case Siblings:
                if(ofPerson.getMom() == null) {
                    return Collections.EMPTY_SET;
                }
                return ofPerson.getMom().getChildren().stream().filter(it -> !it.getName().equals(of)).collect(Collectors.toSet());
            case Maternal_Aunt:
                if(ofPerson.getMom() == null || ofPerson.getMom().getMom() == null) {
                    return Collections.EMPTY_SET;
                }
                Person mom = ofPerson.getMom();
                return ofPerson.getMom().getMom().getChildren().stream().filter(it -> !it.isMale() && !it.equals(mom)).collect(Collectors.toSet());
            case Paternal_Aunt:
                if(ofPerson.getMom() == null || ofPerson.getMom().getSpouse() == null || ofPerson.getMom().getSpouse().getMom() == null) {
                    return Collections.EMPTY_SET;
                }
                Person dad = ofPerson.getMom().getSpouse();
                return ofPerson.getMom().getSpouse().getMom().getChildren().stream().filter(it -> !it.isMale && !it.equals(dad)).collect(Collectors.toSet());
            case Paternal_Uncle:
                if(ofPerson.getMom() == null || ofPerson.getMom().getSpouse() == null || ofPerson.getMom().getSpouse().getMom() == null) {
                    return Collections.EMPTY_SET;
                }
                dad = ofPerson.getMom().getSpouse();
                return ofPerson.getMom().getSpouse().getMom().getChildren().stream().filter(it -> it.isMale && !it.equals(dad)).collect(Collectors.toSet());
            case Maternal_Uncle:
                if(ofPerson.getMom() == null || ofPerson.getMom().getMom() == null) {
                    return Collections.EMPTY_SET;
                }
                mom = ofPerson.getMom();
                return ofPerson.getMom().getMom().getChildren().stream().filter(it -> it.isMale() && !it.equals(mom)).collect(Collectors.toSet());
            case Sister_In_Law:
                Set<Person> desiredResult = new HashSet<>();
                Person spouse = ofPerson.getSpouse();
                if(spouse != null && spouse.getMom()!= null) {
                    spouse.getMom().getChildren().stream().filter(it -> !it.isMale() && !it.equals(spouse)).forEach(desiredResult::add);
                }
                if(ofPerson.getMom() != null) {
                    ofPerson.getMom().getChildren().stream().filter(it -> it.isMale() && !it.equals(ofPerson)).map(Person::getSpouse).forEach(desiredResult::add);
                }
                return desiredResult;

            case Brother_In_Law:
                desiredResult = new HashSet<>();
                spouse = ofPerson.getSpouse();
                if(spouse != null && spouse.getMom()!= null) {
                    spouse.getMom().getChildren().stream().filter(it -> it.isMale() && !it.equals(spouse)).forEach(desiredResult::add);
                }
                if(ofPerson.getMom() != null) {
                    ofPerson.getMom().getChildren().stream().filter(it -> !it.isMale() && !it.equals(ofPerson)).map(Person::getSpouse).forEach(desiredResult::add);
                }
                return desiredResult;

            default:
                throw new IllegalArgumentException("Unknown type - " + relationship);
        }
    }

    protected Person setSpouse(String name, String spouse) {
        Person person = family.get(name);
        person.setSpouse(addPerson(!person.isMale(), spouse));
        person.spouse.setSpouse(person);
        return person.spouse;
    }

    protected Person addMaleChild(String name, Person mom) {
        if(mom.isMale) {
            throw new IllegalArgumentException("Mom cannot be male - " +  mom.getName());
        }
        Person child = addPerson(true, name);
        child.setMom(mom);

        mom.getChildren().add(child);
        return child;
    }

    protected Person addMaleChild(String name, String mom) {
        if(!family.containsKey(mom)) {
            throw new PersonNotFoundException();
        }
        if(family.get(mom).isMale) {
            throw new OperationFailedException("Mom cannot be male - " +  mom);
        }
        Person child = addPerson(true, name);
        child.setMom(family.get(mom));
        family.get(mom).getChildren().add(child);
        return child;
    }

    protected Person addFeMaleChild(String name, Person mom) {

        if(!family.containsKey(mom.name)) {
            throw new PersonNotFoundException();
        }
        if(family.get(mom.name).isMale) {
            throw new OperationFailedException("Mom cannot be male - " +  mom);
        }
        Person child = addPerson(false, name);
        child.setMom(mom);
        mom.getChildren().add(child);
        return child;
    }

    protected Person addFeMaleChild(String name, String mom) {
        if(family.get(mom).isMale) {
            throw new IllegalArgumentException("Mom cannot be male - " +  mom);
        }
        Person child = addPerson(false, name);
        child.setMom(family.get(mom));
        family.get(mom).getChildren().add(child);
        return child;
    }

    protected Person addPerson(boolean isMale, String name) {
        if(family.containsKey(name)) {
            throw new IllegalArgumentException("Family member already exists - " + name);
        }
        Person person = new Person(isMale, name);
        family.put(name, person);
        return person;
    }
    protected Person addPerson(boolean isMale, String name, Person spouse) {
        if(family.containsKey(name)) {
            throw new IllegalArgumentException("Family member already exists - " + name);
        }

        Person person = new Person(isMale, name, spouse);
        family.put(name, person);
        return person;
    }

}
