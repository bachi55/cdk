/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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
 */
package org.openscience.cdk.isomorphism.matchers;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;

/**
 * @cdk.module extra
 */
public class OrderQueryBond extends Bond implements QueryBond {

    public OrderQueryBond() {
    }

    public OrderQueryBond(QueryAtom atom1, QueryAtom atom2, double order) {
        super((Atom)atom1, (Atom)atom2, order);
    }
    
	public boolean matches(Bond bond) {
        if (this.getOrder() == bond.getOrder()) {
            // bond orders match
            return true;
        } else if (this.getFlag(CDKConstants.ISAROMATIC) && bond.getFlag(CDKConstants.ISAROMATIC)) {
            // or both are aromatic
        } // else
        return false;
    };

    public void setAtoms(Atom[] atoms) {
        if (atoms.length > 0 && atoms[0] instanceof QueryAtom) {
            super.setAtoms(atoms);
        } else {
            throw new IllegalArgumentException("Array is not of type QueryAtom[]");
        }
	}
    
	public void setAtomAt(Atom atom, int position) {
        if (atom instanceof QueryAtom) {
            super.setAtomAt(atom, position);
        } else {
            throw new IllegalArgumentException("Atom is not of type QueryAtom");
        }
    }
}

