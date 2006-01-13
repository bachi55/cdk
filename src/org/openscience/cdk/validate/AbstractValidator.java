/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 * 
 */
package org.openscience.cdk.validate;

import org.openscience.cdk.interfaces.Atom;
import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.interfaces.AtomType;
import org.openscience.cdk.interfaces.Bond;
import org.openscience.cdk.interfaces.ChemFile;
import org.openscience.cdk.interfaces.ChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.ChemSequence;
import org.openscience.cdk.interfaces.Crystal;
import org.openscience.cdk.interfaces.ElectronContainer;
import org.openscience.cdk.interfaces.Element;
import org.openscience.cdk.interfaces.Isotope;
import org.openscience.cdk.interfaces.Molecule;
import org.openscience.cdk.interfaces.Reaction;
import org.openscience.cdk.interfaces.SetOfMolecules;
import org.openscience.cdk.interfaces.SetOfReactions;

/**
 * Abstract validator that does nothing but provide all the methods that the
 * ValidatorInterface requires.
 *
 * @cdk.module extra
 *
 * @author   Egon Willighagen
 * @cdk.created  2004-03-27
 * @cdk.require java1.4+
 */ 
public class AbstractValidator implements ValidatorInterface {

    public ValidationReport validateChemObject(IChemObject subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }
    public ValidationReport validateAtom(Atom subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }
    public ValidationReport validateAtomContainer(AtomContainer subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }
    public ValidationReport validateAtomType(AtomType subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }
    public ValidationReport validateBond(Bond subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }
    public ValidationReport validateChemFile(ChemFile subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }
    public ValidationReport validateChemModel(ChemModel subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }
    public ValidationReport validateChemSequence(ChemSequence subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }
    public ValidationReport validateCrystal(Crystal subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }
    public ValidationReport validateElectronContainer(ElectronContainer subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }
    public ValidationReport validateElement(Element subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }
    public ValidationReport validateIsotope(Isotope subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }
    public ValidationReport validateMolecule(Molecule subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }
    public ValidationReport validateReaction(Reaction subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }
    public ValidationReport validateSetOfMolecules(SetOfMolecules subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }
    public ValidationReport validateSetOfReactions(SetOfReactions subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }
    
}
