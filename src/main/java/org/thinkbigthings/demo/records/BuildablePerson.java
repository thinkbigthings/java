package org.thinkbigthings.demo.records;

// This is one possible Builder
// Good news it's an easy one-liner per method, no separate Builder class, immutable by default, no .build() at the end
// Bad news is it's a lot of boilerplate so could be error prone, and creates new object per builder method call
public record BuildablePerson(String firstName, String lastName) {

    public BuildablePerson() {
        this("", "");
    }

    public static BuildablePerson newPerson() {
        return new BuildablePerson();
    }

    public BuildablePerson withFirstName(String newFirstName) {
        return new BuildablePerson(newFirstName, lastName);
    }

    public BuildablePerson withLastName(String newLastName) {
        return new BuildablePerson(firstName, newLastName);
    }
}
