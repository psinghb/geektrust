package com.company;

import com.company.exceptions.OperationFailedException;
import com.company.exceptions.PersonNotFoundException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {

    public static final String CHILD_ADDITION_SUCCEEEDED = "CHILD_ADDITION_SUCCEEEDED";
    public static final String PERSON_NOT_FOUND = "PERSON_NOT_FOUND";
    public static final String CHILD_ADDITION_FAILED = "CHILD_ADDITION_FAILED";
    public static final String GET_RELATIONSHIP = "GET_RELATIONSHIP";
    public static final String NONE = "NONE";
    public static final String ADD_CHILD = "ADD_CHILD";
    public static final String MALE = "Male";
    public static final String FEMALE = "Female";
    public static final String ERROR_IN_FILE = "Error in file";





    public static void main(String[] args) {
        FamilyTree familyTree = init();

        if(args == null || args.length == 0) {
            throw new IllegalArgumentException("No args present - Please specify full file path as argument 0");
        }

        File file = new File(args[0]);
        if(!file.exists()) {
            throw new IllegalArgumentException(String.format("File at given path %s does not exists", args[0]));
        }


        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while (line != null) {
                String[] tokens = line.split("\\s+");
                String command = tokens[0];
                validate(tokens);
                if(command.equals(ADD_CHILD)) {
                    String mother = tokens[1];
                    String child = tokens[2];
                    boolean male = tokens[3].equals(MALE);
                    try {
                        if (male) {
                            familyTree.addMaleChild(child, mother);
                        } else {
                            familyTree.addFeMaleChild(child, mother);
                        }

                        System.out.println(CHILD_ADDITION_SUCCEEEDED);
                    } catch (PersonNotFoundException e) {
                        System.out.println(PERSON_NOT_FOUND);
                    } catch (OperationFailedException e) {
                        System.out.println(CHILD_ADDITION_FAILED);
                    }
                } else if(command.equals(GET_RELATIONSHIP)) {
                    String of = tokens[1];
                    Relationship relationship = Relationship.from(tokens[2]);
                    Set<Person> personSet = familyTree.getRelations(of, relationship);
                    if(personSet == null) {
                        System.out.println(PERSON_NOT_FOUND);
                    } else if(personSet.isEmpty()) {
                        System.out.println(NONE);
                    } else {
                        System.out.println(personSet.stream().map(Person::getName).collect(Collectors.joining(" ")));
                    }
                } else {
                    throw new RuntimeException(String.format("Error in file, unrecognised token - %s at line `%d`", command, line));
                }
                line = reader.readLine();
            }
        }catch (IOException e) {
            throw new RuntimeException("Some error occurred while processing file",e);
        }


    }

    /**
     * validates any given line in the input line
     * @param tokens
     */
    public static void validate(String[] tokens) {
        String command = tokens[0];
        if(!(command.equals(ADD_CHILD) || command.equals(GET_RELATIONSHIP))) {
            throw new RuntimeException();
        }

        if(command.equals(ADD_CHILD)) {
            if(tokens.length != 4) {
                throw new RuntimeException(ERROR_IN_FILE);
            }

            if(!MALE.equals(tokens[3]) || !FEMALE.equals(tokens[3])) {
                throw new RuntimeException(ERROR_IN_FILE);
            }
        }
    }

    /**
     * initialize the family tree and return
     * @return
     */
    public static FamilyTree init() {
        FamilyTree familyTree = new FamilyTree();
        familyTree.buildInitialTree();
        tests(familyTree);
        return familyTree;
    }

    /**
     * I added this for internal dev testing only
     * @param familyTree
     */
    protected static void tests(FamilyTree familyTree) {
        assert familyTree.getSize() == 31;

        Set<Person> persons = familyTree.getRelations("Vasa", Relationship.Siblings);
        assert persons.isEmpty();
        persons = familyTree.getRelations("Tritha", Relationship.Siblings);
        assert persons.size() == 2;
        persons = familyTree.getRelations("Vila", Relationship.Siblings);
        assert persons.size() == 1;

        persons = familyTree.getRelations("Dritha", Relationship.Maternal_Aunt);
        assert persons.size() == 0;

        persons = familyTree.getRelations("Dritha", Relationship.Paternal_Aunt);
        assert persons.size() == 1;

        persons = familyTree.getRelations("Vicha", Relationship.Son);
        assert persons == null;

        persons = familyTree.getRelations("Vich", Relationship.Son);
        assert persons.size() == 0;

        persons = familyTree.getRelations("Vich", Relationship.Daughter);
        assert persons.size() == 2;

        persons = familyTree.getRelations("Jaya", Relationship.Sister_In_Law);
        assert persons.size() == 1;
        assert persons.stream().findFirst().get().getName().equals("Tritha");

        persons = familyTree.getRelations("Jaya", Relationship.Brother_In_Law);
        assert persons.size() == 1;
        assert persons.stream().findFirst().get().getName().equals("Vritha");

        persons = familyTree.getRelations("Dritha", Relationship.Maternal_Uncle);
        assert persons.size() == 0;

        persons = familyTree.getRelations("Dritha", Relationship.Paternal_Uncle);
        assert persons.size() == 3;

        assert persons.stream().anyMatch(it -> it.getName().equals("Ish"));
        assert persons.stream().anyMatch(it -> it.getName().equals("Vich"));
        assert persons.stream().anyMatch(it -> it.getName().equals("Aras"));

        persons = familyTree.getRelations("Asva", Relationship.Maternal_Uncle);
        assert persons.size() == 4;

        assert persons.stream().anyMatch(it -> it.getName().equals("Ish"));
        assert persons.stream().anyMatch(it -> it.getName().equals("Vich"));
        assert persons.stream().anyMatch(it -> it.getName().equals("Aras"));
        assert persons.stream().anyMatch(it -> it.getName().equals("Chit"));

    }
}
