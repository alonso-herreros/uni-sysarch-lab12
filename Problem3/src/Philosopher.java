/**JavaFile******************************************************************

  FileName    [Program that implements the Philosophers Paradigm]

  Synopsis [ Philosophers Paradigm ]
  money, Donald withdraws money] 

  Author      [Iria Estevez-Ayres <ayres@it.uc3m.es>]

  Copyright   [Copyright (c) 2015 Carlos III University of Madrid
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

import java.util.concurrent.Semaphore;

public class Philosopher extends Thread{

    private Semaphore palillo1;
    private Semaphore palillo2;
    private int id;

    public Philosopher(int id, Semaphore palillo1, Semaphore palillo2){
	this.id = id;
	this.palillo1 = palillo1;
	this.palillo2 = palillo2;
    }

    public void comer(){
	try{
	    palillo1.acquire();
	}catch (Exception e){};

	// Fuerzo el deadlock para que se vea 
	try{
	     Thread.sleep(this.id); // duerme un número de segundos dependiendo
	    // de su identificador
	 }catch (Exception e){};

	try{
	    palillo2.acquire();
	}catch (Exception e){};

	System.out.println("Soy el filosofo "+id+"y estoy comiendo");
    }
    public void termino_de_comer(){
	palillo2.release();
	palillo1.release();
    }
    public void run(){
	this.comer();


	this.termino_de_comer();
    }
    public static void main(String[] args){

	Philosopher f1,f2,f3,f4;
	Semaphore palilloA,palilloB,palilloC,palilloD;
	palilloA = new Semaphore(1);
	palilloB = new Semaphore(1);
	palilloC = new Semaphore(1);
	palilloD = new Semaphore(1);
	f1= new Philosopher(1,palilloA,palilloB);
	f2= new Philosopher(2,palilloB,palilloC);
	f3= new Philosopher(3,palilloC,palilloD);
	f4= new Philosopher(4,palilloD,palilloA); 
	f4.start();
	f1.start();
	f3.start();
	f2.start();
    }
}