/**JavaFile******************************************************************
 
FileName    [Program that implements the Museum Problem]

Synopsis [ Museum Problem template ]

Author      [Iria Estevez-Ayres <ayres@it.uc3m.es>]

Solution    [Alonso Herreros Copete <alonso.herreros@alumnos.uc3m.es>]

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
package alonso;

public class Museum {

    public static final int PEOPLE_N = 200;


    public static MuseumMap setupMuseum() {
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

        return museumMap;
    }

    public static Person[] setupPeople(MuseumMap museumMap) {
        Person[] people = new Person[PEOPLE_N];
        for (int i=0; i < PEOPLE_N; i++){
            people[i] = new Person(i, museumMap);
        }
        return people;
    }

    public static void runPeople(Person[] people) {
        for (int i=0; i<PEOPLE_N; i++) {
            people[i].start();
        }
        for (int i=0; i<PEOPLE_N; i++) {
            try { people[i].join(); } catch (InterruptedException e) {}
        }
    }

    public static void main(String[] args){
        Person[] people = setupPeople(setupMuseum());
        runPeople(people);
    }
}
