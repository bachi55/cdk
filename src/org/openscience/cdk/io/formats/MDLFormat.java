/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307  USA.
 */
package org.openscience.cdk.io.formats;

/**
 * @cdk.module io
 */
public class MDLFormat implements ChemFormatMatcher {

    public MDLFormat() {}
    
    public String getFormatName() {
        return "MDL Mol/SDF";
    }

    public String getReaderClassName() { 
      return "org.openscience.cdk.io.MDLReader";
    };
    public String getWriterClassName() { 
      return "org.openscience.cdk.io.MDLWriter";
    };

    public boolean matches(int lineNumber, String line) {
        if (lineNumber == 4 && (line.indexOf("v2000") >= 0 ||
                                line.indexOf("V2000") >= 0)) {
            return true;
        } else if (line.startsWith("M  END")) {
            return true;
        } else if (lineNumber == 4 && line.length()>7) {
            // possibly a MDL mol file
            try {
                String atomCountString = line.substring(0, 3).trim();
                String bondCountString = line.substring(3, 6).trim();
                new Integer(atomCountString);
                new Integer(bondCountString);
                boolean mdlFile = true;
                if (line.length() > 6) {
                    String remainder = line.substring(6).trim();
                    for (int i = 0; i < remainder.length(); ++i) {
                        char c = remainder.charAt(i);
                        if (!(Character.isDigit(c) || Character.isWhitespace(c))) {
                            mdlFile = false;
                        }
                    }
                }
                // all tests succeeded, likely to be a MDL file
                if (mdlFile) {
                    return true;
                }
            } catch (NumberFormatException nfe) {
                // Integers not found on fourth line; therefore not a MDL file
            }
        }
        return false;
    }

}
