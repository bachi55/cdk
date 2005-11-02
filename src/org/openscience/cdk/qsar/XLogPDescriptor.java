/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar;

import java.util.Vector;

import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.interfaces.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.RingSet;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.graph.PathTools;

/**
 * Prediction of logP based on the atom-type method called XLogP. For
 * description of the methodology see Ref. @cdk.cite{WANG97}
 * or <a href="http://www.chem.ac.ru/Chemistry/Soft/XLOGP.en.html">http://www.chem.ac.ru/Chemistry/Soft/XLOGP.en.html</a>. 
 * Actually one molecular factor is missing (presence of para Hs donor pair).
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>checkAromaticity</td>
 *     <td>false</td>
 *     <td>True is the aromaticity has to be checked</td>
 *   </tr>
 * </table>
 *
 *@author         mfe4
 *@cdk.created    2004-11-03
 *@cdk.module     qsar
 *@cdk.set        qsar-descriptors
 * @cdk.dictref qsar-descriptors:xlogP
 */
public class XLogPDescriptor implements Descriptor {
    
	private boolean checkAromaticity = false;

	/**
	 *  Constructor for the XLogPDescriptor object.
	 */
	public XLogPDescriptor() { }


	/**
	 *  Gets the specification attribute of the XLogPDescriptor object.
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				"http://qsar.sourceforge.net/dicts/qsar-descriptors:xlogP",
				this.getClass().getName(),
				"$Id$",
				"The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the XLogPDescriptor object.
	 *
	 *@param  params            The new parameters value
	 *@exception  CDKException  Description of the Exception
         *@see #getParameters
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("XLogPDescriptor only expects one parameter");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The first parameter must be of type Boolean");
		}
		// ok, all should be fine
		checkAromaticity = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the XLogPDescriptor object.
	 *
	 *@return    The parameters value
         *@see #setParameters
	 */
	public Object[] getParameters() {
		// return the parameters as used for the descriptor calculation
		Object[] params = new Object[1];
		params[0] = new Boolean(checkAromaticity);
		return params;
	}


	/**
	 *  Calculates the xlogP for an atom container.
         *
         *  If checkAromaticity is true, the
	 *  method check the aromaticity, if false, means that the aromaticity has
	 *  already been checked. It is necessary to use before the
	 *  addExplicitHydrogensToSatisfyValency method (HydrogenAdder classe).
	 *
	 *@param  ac                AtomContainer
	 *@return                   XLogP is a double
	 *@exception  CDKException  Possible Exceptions
	 */
	public DescriptorValue calculate(AtomContainer ac) throws CDKException {
		RingSet rs = (new AllRingsFinder()).findAllRings(ac);
		HueckelAromaticityDetector.detectAromaticity(ac, rs, true);
		double xlogP = 0;
		org.openscience.cdk.interfaces.Atom[] atoms = ac.getAtoms();
		String symbol = "";
		int bondCount = 0;
		int hsCount = 0;
		double xlogPOld=0;
		double maxBondOrder = 0;
		Vector hBondAcceptors=new Vector();
		Vector hBondDonors=new Vector();
		for (int i = 0; i < atoms.length; i++) {
			if (xlogPOld==xlogP & i>0 & !symbol.equals("H")){
				//System.out.println("XlogPAssignmentError: Could not assign atom number:"+(i-1)+" Symbol:"+symbol+" "+" bondCount:"+bondCount+" hsCount:"+hsCount+" maxBondOrder:"+maxBondOrder+"\t");
			}
			xlogPOld=xlogP;
			symbol = atoms[i].getSymbol();
			bondCount = ac.getBondCount(atoms[i]);
			hsCount = getHydrogenCount(ac, atoms[i]);
			maxBondOrder = ac.getMaximumBondOrder(atoms[i]);
			if (!symbol.equals("H")){
				//System.out.print("i:"+i+" Symbol:"+symbol+" "+" bondCount:"+bondCount+" hsCount:"+hsCount+" maxBondOrder:"+maxBondOrder+"\t");
			}
			if (symbol.equals("C")) {
				if (bondCount == 2) {
					// C sp
					if (hsCount >= 1) {
						xlogP += 0.209;
						//System.out.println("XLOGP: 38		 0.209");
					} else {
						if (maxBondOrder == 2.0) {
							xlogP += 2.073;
							//System.out.println("XLOGP: 40		 2.037");
						} else if (maxBondOrder == 3.0) {
							xlogP += 0.33;
							//System.out.println("XLOGP: 39		 0.33");
						}
					}
				}
				if (bondCount == 3) {
					// C sp2
					if (atoms[i].getFlag(CDKConstants.ISAROMATIC)) {
						if (getAromaticCarbonsCount(ac, atoms[i]) == 2) {
							if (hsCount == 0) {
								if (getAtomTypeXCount(ac, atoms[i]) == 0) {
									xlogP += 0.296;
									//System.out.println("XLOGP: 34		 0.296");
								} else {
									xlogP -= 0.151;
									//System.out.println("XLOGP: 35		-0.151");
								}
							} else {
								xlogP += 0.337;
								//System.out.println("XLOGP: 32		 0.337");
							}
						//} else if (getAromaticCarbonsCount(ac, atoms[i]) < 2 && getAromaticNitrogensCount(ac, atoms[i]) > 1) {
						} else if (getAromaticNitrogensCount(ac, atoms[i]) >= 1) {
							if (hsCount == 0) {
								if (getAtomTypeXCount(ac, atoms[i]) == 0) {
									xlogP += 0.174;
									//System.out.println("XLOGP: 36		 0.174");
								} else {
									xlogP += 0.366;
									//System.out.println("XLOGP: 37		 0.366");
								}
							} else if (getHydrogenCount(ac, atoms[i]) == 1) {
								xlogP += 0.126;
								//System.out.println("XLOGP: 33		 0.126");
							}
						}
					//NOT aromatic, but sp2
					} else {
						if (hsCount == 0) {
							if (getAtomTypeXCount(ac, atoms[i]) == 0) {
								if (getPiSystemsCount(ac, atoms[i]) <= 1) {
									xlogP += 0.05;
									//System.out.println("XLOGP: 26		 0.05");
								} else {
									xlogP += 0.013;
									//System.out.println("XLOGP: 27		 0.013");
								}
							}
							if (getAtomTypeXCount(ac, atoms[i]) == 1) {
								if (getPiSystemsCount(ac, atoms[i]) <= 1) {
									xlogP -= 0.03;
									//System.out.println("XLOGP: 28		-0.03");
								} else {
									xlogP -= 0.027;
									//System.out.println("XLOGP: 29		-0.027");
								}
							}
							if (getAtomTypeXCount(ac, atoms[i]) == 2) {
								if (getPiSystemsCount(ac, atoms[i]) <=1) {
									xlogP += 0.005;
									//System.out.println("XLOGP: 30		 0.005");
								} else {
									xlogP -= 0.315;
									//System.out.println("XLOGP: 31		-0.315");
								}
							}
						}
						if (hsCount == 1) {
							if (getAtomTypeXCount(ac, atoms[i]) == 0) {
								if (getPiSystemsCount(ac, atoms[i]) == 1) {
									xlogP += 0.466;
									//System.out.println("XLOGP: 22		 0.466");
								}
								if (getPiSystemsCount(ac, atoms[i]) == 2) {
									xlogP += 0.136;
									//System.out.println("XLOGP: 23		 0.136");
								}
							} else {
								if (getPiSystemsCount(ac, atoms[i]) == 1) {
									xlogP += 0.001;
									//System.out.println("XLOGP: 24		 0.001");
								}
								if (getPiSystemsCount(ac, atoms[i]) == 2) {
									xlogP -= 0.31;
									//System.out.println("XLOGP: 25		-0.31");
								}
							}
						}
						if (hsCount == 2) {
							xlogP += 0.42;
							//System.out.println("XLOGP: 21		 0.42");
						}
					}//sp2 NOT aromatic
				}
				
				if (bondCount == 4) {
					// C sp3
					if (hsCount == 0) {
						if (getAtomTypeXCount(ac, atoms[i]) == 0) {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP -= 0.006;
								//System.out.println("XLOGP: 16		-0.006");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 1) {
								xlogP -= 0.57;
								//System.out.println("XLOGP: 17		-0.57");
							}
							if (getPiSystemsCount(ac, atoms[i]) >= 2) {
								xlogP -= 0.317;
								//System.out.println("XLOGP: 18		-0.317");
							}
						} else {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP -= 0.316;
								//System.out.println("XLOGP: 19		-0.316");
							} else {
								xlogP -= 0.723;
								//System.out.println("XLOGP: 20		-0.723");
							}
						}
					}
					if (hsCount == 1) {
						if (getAtomTypeXCount(ac, atoms[i]) == 0) {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP += 0.127;
								//System.out.println("XLOGP: 10		 0.127");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 1) {
								xlogP -= 0.243;
								//System.out.println("XLOGP: 11		-0.243");
							}
							if (getPiSystemsCount(ac, atoms[i]) >= 2) {
								xlogP -= 0.499;
								//System.out.println("XLOGP: 12		-0.499");
							}
						} else {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP -= 0.205;
								//System.out.println("XLOGP: 13		-0.205");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 1) {
								xlogP -= 0.305;
								//System.out.println("XLOGP: 14		-0.305");
							}
							if (getPiSystemsCount(ac, atoms[i]) >= 2) {
								xlogP -= 0.709;
								//System.out.println("XLOGP: 15		-0.709");
							}
						}
					}
					if (hsCount == 2) {
						if (getAtomTypeXCount(ac, atoms[i]) == 0) {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP += 0.358;
								//System.out.println("XLOGP:  4		 0.358");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 1) {
								xlogP -= 0.008;
								//System.out.println("XLOGP:  5		-0.008");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 2) {
								xlogP -= 0.185;
								//System.out.println("XLOGP:  6		-0.185");
							}
						} else {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP += 0.137;
								//System.out.println("XLOGP:  7		 0.137");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 1) {
								xlogP -= 0.303;
								//System.out.println("XLOGP:  8		-0.303");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 2) {
								xlogP -= 0.815;
								//System.out.println("XLOGP:  9		-0.815");
							}
						}
					}
					if (hsCount > 2) {
						if (getAtomTypeXCount(ac, atoms[i]) == 0) {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP += 0.528;
								//System.out.println("XLOGP:  1		 0.528");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 1) {
								xlogP += 0.267;
								//System.out.println("XLOGP:  2		 0.267");
							}
						}else{
						//if (getNitrogenOrOxygenCount(ac, atoms[i]) == 1) {
							xlogP -= 0.032;
							//System.out.println("XLOGP:  3		-0.032");
						}
					}
				}//csp3
				
				if (getIfCarbonIsHydrophobic(ac, atoms[i])) {
					xlogP += 0.211;
					//System.out.println("XLOGP: Hydrophobic Carbon	0.211");
				}
			}//C
			
			if (symbol.equals("N")) {
				//NO2
				if (ac.getBondOrderSum(atoms[i]) >= 3.0 && getOxygenCount(ac, atoms[i]) >= 2 && maxBondOrder==2) {
					xlogP += 1.178;
					//System.out.println("XLOGP: 66		 1.178");
				}
				else {
					if (getPresenceOfCarbonil(ac, atoms[i])==1) {
						// amidic nitrogen
						if (hsCount == 0) {
							if (getAtomTypeXCount(ac, atoms[i]) == 0) {
								xlogP += 0.078;
								//System.out.println("XLOGP: 57		 0.078");
							}
							if (getAtomTypeXCount(ac, atoms[i]) == 1) {
								xlogP -= 0.118;
								//System.out.println("XLOGP: 58		-0.118");
							}
						}
						if (hsCount == 1) {
							if (getAtomTypeXCount(ac, atoms[i]) == 0) {
								xlogP -= 0.096;
								hBondDonors.add(new Integer(i));
								//System.out.println("XLOGP: 55		-0.096");
							} else {
								xlogP -= 0.044;
								hBondDonors.add(new Integer(i));
								//System.out.println("XLOGP: 56		-0.044");
							}
						}
						if (hsCount == 2) {
							xlogP -= 0.646;
							hBondDonors.add(new Integer(i));
							//System.out.println("XLOGP: 54		-0.646");
						}
					} else {//NO amidic nitrogen
						if (bondCount == 1) {
							// -C#N
							if (getCarbonsCount(ac, atoms[i]) == 1) {
								xlogP -= 0.566;
								//System.out.println("XLOGP: 68		-0.566");
							}
						}else if (bondCount == 2) {
							// N sp2
							if (atoms[i].getFlag(CDKConstants.ISAROMATIC)) {
								xlogP -= 0.493;
								//System.out.println("XLOGP: 67		-0.493");
							} else {
								if (getDoubleBondedCarbonsCount(ac, atoms[i]) == 0) {
									if (getDoubleBondedNitrogenCount(ac, atoms[i]) == 0) {
										if (getDoubleBondedOxygenCount(ac, atoms[i]) == 1) {
											xlogP += 0.427;
											//System.out.println("XLOGP: 65		 0.427");
										}
									}
									if (getDoubleBondedNitrogenCount(ac, atoms[i]) == 1) {
										if (getAtomTypeXCount(ac, atoms[i]) == 0) {
											xlogP += 0.536;
											//System.out.println("XLOGP: 63		 0.536");
										}
										if (getAtomTypeXCount(ac, atoms[i]) == 1) {
											xlogP -= 0.597;
											//System.out.println("XLOGP: 64		-0.597");
										}
									}
								}else if (getDoubleBondedCarbonsCount(ac, atoms[i]) == 1) {
									if (getAtomTypeXCount(ac, atoms[i]) == 0) {
										if (getPiSystemsCount(ac, atoms[i]) == 0) {
											xlogP += 0.007;
											//System.out.println("XLOGP: 59		 0.007");
										}
										if (getPiSystemsCount(ac, atoms[i]) == 1) {
											xlogP -= 0.275;
											//System.out.println("XLOGP: 60		-0.275");
										}
									}else if (getAtomTypeXCount(ac, atoms[i]) == 1) {
										if (getPiSystemsCount(ac, atoms[i]) == 0) {
											xlogP += 0.366;
											//System.out.println("XLOGP: 61		 0.366");
										}
										if (getPiSystemsCount(ac, atoms[i]) == 1) {
											xlogP += 0.251;
											//System.out.println("XLOGP: 62		 0.251");
										}
									}
								}
							}
						}else if (bondCount == 3) {
							// N sp3
							if (hsCount == 0) {
								if (rs.contains(atoms[i])) {
									if (getAtomTypeXCount(ac, atoms[i]) == 0) {
										xlogP += 0.881;
										//System.out.println("XLOGP: 51		 0.881");
									} else {
										xlogP -= 0.01;
										//System.out.println("XLOGP: 53		-0.01");
									}
								} else {
									if (getAtomTypeXCount(ac, atoms[i]) == 0) {
										if (getPiSystemsCount(ac, atoms[i]) == 0) {
											xlogP += 0.159;
											//System.out.println("XLOGP: 49		 0.159");
										}
										if (getPiSystemsCount(ac, atoms[i]) > 0) {
											xlogP += 0.761;
											//System.out.println("XLOGP: 50		 0.761");
										}
									} else {
										xlogP -= 0.239;
										//System.out.println("XLOGP: 52		-0.239");
									}
								}
							}else if (hsCount == 1) {
								if (getAtomTypeXCount(ac, atoms[i]) == 0) {
//									 like pyrrole
									if (atoms[i].getFlag(CDKConstants.ISAROMATIC)) {
										xlogP += 0.545;
										hBondDonors.add(new Integer(i));
										//System.out.println("XLOGP: 46		 0.545");
									} else {
										if (getPiSystemsCount(ac, atoms[i]) == 0) {
											xlogP -= 0.112;
											hBondDonors.add(new Integer(i));
											//System.out.println("XLOGP: 44		-0.112");
										}
										if (getPiSystemsCount(ac, atoms[i]) > 0) {
											xlogP += 0.166;
											hBondDonors.add(new Integer(i));
											//System.out.println("XLOGP: 45		 0.166");
										}
									}
								} else {
									if (rs.contains(atoms[i])) {
										xlogP += 0.153;
										hBondDonors.add(new Integer(i));
										//System.out.println("XLOGP: 48		 0.153");
									} else {
										xlogP += 0.324;
										hBondDonors.add(new Integer(i));
										//System.out.println("XLOGP: 47		 0.324");
									}
								}
							}else if (hsCount == 2) {
								if (getAtomTypeXCount(ac, atoms[i]) == 0) {
									if (getPiSystemsCount(ac, atoms[i]) == 0) {
										xlogP -= 0.534;
										hBondDonors.add(new Integer(i));
										//System.out.println("XLOGP: 41		-0.534");
									}
									if (getPiSystemsCount(ac, atoms[i]) == 1) {
										xlogP -= 0.329;
										hBondDonors.add(new Integer(i));
										//System.out.println("XLOGP: 42		-0.329");
									}
								} else {
									xlogP -= 1.082;
									hBondDonors.add(new Integer(i));
									//System.out.println("XLOGP: 43		-1.082");
								}
							}
						}
					}
				}
			}
			if (symbol.equals("O")) {
				if (bondCount == 1 && maxBondOrder==2.0) {
					xlogP -= 0.399;
					hBondAcceptors.add(new Integer(i));
					//System.out.println("XLOGP: 75	A=O	-0.399");
				}else if(bondCount == 1 && hsCount==0 && (getPresenceOfNitro(ac,atoms[i]) || getPresenceOfCarbonil(ac,atoms[i])==1)){
						xlogP -= 0.399;
						hBondAcceptors.add(new Integer(i));
						//System.out.println("XLOGP: 75	A=O	-0.399");					
				}else if (bondCount >= 1) {
					if (hsCount == 0 && bondCount==2) {
						if (getAtomTypeXCount(ac, atoms[i]) == 0) {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP += 0.084;
								//System.out.println("XLOGP: 72	R-O-R	 0.084");
							}
							if (getPiSystemsCount(ac, atoms[i]) > 0) {
								xlogP += 0.435;
								//System.out.println("XLOGP: 73	R-O-R.1	 0.435");
							}
						}else if (getAtomTypeXCount(ac, atoms[i]) == 1) {
							xlogP += 0.105;
							//System.out.println("XLOGP: 74	R-O-X	 0.105");
						}
					}else{
						if (getAtomTypeXCount(ac, atoms[i]) == 0) {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP -= 0.467;
								hBondDonors.add(new Integer(i));
								//System.out.println("XLOGP: 69	R-OH	-0.467");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 1) {
								xlogP += 0.082;
								hBondDonors.add(new Integer(i));
								//System.out.println("XLOGP: 70	R-OH.1	 0.082");
							}
						}else if (getAtomTypeXCount(ac, atoms[i]) == 1) {
							xlogP -= 0.522;
							hBondDonors.add(new Integer(i));
							//System.out.println("XLOGP: 71	X-OH	-0.522");
						}
					}
				}
			}
			if (symbol.equals("S")) {
				if (bondCount == 1 && maxBondOrder==2) {
					xlogP -= 0.148;
					//System.out.println("XLOGP: 78	A=S	-0.148");
				}else if (bondCount == 2) {
					if (hsCount == 0) {
						xlogP += 0.255;
						//System.out.println("XLOGP: 77	A-S-A	 0.255");
					} else {
						xlogP += 0.419;
						//System.out.println("XLOGP: 76	A-SH	 0.419");
					}
				}else if (bondCount == 3) {
					if (getAtomTypeXCount(ac, atoms[i]) == 1) {
						xlogP -= 1.375;
						//System.out.println("XLOGP: 79	A-SO-A	-1.375");
					}
				}else if (bondCount == 4) {
					if (getAtomTypeXCount(ac, atoms[i]) == 2) {
						xlogP -= 0.168;
						//System.out.println("XLOGP: 80	A-SO2-A	-0.168");
					}
				}
			}
			if (symbol.equals("P")) {
				if (getDoubleBondedSulfurCount(ac, atoms[i]) == 1 && bondCount==5) {
					xlogP += 1.253;
					//System.out.println("XLOGP: 82	S=PA3	 1.253");
				}
				if (getDoubleBondedOxygenCount(ac, atoms[i]) == 1 && bondCount==5) {
					xlogP -= 0.477;
					//System.out.println("XLOGP: 81	O=PA3	-0.477");
				}
			}
			if (symbol.equals("F")) {
				if (getPiSystemsCount(ac, atoms[i]) == 0) {
					xlogP += 0.375;
					//System.out.println("XLOGP: 83	-F.0	 0.375");
				}else if (getPiSystemsCount(ac, atoms[i]) == 1) {
					xlogP += 0.202;
					//System.out.println("XLOGP: 84	-F.1	 0.202");
				}
			}
			if (symbol.equals("Cl")) {
				if (getPiSystemsCount(ac, atoms[i]) == 0) {
					xlogP += 0.512;
					//System.out.println("XLOGP: 85	-Cl.0	 0.512");
				}else if (getPiSystemsCount(ac, atoms[i]) == 1) {
					xlogP += 0.663;
					//System.out.println("XLOGP: 86	-Cl.1	 0.663");
				}
			}
			if (symbol.equals("Br")) {
				if (getPiSystemsCount(ac, atoms[i]) == 0) {
					xlogP += 0.85;
					//System.out.println("XLOGP: 87	-Br.0	 0.85");
				}else if (getPiSystemsCount(ac, atoms[i]) == 1) {
					xlogP += 0.839;
					//System.out.println("XLOGP: 88	-Br.1	 0.839");
				}
			}
			if (symbol.equals("I")) {
				if (getPiSystemsCount(ac, atoms[i]) == 0) {
					xlogP += 1.05;
					//System.out.println("XLOGP: 89	-I.0	 1.05");
				}else if (getPiSystemsCount(ac, atoms[i]) == 1) {
					xlogP += 1.109;
					//System.out.println("XLOGP: 90	I.1	 1.109");
				}
			}
			
//			Halogen pair 1-3
			int halcount=getHalogenCount(ac, atoms[i]);
			if ( halcount== 2) {
				xlogP += 0.137;
				//System.out.println("XLOGP: Halogen 1-3 pair	 0.137");
			}else if (halcount==3){
				xlogP += (3*0.137);
				//System.out.println("XLOGP: Halogen 1-3 pair	 0.411");
			}else if (halcount==4){
				xlogP += (6*0.137);
				//System.out.println("XLOGP: Halogen 1-3 pair	 1.902");
			}
			
//			sp2 Oxygen 1-5 pair
			if (getPresenceOfCarbonil(ac, atoms[i]) == 2) {// sp2 oxygen 1-5 pair
				if(!rs.contains(atoms[i])) { 
					xlogP += 0.580;
					//System.out.println("XLOGP: sp2 Oxygen 1-5 pair	 0.580");
				}
			}
		}
		
		////System.out.println("\nxlogP before correction factors:"+xlogP);
		/*Descriptor acc = new HBondAcceptorCountDescriptor();
		Object[] paramsAcc = {new Boolean(false)};
		acc.setParameters(paramsAcc);
		Descriptor don = new HBondDonorCountDescriptor();
		Object[] paramsDon = {new Boolean(false)};
		don.setParameters(paramsDon);
		int acceptors = ((IntegerResult) acc.calculate(ac).getValue()).intValue();
		int donors = ((IntegerResult) don.calculate(ac).getValue()).intValue();
		if (donors > 0 && acceptors > 0) {
			//System.out.println("XLOGP: Internal HBonds	0.429");
			xlogP += 0.429;
			// internal H-bonds
		}*/
		AtomContainer path=null;
		for (int i=0; i<hBondAcceptors.size();i++){
			for (int j=0; j<hBondDonors.size();j++){
				if (rs.contains(atoms[((Integer)hBondAcceptors.get(i)).intValue()]) && rs.contains(atoms[((Integer)hBondDonors.get(j)).intValue()])){
					resetVisitedFlags(atoms);
					path=new org.openscience.cdk.AtomContainer();
					PathTools.depthFirstTargetSearch(ac, atoms[((Integer)hBondAcceptors.get(i)).intValue()], atoms[((Integer)hBondDonors.get(j)).intValue()],path);
					if (path.getAtomCount()==4){
						xlogP += 0.429;
						//System.out.println("XLOGP: Internal HBonds	 0.429");
					}
				}else if (rs.contains(atoms[((Integer)hBondAcceptors.get(i)).intValue()]) || rs.contains(atoms[((Integer)hBondDonors.get(j)).intValue()])){
					resetVisitedFlags(atoms);
					path=new org.openscience.cdk.AtomContainer();
					PathTools.depthFirstTargetSearch(ac, atoms[((Integer)hBondAcceptors.get(i)).intValue()], atoms[((Integer)hBondDonors.get(j)).intValue()],path);
					if (path.getAtomCount()==5){
						xlogP += 0.429;
						//System.out.println("XLOGP: Internal HBonds	 0.429");
					}
				}
				
			}
		}
		resetVisitedFlags(atoms);
		
		SmilesParser sp = new SmilesParser();
		AtomContainer paba = sp.parseSmiles("CS(=O)(=O)c1ccc(N)cc1");
		// p-amino sulphonic acid
		if (UniversalIsomorphismTester.isSubgraph((org.openscience.cdk.AtomContainer)ac, paba)) {
			xlogP -= 0.501;
			//System.out.println("XLOGP: p-amino sulphonic acid	-0.501");
		}

		AtomContainer aminoacid = sp.parseSmiles("NCC(=O)O");
		// alpha amino acid
		if (UniversalIsomorphismTester.isSubgraph((org.openscience.cdk.AtomContainer)ac, aminoacid)) {
			xlogP -= 2.166;
			//System.out.println("XLOGP: alpha amino acid	-2.166");
		}

		AtomContainer salicilic = sp.parseSmiles("O=C(O)c1ccccc1O");
		// salicylic acid
		if (ac.getAtomCount()==salicilic.getAtomCount()){
			if (UniversalIsomorphismTester.isSubgraph((org.openscience.cdk.AtomContainer)ac, salicilic)) {
				xlogP += 0.554;
				//System.out.println("XLOGP: salicylic acid	 0.554");
			}
		}

		AtomContainer orthopair = sp.parseSmiles("OCCO");
		// ortho oxygen pair
		if (UniversalIsomorphismTester.isSubgraph((org.openscience.cdk.AtomContainer)ac, orthopair)) {
			xlogP -= 0.268;
			//System.out.println("XLOGP: Ortho oxygen pair	-0.268");
		}

		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(xlogP));
	}
	
	private void resetVisitedFlags(org.openscience.cdk.interfaces.Atom []atoms){
		for (int i = 0; i < atoms.length; i++) {
			atoms[i].setFlag(CDKConstants.VISITED, false);
		}
	}

	/**
	 *  Gets the hydrogenCount attribute of the XLogPDescriptor object.
	 *
	 *@param  ac    Description of the Parameter
	 *@param  atom  Description of the Parameter
	 *@return       The hydrogenCount value
	 */
	private int getHydrogenCount(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		org.openscience.cdk.interfaces.Atom[] neighboors = ac.getConnectedAtoms(atom);
		int hcounter = 0;
		for (int i = 0; i < neighboors.length; i++) {
			if (neighboors[i].getSymbol().equals("H")) {
				hcounter += 1;
			}
		}
		return hcounter;
	}


	/**
	 *  Gets the HalogenCount attribute of the XLogPDescriptor object.
	 *
	 *@param  ac    Description of the Parameter
	 *@param  atom  Description of the Parameter
	 *@return       The alogenCount value
	 */
	private int getHalogenCount(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		org.openscience.cdk.interfaces.Atom[] neighbours = ac.getConnectedAtoms(atom);
		int acounter = 0;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i].getSymbol().equals("F") || neighbours[i].getSymbol().equals("I") || neighbours[i].getSymbol().equals("Cl") || neighbours[i].getSymbol().equals("Br")) {
				acounter += 1;
			}
		}
		return acounter;
	}

	/**
	 *  Gets the atomType X Count attribute of the XLogPDescriptor object.
	 *
	 *@param  ac    Description of the Parameter
	 *@param  atom  Description of the Parameter
	 *@return       The nitrogenOrOxygenCount value
	 */
	private int getAtomTypeXCount(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		org.openscience.cdk.interfaces.Atom[] neighbours = ac.getConnectedAtoms(atom);
		int nocounter = 0;
		Bond bond=null;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i].getSymbol().equals("N") || neighbours[i].getSymbol().equals("O") && !neighbours[i].getFlag(CDKConstants.ISAROMATIC)) {
				//if (ac.getMaximumBondOrder(neighbours[i]) == 1.0) {
				bond = ac.getBond(neighbours[i], atom);
				if (bond.getOrder() != 2.0) {
					nocounter += 1;
				}
			}
		}
		return nocounter;
	}


	/**
	 *  Gets the aromaticCarbonsCount attribute of the XLogPDescriptor object.
	 *
	 *@param  ac    Description of the Parameter
	 *@param  atom  Description of the Parameter
	 *@return       The aromaticCarbonsCount value
	 */
	private int getAromaticCarbonsCount(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		org.openscience.cdk.interfaces.Atom[] neighbours = ac.getConnectedAtoms(atom);
		int carocounter = 0;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i].getSymbol().equals("C")) {
				if (neighbours[i].getFlag(CDKConstants.ISAROMATIC)) {
					carocounter += 1;
				}
			}
		}
		return carocounter;
	}


	/**
	 *  Gets the carbonsCount attribute of the XLogPDescriptor object.
	 *
	 *@param  ac    Description of the Parameter
	 *@param  atom  Description of the Parameter
	 *@return       The carbonsCount value
	 */
	private int getCarbonsCount(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		org.openscience.cdk.interfaces.Atom[] neighbours = ac.getConnectedAtoms(atom);
		int ccounter = 0;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i].getSymbol().equals("C")) {
				if (!neighbours[i].getFlag(CDKConstants.ISAROMATIC)) {
					ccounter += 1;
				}
			}
		}
		return ccounter;
	}

	/**
	 *  Gets the oxygenCount attribute of the XLogPDescriptor object.
	 *
	 *@param  ac    Description of the Parameter
	 *@param  atom  Description of the Parameter
	 *@return       The carbonsCount value
	 */
	private int getOxygenCount(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		org.openscience.cdk.interfaces.Atom[] neighbours = ac.getConnectedAtoms(atom);
		int ocounter = 0;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i].getSymbol().equals("O")) {
				if (!neighbours[i].getFlag(CDKConstants.ISAROMATIC)) {
					ocounter += 1;
				}
			}
		}
		return ocounter;
	}
	

	/**
	 *  Gets the doubleBondedCarbonsCount attribute of the XLogPDescriptor object.
	 *
	 *@param  ac    Description of the Parameter
	 *@param  atom  Description of the Parameter
	 *@return       The doubleBondedCarbonsCount value
	 */
	private int getDoubleBondedCarbonsCount(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		org.openscience.cdk.interfaces.Atom[] neighbours = ac.getConnectedAtoms(atom);
		Bond bond = null;
		int cdbcounter = 0;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i].getSymbol().equals("C")) {
				bond = ac.getBond(neighbours[i], atom);
				if (!neighbours[i].getFlag(CDKConstants.ISAROMATIC)) {
					if (bond.getOrder() == 2.0) {
						cdbcounter += 1;
					}
				}
			}
		}
		return cdbcounter;
	}


	/**
	 *  Gets the doubleBondedOxygenCount attribute of the XLogPDescriptor object.
	 *
	 *@param  ac    Description of the Parameter
	 *@param  atom  Description of the Parameter
	 *@return       The doubleBondedOxygenCount value
	 */
	private int getDoubleBondedOxygenCount(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		org.openscience.cdk.interfaces.Atom[] neighbours = ac.getConnectedAtoms(atom);
		Bond bond = null;
		int odbcounter = 0;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i].getSymbol().equals("O")) {
				bond = ac.getBond(neighbours[i], atom);
				if (!neighbours[i].getFlag(CDKConstants.ISAROMATIC)) {
					if (bond.getOrder() == 2.0) {
						odbcounter += 1;
					}
				}
			}
		}
		return odbcounter;
	}


	/**
	 *  Gets the doubleBondedSulfurCount attribute of the XLogPDescriptor object.
	 *
	 *@param  ac    Description of the Parameter
	 *@param  atom  Description of the Parameter
	 *@return       The doubleBondedSulfurCount value
	 */
	private int getDoubleBondedSulfurCount(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		org.openscience.cdk.interfaces.Atom[] neighbours = ac.getConnectedAtoms(atom);
		Bond bond = null;
		int odbcounter = 0;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i].getSymbol().equals("S")) {
				bond = ac.getBond(neighbours[i], atom);
				if (!neighbours[i].getFlag(CDKConstants.ISAROMATIC)) {
					if (bond.getOrder() == 2.0) {
						odbcounter += 1;
					}
				}
			}
		}
		return odbcounter;
	}


	/**
	 *  Gets the doubleBondedNitrogenCount attribute of the XLogPDescriptor object.
	 *
	 *@param  ac    Description of the Parameter
	 *@param  atom  Description of the Parameter
	 *@return       The doubleBondedNitrogenCount value
	 */
	private int getDoubleBondedNitrogenCount(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		org.openscience.cdk.interfaces.Atom[] neighbours = ac.getConnectedAtoms(atom);
		Bond bond = null;
		int ndbcounter = 0;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i].getSymbol().equals("N")) {
				bond = ac.getBond(neighbours[i], atom);
				if (!neighbours[i].getFlag(CDKConstants.ISAROMATIC)) {
					if (bond.getOrder() == 2.0) {
						ndbcounter += 1;
					}
				}
			}
		}
		return ndbcounter;
	}


	/**
	 *  Gets the aromaticNitrogensCount attribute of the XLogPDescriptor object.
	 *
	 *@param  ac    Description of the Parameter
	 *@param  atom  Description of the Parameter
	 *@return       The aromaticNitrogensCount value
	 */
	private int getAromaticNitrogensCount(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		org.openscience.cdk.interfaces.Atom[] neighbours = ac.getConnectedAtoms(atom);
		int narocounter = 0;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i].getSymbol().equals("N")) {
				if (neighbours[i].getFlag(CDKConstants.ISAROMATIC)) {
					narocounter += 1;
				}
			}
		}
		return narocounter;
	}



	// a piSystem is a double or triple or aromatic bond:
	/**
	 *  Gets the piSystemsCount attribute of the XLogPDescriptor object.
	 *
	 *@param  ac    Description of the Parameter
	 *@param  atom  Description of the Parameter
	 *@return       The piSystemsCount value
	 */
	private int getPiSystemsCount(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		org.openscience.cdk.interfaces.Atom[] neighbours = ac.getConnectedAtoms(atom);
		int picounter = 0;
		for (int i = 0; i < neighbours.length; i++) {
			if (ac.getMaximumBondOrder(neighbours[i]) > 1.0) {
				picounter += 1;
			}
		}
		return picounter;
	}

	/**
	 *  Gets the presenceOfN=O attribute of the XLogPDescriptor object.
	 *
	 *@param  ac    Description of the Parameter
	 *@param  atom  Description of the Parameter
	 *@return       The presenceOfCarbonil value
	 */
	private boolean getPresenceOfNitro(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		org.openscience.cdk.interfaces.Atom[] neighbours = ac.getConnectedAtoms(atom);
		org.openscience.cdk.interfaces.Atom[] second = null;
		Bond bond = null;
		//int counter = 0;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i].getSymbol().equals("N")) {
				second = ac.getConnectedAtoms(neighbours[i]);
				for (int b = 0; b < second.length; b++) {
					if (second[b].getSymbol().equals("O")) {
						bond = ac.getBond(neighbours[i], second[b]);
						if (bond.getOrder() == 2.0) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 *  Gets the presenceOfCarbonil attribute of the XLogPDescriptor object.
	 *
	 *@param  ac    Description of the Parameter
	 *@param  atom  Description of the Parameter
	 *@return       The presenceOfCarbonil value
	 */
	private int getPresenceOfCarbonil(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		org.openscience.cdk.interfaces.Atom[] neighbours = ac.getConnectedAtoms(atom);
		org.openscience.cdk.interfaces.Atom[] second = null;
		Bond bond = null;
		int counter = 0;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i].getSymbol().equals("C")) {
				second = ac.getConnectedAtoms(neighbours[i]);
				for (int b = 0; b < second.length; b++) {
					if (second[b].getSymbol().equals("O")) {
						bond = ac.getBond(neighbours[i], second[b]);
						if (bond.getOrder() == 2.0) {
							counter +=1;
						}
					}
				}
			}
		}
		return counter;
	}

	
	// C must be sp2 or sp3
	// and, for all distances C-1-2-3-4 only C atoms are permitted
	/**
	 *  Gets the ifCarbonIsHydrophobic attribute of the XLogPDescriptor object.
	 *
	 *@param  ac    Description of the Parameter
	 *@param  atom  Description of the Parameter
	 *@return       The ifCarbonIsHydrophobic value
	 */
	private boolean getIfCarbonIsHydrophobic(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		org.openscience.cdk.interfaces.Atom[] first = ac.getConnectedAtoms(atom);
		org.openscience.cdk.interfaces.Atom[] second = null;
		org.openscience.cdk.interfaces.Atom[] third = null;
		org.openscience.cdk.interfaces.Atom[] fourth = null;
		if (first.length > 0) {
			for (int i = 0; i < first.length; i++) {
				if (first[i].getSymbol().equals("C") || first[i].getSymbol().equals("H")) {
				} else {
					return false;
				}
				second = ac.getConnectedAtoms(first[i]);
				if (second.length > 0) {
					for (int b = 0; b < second.length; b++) {
						if (second[b].getSymbol().equals("C") || second[b].getSymbol().equals("H")) {
						} else {
							return false;
						}
						third = ac.getConnectedAtoms(second[b]);
						if (third.length > 0) {
							for (int c = 0; c < third.length; c++) {
								if (third[c].getSymbol().equals("C") || third[c].getSymbol().equals("H")) {
								} else {
									return false;
								}
								fourth = ac.getConnectedAtoms(third[c]);
								if (fourth.length > 0) {
									for (int d = 0; d < fourth.length; d++) {
										if (fourth[d].getSymbol().equals("C") || fourth[d].getSymbol().equals("H")) {
										} else {
											return false;
										}
									}
								} else {
									return false;
								}
							}
						} else {
							return false;
						}
					}
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}



	/**
	 *  Gets the parameterNames attribute of the XLogPDescriptor object.
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "checkAromaticity";
		return params;
	}



	/**
	 *  Gets the parameterType attribute of the XLogPDescriptor object.
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		Object[] paramTypes = new Object[1];
		paramTypes[0] = new Boolean(true);
		return paramTypes;
	}
}

