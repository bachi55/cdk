/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 *
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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
 */

package org.openscience.cdk;

/**
 * Base class for entities containing electrons, like bonds, orbitals, lone-pairs.
 *
 * @cdk.module core
 *
 * @cdk.keyword orbital
 * @cdk.keyword lone-pair
 * @cdk.keyword bond
 */
public class ElectronContainer extends ChemObject implements java.io.Serializable, Cloneable 
{
	/** Number of electrons in the ElectronContainer. */
	protected int electronCount;

    /**
     * Constructs an empty ElectronContainer.
     */
    public ElectronContainer() {
        electronCount = 0;
    }
    
	/**
	 * Returns the number of electrons in this electron container.
	 *
	 * @return The number of electrons in this electron container.
     *
     * @see     #setElectronCount
	 */
	public int getElectronCount()
	{
		return this.electronCount;
	}


	/**
	 * Sets the number of electorn in this electron container.
	 *
	 * @param   electronCount The number of electrons in this electron container.
     *
     * @see     #getElectronCount
	 */
	public void setElectronCount(int electronCount)
	{
		this.electronCount = electronCount;
		notifyChanged();
	}

    /**
     * Clones this atom object and its content.
     *
     * @return  The cloned object   
     */
    public Object clone() {
        Object clone = null;
        try {
            clone = super.clone();
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
        }
        return clone;
    }
}


