/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2005  The JChemPaint project
 *
 * Contact: jchempaint-devel@lists.sf.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.applications.jchempaint.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.AtomType;
import org.openscience.cdk.ElectronContainer;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.undoredo.ConvertToRadicalEdit;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * @cdk.module jchempaint
 */
public class ConvertToRadicalAction extends JCPAction {

    public void actionPerformed(ActionEvent event) {
        logger.debug("Converting to radical: ", type);
        IChemObject object = getSource(event);
        JChemPaintModel jcpmodel = jcpPanel.getJChemPaintModel();
        org.openscience.cdk.interfaces.ChemModel model = jcpmodel.getChemModel();
        if (object != null) {
            if (object instanceof Atom) {
                Atom atom = (Atom)object;
                
                AtomContainer relevantContainer = ChemModelManipulator.getRelevantAtomContainer(model, atom);
                double number = controlLonePair(relevantContainer, atom);
                
                if(number > 0.0)
                {
                	ElectronContainer electronContainer = new SingleElectron(atom);
                    relevantContainer.addElectronContainer(electronContainer);
                    UndoableEdit  edit = new ConvertToRadicalEdit(relevantContainer, 
                            electronContainer);
                    jcpPanel.getUndoSupport().postEdit(edit);
                    logger.info("Added single electron to atom");
                    logger.debug("new AC: ", relevantContainer);
                    
                }
                else JOptionPane.showMessageDialog(jcpPanel,"A radical cannot be added to this atom." +
        		" Re-try with less hydrogens.");

            
            } else {
                logger.error("Object not an Atom! Cannot convert into a radical!");
            }
        } else {
            logger.warn("Cannot convert a null object!");
        }
        jcpmodel.fireChange();
    }
    private double controlLonePair(AtomContainer container, Atom atom)
    {
    	double nLonePair = 0.0;
    	AtomTypeFactory atomATF = null;
		try
		{
			atomATF = AtomTypeFactory.getInstance(
                "org/openscience/cdk/config/data/valency2_atomtypes.xml",
                atom.getBuilder()
            );
	    	
			if(atomATF != null)
			{

				AtomType atomType = atomATF.getAtomType(atom.getSymbol());
		    	double bondOrderSum = container.getBondOrderSum(atom);
				int charge = atom.getFormalCharge();
				int hcount = atom.getHydrogenCount();
				int valency = atomType.getValency();
				nLonePair = (valency - (hcount + bondOrderSum) - charge) / 2;
			}
		} catch (Exception ex1)
		{
			logger.error(ex1.getMessage());
			logger.debug(ex1);
		}
		return nLonePair;
		
		
    }
}
