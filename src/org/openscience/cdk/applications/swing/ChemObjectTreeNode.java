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
package org.openscience.cdk.applications.swing;

import java.awt.FlowLayout;
import java.lang.reflect.Field;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.tools.LoggingTool;

/**
 * A node in the ChemObjectTree.
 *
 * @cdk.module applications
 *
 * @author     egonw
 * @cdk.created    2004-08-22
 * @cdk.keyword    node, ChemObject tree
 * @cdk.require swing
 */
public class ChemObjectTreeNode extends DefaultMutableTreeNode {

    public ChemObjectTreeNode(ChemObject object) {
        super();
        this.setUserObject(object);
    }
    
    public void setUserObject() {
        // do nothing
    }
    
    public String toString() {
        StringBuffer name = new StringBuffer();
        Object object = this.getUserObject();
        name.append(object.getClass()
            .getName().substring("org.openscience.cdk.".length()));
        if (object instanceof Atom) {
            Atom atom = (Atom)object;
            name.append(" " + atom.getSymbol());
        } else if (object instanceof Bond) {
            Bond bond = (Bond)object;
            name.append(" " + bond.getOrder());
        }
        return name.toString();
    }
    
}

