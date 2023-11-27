/**JavaFile******************************************************************

   FileName    [Program that implements the Museum Problem]

   Synopsis [ Museum Problem template ]

   Author      [Iria Estevez-Ayres <ayres@it.uc3m.es>]

   Copyright   [Copyright (c) 2019 Carlos III University of Madrid
   All rights reserved.

   Permission is hereby granted, without written agreement and without license
   or royalty fees, to use, copy, modify, and distribute this software and its
   documentation for any purpose, provided that the above copyright notice and
   the following two paragraphs appear in all copies of this software.

   IN NO EVENT SHALL THE CARLOS III UNIVERSITY OF MADRID BE LIABLE TO ANY PARTY 
   FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING 
   OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE CARLOS III
   UNIVERSITY OF MADRID HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

   THE CARLOS III UNIVERSITY OF MADRID SPECIFICALLY DISCLAIMS ANY WARRANTIES,
   INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
   FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS ON AN
   "AS IS" BASIS, AND CARLOS III UNIVERSITY OF MADRID HAS NO OBLIGATION TO 
   PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.]

******************************************************************************/

import java.util.*;

class Room {
    public static class EntryPoint {
        public final Room room;
        public final int doorN;
        public EntryPoint(Room room, int doorN) {
            this.room = room;
            this.doorN = doorN;
        }
    }

    private static class BST<T extends Comparable<T>, E> {
        private int size;
        private T key;
        private E data;
        private BST<T, E> left, right;

        public boolean isEmpty() { return data==null; }
        public int size() { return size; }
        public E get(T key) {
            if (isEmpty())  return null;
            if (key.compareTo(this.key)==0)  return data;
            if (key.compareTo(this.key) <0)  return (left==null)? null : left.get(key);
            if (key.compareTo(this.key) >0)  return (right==null)? null : right.get(key);
            return null;
        }
        public boolean put(T key, E element) {
            if (isEmpty()) { this.key = key; this.data = element; size++; return true; }
            if (key.compareTo(this.key)==0)  { this.data = element; return false; }
            if (key.compareTo(this.key) <0)  {
                if (left==null)  left = new BST<T, E>();
                boolean ch = left.put(key, element);
                if (ch)  size++;
                return ch;
            }
            if (key.compareTo(this.key) >0) {
                if (right==null)  right = new BST<T, E>();
                boolean ch = right.put(key, element);
                if (ch)  size++;
                return ch;
            }
            return false;
        }
    }

    public String name;
    private final int cap;
    private int total;


    private BST<Integer, Integer> pools = new BST<Integer, Integer>();

    public Room(int cap) { this(cap, Integer.toString(cap)); }
    public Room(int cap, String name) {
        this.cap = cap;
        this.name = name;
    }
    
    private synchronized int addPool(int id) {
        int poolN = pools.size();
        int poolSize = (cap-1)/(poolN+1);
        System.out.printf("[R%s ] Adding pool (%d pools, size %d)\n", name, poolN+1, poolSize);
        if (poolN == 0) {
            pools.put(id, (cap-1));
            return cap;
        }
        
        int delta = poolSize-cap/poolN;
        for (int i=0; i<pools.size(); i++) {
            pools.put(i, pools.get(i)+delta);
        }
        pools.put(id, poolSize);
        return pools.get(id);
    }
    protected synchronized int getPool(int id) {
        Integer pool = pools.get(id);
        if (pool == null)  pool = addPool(id);
        return pool;
    }
    public int getTotal() { return total; }
    
    public synchronized boolean tryEnter(int poolID) {
        int pool = getPool(poolID);
        if (pool>0 && total < cap) {
            pools.put(poolID, --pool);
            total++;
            notifyAll();
            return true;
        }
        return false;
    }
    public synchronized void enter(int poolID) {
        int pool = getPool(poolID);
        while (pool<=0 || total > cap) {
            try { wait(); } catch (InterruptedException e) {}
        }
        pools.put(poolID, --pool);
        total++;
        notifyAll();
    }

    public synchronized void exit() { exit(0); }
    public synchronized void exit(int poolID) {
        if (pools.get(poolID)==null)  throw new IllegalArgumentException("Invalid pool ID " + poolID);
        pools.put(poolID, pools.get(poolID)+1);
        total--;
    }
}


class MuseumMap extends ArrayList<Room.EntryPoint> {
    public boolean add(Room room, int doorN) {
        return add(new Room.EntryPoint(room, doorN));
    }
}


class Person extends Thread {
    
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
    private void moveTo(Room room) { moveTo(room, 0); }
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

        System.out.printf("[ %d ] Finished the museum visit\n", id);
    }
}

public class Museum {

    public static void main(String[] args){

        Room r45 = new Room(45);
        Room r20a = new Room(20, "20a");
        Room r40 = new Room(40);
        Room r20b = new Room(20, "20b");
        Room r30 = new Room(30);

        MuseumMap museumMap = new MuseumMap();
        museumMap.add(r45, 0);
        museumMap.add(r20a, 0);
        museumMap.add(r45, 1);
        museumMap.add(r40, 0);
        museumMap.add(r20b, 0);
        museumMap.add(r40, 1);
        museumMap.add(r30, 0);

        
        for (int i=0; i < 100; i++){
            new Person(i, museumMap).start();
        }
    }
}
