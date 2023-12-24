package test;
import alonso.Museum;
import alonso.Person;

public class MuseumTest {
    public static void main(String[] args) {
        System.out.println("Starting... ");
        long startTime = System.nanoTime();
        
        Person[] people = Museum.setupPeople(Museum.setupMuseum());
        
        long setupTime = System.nanoTime();
        System.out.printf("Setup in %f.4 ms.\n", (setupTime-startTime)/1e6);
        
        Museum.runPeople(people);
        
        long runTime = System.nanoTime();
        System.out.printf("Ran in %f.4 ms.\n", (runTime-setupTime)/1e6);

        System.out.printf("Total time: %f.2ms.\n", (runTime-startTime)/1e6);
    }
}
