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

import java.lang.*;
import java.util.*;

import java.util.concurrent.Semaphore;

class Santa extends Thread{
    public void run(){

	
    }
}

class Elve extends Thread{
    public void run(){

    }

}

class Reindeer extends Thread{
    public void run(){

    }

}

public class NorthPole{

    public static void main(String[] args){

	// You can (and should) create new constructors for each class
	// So, you will also need to modify this main
	
	Santa claus = new Santa();
	Reindeer rudolf;
	Elve legolas;
	
	claus.start();
	
	for (int i=0; i < 9; i++){
	    rudolf = new Reindeer();
	    rudolf.start();
	}
	
	for (int i=0; i < 100; i++){
	    legolas = new Elve();
	    legolas.start();
	}

    }
}
