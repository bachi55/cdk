/*  $RCSfile$
 *  $Author$  
 *  $Date$  
 *  $Revision$
 * 
 * Copyright (C) 2000-2005  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 * agenda - add  parameter-tuning methods
 */

package org.openscience.cdk.structgen.stochastic.operator;

import java.util.Vector;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.structgen.stochastic.PartialFilledStructureMerger;
import org.openscience.cdk.math.RandomNumbersTool;

/**
 * Modified molecular structures by applying crossover operator on a pair of parent structures 
 * and generate a pair of offspring structures. Each of the two offspring structures inherits 
 * a certain fragments from both of its parents. 
 */

public class CrossoverMachine  
{
	private AtomContainer[] redChild,blueChild;
	private Vector redAtoms,blueAtoms;
	Vector children;	
	
	PartialFilledStructureMerger pfsm;
	
	/** selects a partitioning mode*/
	int splitMode = 2;	
	/** selects a partitioning scale*/	
	int numatoms = 5;
	/** Indicates that <code>crossover</code> is using SPLIT_MODE_RADNDOM mode. */
    public static final int SPLIT_MODE_RADNDOM = 0;
    /** Indicates that <code>crossover</code> is using SPLIT_MODE_DEPTH_FIRST mode. */
    public static final int SPLIT_MODE_DEPTH_FIRST = 1;	
    /** Indicates that <code>crossover</code> is using SPLIT_MODE_BREADTH_FIRST mode. */
    public static final int SPLIT_MODE_BREADTH_FIRST = 2;
	
    /**
     * Constructs a new CrossoverMachine operator 
     */
    public CrossoverMachine()
    {
	    redChild = new AtomContainer[2];
		blueChild = new AtomContainer[2];
		
		redAtoms = new Vector();
		blueAtoms = new Vector();		
		children = new Vector(2);
		try
		{
			pfsm = new PartialFilledStructureMerger();
		}
		catch(java.lang.Exception ex){ }		
    }
	
	/**
     * Performs the n point crossover of two <code>MolChromosome</code>
     * supplied by the <code>CrossInfo.parents</code> class and stores the resulting
     * chromosomes in <code>CrossInfo.children</code>.
     * <p>
     * @param cross_info the class storing the two parent chromosomes and the array
     * of children to generate.
     * @return an object storing information generated by the operator.
     * @exception IllegalArgumentException if some of the crosspoints defined are
     * greater than the size of the corresponding chromosome.
     */
    protected Vector doCrossover(AtomContainer dad, AtomContainer mom) throws CDKException
    {
		int dim = dad.getAtomCount();
		
		/***randomly divide atoms into two parts: redAtoms and blueAtoms.***/
		redAtoms.removeAllElements();
		blueAtoms.removeAllElements();		
		
	    if (splitMode==SPLIT_MODE_RADNDOM)
		{
			/*better way to randomly divide atoms into two parts: redAtoms and blueAtoms.*/
			for (int i = 0; i < dim; i++)
				redAtoms.add(new Integer(i));
			for (int i = 0; i < (dim - numatoms); i++)
			{   int ranInt = RandomNumbersTool.randomInt(0,redAtoms.size()-1);
				redAtoms.removeElementAt(ranInt);
				blueAtoms.add(new Integer(ranInt));
			}
				 
		}
		else
		{
			/*split graph using depth/breadth first traverse*/
			ChemGraph graph = new ChemGraph(dad);
			graph.setNumAtoms(numatoms);
			if (splitMode==SPLIT_MODE_DEPTH_FIRST)
			{
				redAtoms = graph.pickDFgraph();
			}
			else
				redAtoms = graph.pickBFgraph();
				
			for (int i = 0; i < dim; i++){
				Integer element = new Integer(i);
				if (!(redAtoms.contains(element)))
				{
					blueAtoms.add(element);
				}	
			}
		}		
		/*** dividing over ***/
		
		
		redChild[0] =new AtomContainer(dad); 
		blueChild[0] = new AtomContainer(dad); 
		redChild[1] = new AtomContainer(mom); 
		blueChild[1] = new AtomContainer(mom); 
		
		
		for (int j = 0; j < blueAtoms.size(); j++)
		{
            Bond[] bonds = redChild[1].getBonds();
			for (int i = 0; i < bonds.length; i++) {
				if (bonds[i].contains(redChild[0].getAtomAt(((Integer)blueAtoms.elementAt(j)).intValue())))
				{
					redChild[0].removeElectronContainer(bonds[i]);
					i--;
				}
			}
		}


		for (int j = 0; j < blueAtoms.size(); j++)
		{
            Bond[] bonds = redChild[1].getBonds();
			for (int i = 0; i < bonds.length; i++) {
				if (bonds[i].contains(redChild[1].getAtomAt(((Integer)blueAtoms.elementAt(j)).intValue())))
				{
					redChild[1].removeElectronContainer(bonds[i]);
					i--;
				}
			}
		}


		for (int j = 0; j < redAtoms.size(); j++)
		{
            Bond[] bonds = blueChild[0].getBonds();
			for (int i = 0; i < bonds.length; i++) {
				if (bonds[i].contains(blueChild[0].getAtomAt(((Integer)redAtoms.elementAt(j)).intValue())))
				{
					blueChild[0].removeElectronContainer(bonds[i]);
					i--;
				}
			}
		}


		for (int j = 0; j < redAtoms.size(); j++) {
            Bond[] bonds = blueChild[1].getBonds();
			for (int i = 0; i < bonds.length; i++) {
				if (bonds[i].contains(blueChild[1].getAtomAt(((Integer)redAtoms.elementAt(j)).intValue())))
				{
					blueChild[1].removeElectronContainer(bonds[i]);
					i--;
				}
			}
		}

		
		redChild[0].add(blueChild[1]);
		if (children.size()==2)
		{
			redChild[1].add(blueChild[0]);
		}
		
		
		
		for (int f = 0; f < children.size(); f++)
		{
			pfsm.setAtomContainer(redChild[f]);
			children.add(f, pfsm.generate());
		}
        return children;			
    } 
}
