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


import java.lang.*;
import java.util.*;
import java.util.concurrent.Semaphore;

class Person extends Thread{
    private int me;
    private Semaphore s45Sem, s20aSem,  s40Sem, s20bSem, s30Sem;
    private static int numIn45, numIn20a, numIn40, numIn20b, numIn30;


    private Semaphore mutex45, mutex20a, mutex40, mutex20b, mutex30;
    private Random rnd = new Random();
    
    Person(int me, Semaphore s45Sem, Semaphore s20aSem, Semaphore s40Sem,
	   Semaphore s20bSem, Semaphore s30Sem, Semaphore mutex45,
	   Semaphore mutex20a, Semaphore mutex40, Semaphore mutex20b,
	   Semaphore mutex30){
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
	this.me =me;
    }


    private void room45in(int time){
	numIn45++;

	if (time == 2){
	    numIn20a--;
	}

	System.out.println("[ "+ me +" ] I am inside room 45 "+ time+ " time. There is "
			   + numIn45  + " people");
    }
    

    private void room20ain(){
	numIn20a++;
	numIn45--;
	System.out.println("[ "+ me +" ] I am inside room 20a. There is "
			   + numIn20a + " people");
    }


    private void room40in(int time){
	numIn40++;
	
	if (time == 1){
	    numIn45--;
	}

	if (time == 2){
	    numIn20b--;
	}

	System.out.println("[ "+ me +" ] I am inside room 40 "+ time+ " time. There is "
			   + numIn40 + " people");
    }


    private void room20bin(){
	numIn20b++;
	numIn40--;
	System.out.println("[ "+ me +" ] I am inside room 20b. There is "
			   + numIn20b + " people");
    }

    private void room30in(){
	numIn30++;
	numIn40--;
	System.out.println("[ "+ me +" ] I am inside room 30. There is "
			   + numIn30 + " people");
    }

    private void room30out(){
	numIn30--;
    }

    public void run(){
	System.out.println("[ "+ me +" ] Trying to enter");

	room45in(1);
	try {
	    Thread.sleep(rnd.nextInt(1000));
	} catch (Exception e) {}

	room20ain();
	try {
	    Thread.sleep(rnd.nextInt(1000));
	} catch (Exception e) {}

	room45in(2);
	try {
	    Thread.sleep(rnd.nextInt(1000));
	} catch (Exception e) {}

	room40in(1);
	try {
	    Thread.sleep(rnd.nextInt(1000));
	} catch (Exception e) {}
		
	room20bin();
	try {
	    Thread.sleep(rnd.nextInt(1000));
	} catch (Exception e) {}
		
	room40in(2);
	try {
	    Thread.sleep(rnd.nextInt(1000));
	} catch (Exception e) {}

	room30in();
	
	try {
	    Thread.sleep(rnd.nextInt(1000));
	} catch (Exception e) {}

	room30out();

	System.out.println("[ "+ me +" ] Outside the museum");
    }
}

public class Museum{

    public static void main(String[] args){

	Person p;
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
		
	for (int i=0; i < 200; i++){
	    p = new Person(i, s45Sem, s20aSem, s40Sem, s20bSem, s30Sem,
			   mutex45, mutex20a, mutex40, mutex20b, mutex30);
	    p.start();
	}
    }
}
