package test;
import teachers.MuseumOfficial;

public class MuseumOfficialTest {
    public static void main(String[] args) {
        System.out.println("Starting... ");
        long startTime = System.nanoTime();
                
        MuseumOfficial.main(new String[0]);
        
        long runTime = System.nanoTime();
        System.out.printf("Ran in %f.4 ms.\n", (runTime-startTime)/1e6);
    }
}
