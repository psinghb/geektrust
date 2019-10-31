package com.company;

import java.util.stream.Stream;

public enum Relationship {
    Paternal_Uncle("Paternal-Uncle"),
    Maternal_Uncle("Maternal-Uncle"),
    Paternal_Aunt("Paternal-Aunt"),
    Maternal_Aunt("Maternal-Aunt"),
    Sister_In_Law("Sister-In-Law"),
    Brother_In_Law("Brother-In-Law"),
    Son("Son"),
    Daughter("Daughter"),
    Siblings("Siblings");

    String displayName;
    Relationship(String d) {
        displayName = d;
    }

    static Relationship from(String d) {
        return Stream.of(Relationship.values()).filter(it-> it.displayName.equals(d)).findFirst().orElseThrow(RuntimeException::new);
    }

}
