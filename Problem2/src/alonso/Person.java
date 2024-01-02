package alonso;

import java.util.Random;

public class Person extends Thread {

    private static int finished;
    
    static class PersonalMap {
        final MuseumMap referenceMap;
        private int index;
        private Room.EntryPoint currEntryPoint;

        public PersonalMap(MuseumMap referenceMap) {
            this.referenceMap = referenceMap;
        }

        public Room.EntryPoint getNext() {
            if (currEntryPoint!=null)  index++;
            return currEntryPoint = (index < referenceMap.size())? referenceMap.get(index) : null;
        }
    }

    private int id;
    private PersonalMap map;
    private Room.EntryPoint curr;

    private Random rnd = new Random();

    Person(int id, MuseumMap map) {
        this.id = id;
        this.map = new PersonalMap(map);
    }

    private Room.EntryPoint advance() {
        moveTo(map.getNext());
        return curr;
    }

    private void moveTo(Room.EntryPoint entryPoint) {
        if (entryPoint == null)  moveTo(null, 0);
        else moveTo(entryPoint.room, entryPoint.doorN);
    }
    // private void moveTo(Room room) { moveTo(room, 0); }
    private void moveTo(Room room, int door) {
        if (room != null) {
            System.out.printf("[ %d ] Trying to enter room %s in pool %d\n", id, room.name, door);
            synchronized(room) {
                while (!room.tryEnter(door)) {
                    try { room.wait(); } catch (InterruptedException e) {}
                }
                System.out.printf("[ %d ] Entering room %s. (%d people, %d spaces in pool)\n", id, room.name, room.getTotal(), room.getPool(door));
            }
        }
        if (curr!=null) {
            synchronized(curr.room){
                curr.room.exit(curr.doorN);
                System.out.printf("[ %d ] Leaving room %s. (%d poeple, %d spaces in pool)\n", id, curr.room.name, curr.room.getTotal(), curr.room.getPool(curr.doorN));
            }
        }
        curr = (room==null)? null : new Room.EntryPoint(room, door);
    }

    public void run(){
        System.out.println("[ "+ id +" ] At museum door");

        while (advance() != null) {
            try { Thread.sleep(rnd.nextInt(1000)); } catch (InterruptedException e) {}
        }

        synchronized(this.getClass()) {
            System.out.printf("[ %d ] Finished the museum visit (%d finished)\n", id, ++finished);
        }
    }
}

