/*  $RCSfile$    
 *  $Author$    
 *  $Date$    
 *  $Revision$
 *
 *  Copyright (C) 2002-2005  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.ringsearch;

import java.util.Vector;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;

/**
* Implementation of a Path as needed by {@cdk.cite HAN96}.
 *
 * @cdk.module standard
 *
 * @cdk.keyword graph, path
 *
 * @author     steinbeck
 * @cdk.created    2002-02-28
 */
public class Path extends Vector
{

	/**
	 *  Constructs an empty path
	 */
	public Path()
	{
		super();
	}


	/**
	 *  Constructs a new Path with two Atoms
	 *
	 * @param  atom1  first atom in the new path
	 * @param  atom2  second atom in the new path
	 */
	public Path(Atom atom1, Atom atom2)
	{
		super();
		add(atom1);
		add(atom2);
	}


	/**
	 *  Joins two paths. The joint point is given by an atom
	 *  which is shared by the two pathes.
	 *
	 * @param  path1  First path to join
	 * @param  path2  Second path to join
	 * @param  atom   The atom which is the joint point
	 * @return        The newly formed longer path
	 */
	public static Path join(Path path1, Path path2, Atom atom)
	{
		Path newPath = new Path();
		Path tempPath = new Path();
		if (path1.firstElement() == atom)
		{
			path1.revert();
		}
		newPath.addAll(path1);
		if (path2.lastElement() == atom)
		{
			path2.revert();
		}
		tempPath.addAll(path2);
		tempPath.remove(atom);
		newPath.addAll(tempPath);
		return newPath;
	}
	
	public int getIntersectionSize(Path other)
	{
		Atom a1, a2;
		int iSize = 0;
		for (int i = 0; i < size(); i++)
		{
			a1 = (Atom)elementAt(i);
			for (int j = 0; j < other.size(); j++)
			{
				a2 = (Atom)other.elementAt(j);
				if (a1 == a2) iSize++;
			}
		}
		return iSize;
	}
	
	public void revert()
	{
		Object o = null;
		int size = size();
		int i = (int)(size / 2);
		for (int f = 0; f < i; f++)
		{  
			o = elementAt(f);
			setElementAt(elementAt(size - f -1), f);
			setElementAt(o, size - f - 1);
		}
	}
	
	public String toString(AtomContainer ac)
	{
		String s = "Path of length " + size() + ": ";
		try
		{
			for (int f = 0; f < size(); f++)
			{
				s += ac.getAtomNumber((Atom)elementAt(f)) + " ";
			}
		}
		catch(Exception exc)
		{
			System.out.println(exc);
			s += "Could not create a string representaion of this path";	
		}
		return s;
	}
}

