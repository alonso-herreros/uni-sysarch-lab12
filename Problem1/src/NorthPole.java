/**JavaFile******************************************************************

   FileName    [Program that implements the Santa Claus Problem]

   Synopsis [ Santa Claus Problem template ]

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

import java.util.concurrent.Semaphore;

class Santa extends Thread {
    private final SantaNotifs notifs;
    
    public Santa(SantaNotifs santaNotifs) {
        this.notifs = santaNotifs;
    }

    public void run() {
        while (true) {
            if (notifs.getFlag(SantaNotifs.ID.REINDEERS)) {
                distributeToys();
                continue;
            }
            if (notifs.getFlag(SantaNotifs.ID.ELVES)) {
                helpElves();
                continue;
            }
            notifs.await();
        }
    }

    private void distributeToys() {
        System.out.println("Santa: Distributing toys");
        try { Thread.sleep(3000); } catch (InterruptedException e) {}
        System.out.println("Santa: Toys Distributed");
        notifs.setFlag(SantaNotifs.ID.REINDEERS_FINISHED);
    }

    private void helpElves() {
        System.out.println("Santa: Helping elves");
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        System.out.println("Santa: Helped elves");
        notifs.setFlag(SantaNotifs.ID.ELVES_SOLVED);
    }
}


class SantaNotifs {
    private int flags = 0;

    enum ID {
        REINDEERS(0), ELVES(1), REINDEERS_FINISHED(2), ELVES_SOLVED(3);
        private int val;
        private ID(int val) { this.val = val; }
        public int getVal() { return val; }
    }

    public synchronized boolean setFlag(ID id) {
        if (getFlag(id))  return false;
        flags |= 1<<id.getVal();
        notifyAll();
        return true;
    }
    public synchronized boolean clearFlag(ID id) {
        if (!getFlag(id))  return false;
        flags = ~(~flags|1<<id.getVal());
        notifyAll();
        return true;
    }
    public boolean getFlag(ID id) {
        return (flags & 1<<id.getVal()) != 0;
    }
    public int getFlags() { return flags; }

    public synchronized void await() {
        try { wait(); } catch (InterruptedException e) {}
    }
    public synchronized void await(ID id) { await(id, true); }
    public synchronized void await(ID id, boolean value) {
        while (getFlag(id) != value) {
            try { wait(); } catch (InterruptedException e) {}
        }
    }
}


class Elve extends Thread {
    private final SantaNotifs santaNotifs;
    private final Semaphore room;

    public Elve(SantaNotifs santaNotifs, Semaphore elvesRoom) {
        this.santaNotifs = santaNotifs;
        this.room = elvesRoom;
    }


    public void run() {
        Random rnd = new Random();
        try { Thread.sleep(rnd.nextInt(200)); } catch (InterruptedException e) {}
        help();
    }

    private void help() {
        santaNotifs.await(SantaNotifs.ID.ELVES, false);
        santaNotifs.clearFlag(SantaNotifs.ID.ELVES_SOLVED);
        synchronized(room) {
            while (!room.tryAcquire()) {
                try { room.wait(); } catch (InterruptedException e) {}
            }
            if (room.availablePermits() == 0)  santaNotifs.setFlag(SantaNotifs.ID.ELVES);
            System.out.printf("Elf: need help (waiting for %d more)\n", room.availablePermits());
        }
        santaNotifs.await(SantaNotifs.ID.ELVES_SOLVED);
        synchronized(room) {
            room.release();
            if (room.availablePermits() == NorthPole.MAX_ELVES)  santaNotifs.clearFlag(SantaNotifs.ID.ELVES);
            System.out.printf("Elf: got help. (%d solved)\n", room.availablePermits());
        }
    }

}

class Reindeer extends Thread {
    private final SantaNotifs santaNotifs;
    private final Semaphore stable;

    public Reindeer(SantaNotifs santaNotifs, Semaphore reindeersStable) {
        this.santaNotifs = santaNotifs;
        this.stable = reindeersStable;
    }


    public void run() {
        Random rnd = new Random();
        try { Thread.sleep(rnd.nextInt(100)); } catch (InterruptedException e) {}
        arrive();
    }

    private void arrive() {
        santaNotifs.await(SantaNotifs.ID.REINDEERS, false);
        santaNotifs.clearFlag(SantaNotifs.ID.REINDEERS_FINISHED);
        synchronized(stable) {
            while (!stable.tryAcquire()) {
                try { stable.wait(); } catch (InterruptedException e) {}
            }
            System.out.printf("Reindeer: arrived (%d left)\n", stable.availablePermits());
            if (stable.availablePermits() == 0)  santaNotifs.setFlag(SantaNotifs.ID.REINDEERS);
        }
        santaNotifs.await(SantaNotifs.ID.REINDEERS_FINISHED);
        synchronized(stable) {
            stable.release();
            if (stable.availablePermits() == NorthPole.MAX_REINDEER)  santaNotifs.clearFlag(SantaNotifs.ID.REINDEERS);
            System.out.printf("Reindeer: finished work. (%d finished)\n", stable.availablePermits());
        }
    }

}

public class NorthPole {
    public static final int MAX_ELVES = 3;
    public static final int MAX_REINDEER = 9;

    public static void main(String[] args) {
        SantaNotifs santaNotifs = new SantaNotifs();
        
        Santa claus = new Santa(santaNotifs);
        claus.start();
        
        Semaphore reindeersStable = new Semaphore(MAX_REINDEER);
        for (int i=0; i < 9; i++) {
            Reindeer rudolf = new Reindeer(santaNotifs, reindeersStable);
            rudolf.start();
        }
        
        Semaphore elvesRoom = new Semaphore(MAX_ELVES);
        for (int i=0; i < 100; i++) {
            Elve legolas = new Elve(santaNotifs, elvesRoom);
            legolas.start();
        }

    }
}
