package org.eclipse.epsilon.emc.metaedit;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.eclipse.epsilon.emc.metaedit.api.MEAny;
import org.eclipse.epsilon.emc.metaedit.api.MEOop;
import org.eclipse.epsilon.emc.metaedit.api.MetaEditAPIPortType;



public class MEOopUtil {
	
	public static MEOop createMEOop(String s) {
		MEOop o = new MEOop();
		String[] parts = s.split("_");
		o.setAreaID(Integer.parseInt(parts[0]));
		o.setObjectID(Integer.parseInt(parts[1]));
		return o;
	}
	
	public static Object getPropertyValue(MEOop o, String property, MetaEditAPIPortType port) throws RemoteException {
		ArrayList<MEOop> properties = new ArrayList<MEOop>();
		
		for (MEOop p : port.allProperties(o)) {
			properties.add(p);
		}
		
		for (MEOop p : properties) {
			if (property.equals(port.typeName(port.type(p)))) {
				MEAny value = port.valueAt(o, properties.indexOf(p)+1);
				
				String meType = value.getMeType();
				String meValue = value.getMeValue();
				
				if (meType.equalsIgnoreCase("OrderedCollection")) {
					ArrayList<MEOop> result = new ArrayList<MEOop>();
					for (String v : meValue.split(" ")) {
						if (v.trim().length() > 0)
						result.add(createMEOop(v));
					}
					return result;
				}
				else if (meType.equals("Boolean")){
					return Boolean.parseBoolean(meValue);
				}
				else if (meType.equals("String") || meType.equals("Text")) {
					return meValue.substring(1, meValue.length()-1).replace("''", "'");
				}
				else if (meType.equals("MEOop")) {
					return createMEOop(meValue);
				}
				else if (meType.equals("Integer")) {
					return Integer.parseInt(meValue);
				}
				else if (meType.equals("Number")) {
					return Float.parseFloat(meValue);
				}

				return meValue;
			}
		}
		return null;
	}
	
}
