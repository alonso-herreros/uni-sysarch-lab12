package teachers;
import java.lang.*;
import java.util.*;
import java.util.concurrent.Semaphore;

class PersonFinal extends Thread {
    private int me;
    private Semaphore s45Sem, s20aSem, s40Sem, s20bSem, s30Sem;
    private static int numIn45, numIn20a, numIn40, numIn20b, numIn30;
    private Semaphore mutex45, mutex20a, mutex40, mutex20b, mutex30;
    private Random rnd = new Random();

    PersonFinal(int me, Semaphore s45Sem, Semaphore s20aSem, Semaphore s40Sem,
                Semaphore s20bSem, Semaphore s30Sem, Semaphore mutex45,
                Semaphore mutex20a, Semaphore mutex40, Semaphore mutex20b,
                Semaphore mutex30) {
        this.s45Sem = s45Sem;
        this.s20aSem = s20aSem;
        this.s40Sem = s40Sem;
        this.s20bSem = s20bSem;
        this.s30Sem = s30Sem;
        this.mutex45 = mutex45;
        this.mutex20a = mutex20a;
        this.mutex40 = mutex40;
        this.mutex20b = mutex20b;
        this.mutex30 = mutex30;
        this.me = me;
    }

    private void room45in(int time) {
        if (time == 1) {
            try {
                s45Sem.acquire();
            } catch (InterruptedException e) {
            }
        }
        try {
            mutex45.acquire();
        } catch (InterruptedException e) {
        }
        numIn45++;
        mutex45.release();
        if (time == 2) {
            try {
                mutex20a.acquire();
            } catch (InterruptedException e) {
            }
            numIn20a--;
            mutex20a.release();
            s20aSem.release();
        }
        try {
            mutex45.acquire();
        } catch (InterruptedException e) {
        }
        System.out.println("[ " + me + " ] I am inside room 45 " + time + " time. There is "
                + numIn45 + " people");
        mutex45.release();
    }

    private void room20ain() {
        try {
            s20aSem.acquire();
        } catch (InterruptedException e) {
        }
        try {
            mutex20a.acquire();
        } catch (InterruptedException e) {
        }
        try {
            mutex45.acquire();
        } catch (InterruptedException e) {
        }
        numIn20a++;
        numIn45--;
        mutex45.release();
        System.out.println("[ " + me + " ] I am inside room 20a. There is "
                + numIn20a + " people");
        mutex20a.release();
    }

    private void room40in(int time) {
        if (time == 1) {
            try {
                s40Sem.acquire();
            } catch (InterruptedException e) {
            }
        }
        try {
            mutex40.acquire();
        } catch (InterruptedException e) {
        }
        numIn40++;
        mutex40.release();
        if (time == 1) {
            try {
                mutex45.acquire();
            } catch (InterruptedException e) {
            }
            numIn45--;
            mutex45.release();
            s45Sem.release();
        }
        if (time == 2) {
            try {
                mutex20b.acquire();
            } catch (InterruptedException e) {
            }
            numIn20b--;
            mutex20b.release();
            s20bSem.release();
        }
        try {
            mutex40.acquire();
        } catch (InterruptedException e) {
        }
        System.out.println("[ " + me + " ] I am inside room 40 " + time + " time. There is "
                + numIn40 + " people");
        mutex40.release();
    }

    private void room20bin() {
        try {
            s20bSem.acquire();
        } catch (InterruptedException e) {
        }
        try {
            mutex20b.acquire();
        } catch (InterruptedException e) {
        }
        try {
            mutex40.acquire();
        } catch (InterruptedException e) {
        }
        numIn20b++;
        numIn40--;
        System.out.println("[ " + me + " ] I am inside room 20b. There is "
                + numIn20b + " people");
        mutex20b.release();
        mutex40.release();
    }

    private void room30in() {
        try {
            s30Sem.acquire();
        } catch (InterruptedException e) {
        }
        try {
            mutex30.acquire();
        } catch (InterruptedException e) {
        }
        try {
            mutex40.acquire();
        } catch (InterruptedException e) {
        }
        numIn30++;
        numIn40--;
        System.out.println("[ " + me + " ] I am inside room 30. There is "
                + numIn30 + " people");
        mutex30.release();
        mutex40.release();
        s40Sem.release();
    }

    private void room30out() {
        try {
            mutex30.acquire();
        } catch (InterruptedException e) {
        }
        numIn30--;
        mutex30.release();
        s30Sem.release();
    }

    public void run() {
        System.out.println("[ " + me + " ] Trying to enter");
        room45in(1);
        try {
            Thread.sleep(rnd.nextInt(1000));
        } catch (Exception e) {
        }
        room20ain();
        try {
            Thread.sleep(rnd.nextInt(1000));
        } catch (Exception e) {
        }
        room45in(2);
        try {
            Thread.sleep(rnd.nextInt(1000));
        } catch (Exception e) {
        }
        room40in(1);
        try {
            Thread.sleep(rnd.nextInt(1000));
        } catch (Exception e) {
        }
        room20bin();
        try {
            Thread.sleep(rnd.nextInt(1000));
        } catch (Exception e) {
        }
        room40in(2);
        try {
            Thread.sleep(rnd.nextInt(1000));
        } catch (Exception e) {
        }
        room30in();
        try {
            Thread.sleep(rnd.nextInt(1000));
        } catch (Exception e) {
        }
        room30out();
        System.out.println("[ " + me + " ] Outside the museum");
    }
}

public class MuseumOfficial {
    public static void main(String[] args) {
        PersonFinal[] p = new PersonFinal[200]; // Added for performance testing
        Semaphore s45Sem, s20aSem, s40Sem, s20bSem, s30Sem;
        Semaphore mutex45, mutex20a, mutex40, mutex20b, mutex30;
        s45Sem = new Semaphore(45);
        s20aSem = new Semaphore(20);
        s40Sem = new Semaphore(40);
        s20bSem = new Semaphore(20);
        s30Sem = new Semaphore(30);
        mutex45 = new Semaphore(1);
        mutex20a = new Semaphore(1);
        mutex40 = new Semaphore(1);
        mutex20b = new Semaphore(1);
        mutex30 = new Semaphore(1);
        for (int i = 0; i < 200; i++) {
            p[i] = new PersonFinal(i, s45Sem, s20aSem, s40Sem, s20bSem, s30Sem,
                    mutex45, mutex20a, mutex40, mutex20b, mutex30);
            p[i].start();
        }

        for (int i=0; i<200; i++) { // Added for performance testing
            try { p[i].join(); } catch (InterruptedException e) {}
        }
    }
}