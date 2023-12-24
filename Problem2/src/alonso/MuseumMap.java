package alonso;

import java.util.ArrayList;

class MuseumMap extends ArrayList<Room.EntryPoint> {
    public boolean add(Room room, int doorN) {
        return add(new Room.EntryPoint(room, doorN));
    }
}
