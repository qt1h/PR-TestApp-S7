package fr.uha.ensisa.gl.testsquad.mantest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Représente une suite de tests manuels.
 */
public class ManualSuiteTest {

    private long id;
    private String name;
    private String description;
    private final List<ManualTest> tests;

    public ManualSuiteTest() {
        this.tests = new ArrayList<>();
    }

    public ManualSuiteTest(long id, String name) {
        this.id = id;
        this.name = name;
        this.tests = new ArrayList<>();
    }

    public ManualSuiteTest(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tests = new ArrayList<>();
    }

    // Accesseurs
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retourne une copie défensive de la liste des tests.
     */
    public List<ManualTest> getTests() {
        return new ArrayList<>(tests);
    }

    /**
     * Ajoute un test à la suite.
     * @param test le test à ajouter.
     */
    public void addTest(ManualTest test) {
        tests.add(test);
    }

    /**
     * Supprime un test donné de la suite.
     * @param test le test à supprimer.
     */
    public void removeTest(ManualTest test) {
        tests.remove(test);
    }

    public void removeAllTests() {
        // TODO Auto-generated method stub
        tests.clear();
    }

    /**
     * Supprime un test de la suite en fonction de son identifiant.
     * @param testId l'identifiant du test à supprimer.
     */
    public void removeTestById(long testId) {
        tests.removeIf(t -> t.getId() == testId);
    }

    @Override
    public String toString() {
        return "ManualSuiteTest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", tests=" + tests +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ManualSuiteTest)) return false;
        ManualSuiteTest that = (ManualSuiteTest) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(tests, that.tests);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, tests);
    }

}
