package org.eclipse.epsilon.emc.metaedit;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.emc.metaedit.api.MEOop;
import org.eclipse.epsilon.emc.metaedit.api.METype;
import org.eclipse.epsilon.emc.metaedit.api.MetaEditAPI;
import org.eclipse.epsilon.emc.metaedit.api.MetaEditAPIPortType;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.exceptions.models.EolEnumerationValueNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.exceptions.models.EolNotInstantiableModelElementTypeException;
import org.eclipse.epsilon.eol.execute.introspection.IPropertyGetter;
import org.eclipse.epsilon.eol.models.IRelativePathResolver;
import org.eclipse.epsilon.eol.models.Model;

public class MetaEditModel extends Model {

	protected MEOop graph = null;
	protected MetaEditAPIPortType port = null;
	protected ArrayList<MEOop> objects = null;
	protected ArrayList<MEOop> relationships = null;
	protected HashSet<Integer> relationshipIds = null;
	
	protected String graphTypeName = "";
	protected String graphName = "";
	public static final String PROPERTY_GRAPH_NAME = "graphName";
	public static final String PROPERTY_GRAPH_TYPE_NAME = "graphType";
	
	public static void main(String[] args) throws Exception {
		MetaEditModel m = new MetaEditModel();
		m.setName("F");
		m.setGraphTypeName("Class Diagram [UML]");
		//m.setGraphName("ChartTool");
		m.setGraphName("Reverse engineered Java (ex 1)");
		m.load();
		
		EolModule module = new EolModule();
		//module.parse("`Class [UML]`.all.println();");
		//module.parse("context Action { constraint SomeName { check: self.name <> '' message: ''} } ")
		//module.parse("for (t in F!Transition.all) { (t.source.name + ' -> ' + t.target.name).println(); }");
		module.parse(new File("examples/uml.eol").toURI());
		module.getContext().getPrettyPrinterManager().addPrettyPrinter(new MetaEditPrettyPrinter(m.port));
		module.getContext().getModelRepository().addModel(m);
		module.execute();
	}
	
	public MetaEditModel() {
		propertyGetter = new MetaEditPropertyGetter();
		propertySetter = new MetaEditPropertySetter();
	}
	
	@Override
	public void load(StringProperties properties, IRelativePathResolver resolver)
			throws EolModelLoadingException {
		super.load(properties, resolver);
		this.graphName = properties.getProperty(PROPERTY_GRAPH_NAME);
		this.graphTypeName = properties.getProperty(PROPERTY_GRAPH_TYPE_NAME);
		load();
	}
	
	@Override
	public void load() throws EolModelLoadingException {
		try {
			MetaEditAPI meServer = new org.eclipse.epsilon.emc.metaedit.api.MetaEditAPILocator();
			port = meServer.getMetaEditAPIPort();
			for (MEOop graph : port.allGoodInstances(new METype(graphTypeName))) {
				if (graphName.equals(port.userPrintString(graph))) {
					this.graph = graph;
				}
			}
			if (this.graph == null) throw new Exception("Graph '" + graphName + "' does not exit.");
		}
		catch (Exception ex) {
			throw new EolModelLoadingException(ex, this);
		}
		
		allContents();
	}

	/**
	 * @since 1.6
	 */
	public boolean isLoaded() {
		return graph != null && objects != null;
	}
	
	@Override
	public Object getEnumerationValue(String enumeration, String label)
			throws EolEnumerationValueNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Collection<MEOop> allContents() {
		
		try {
			if (objects == null) {
				MEOop[] portOps = port.objectSet(graph);
				objects = new ArrayList<>(portOps.length);
				for (MEOop o : portOps) {
					objects.add(o);
				}
				MEOop[] relOps = port.relationshipSet(graph);
				relationships = new ArrayList<>(relOps.length);
				relationshipIds = new HashSet<>(relOps.length);
				
				for (MEOop o : relOps) {
					relationships.add(o);
					relationshipIds.add(o.getObjectID());
				}
			}
		}
		catch (Exception ex) {ex.printStackTrace();}
		
		return objects;
	}

	@Override
	public Collection<?> getAllOfType(String type)
			throws EolModelElementTypeNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<?> getAllOfKind(String type)
			throws EolModelElementTypeNotFoundException {
	
		ArrayList<MEOop> allOfKind = new ArrayList<>();
		
		try {
			for (MEOop o : objects) {
				if (type.equals(port.typeName(port.type(o)))) {
					allOfKind.add(o);
				}
				if (allOfKind.isEmpty()) {
					for (MEOop r : relationships) {
						if (port.typeName(port.type(r)).equals(type)) {
							allOfKind.add(r);
						}
					}
				}
			}
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		return allOfKind;
	}

	@Override
	public Object getTypeOf(Object instance) {
		System.err.println("T: " + getTypeNameOf(instance));
		return getTypeNameOf(instance);
	}

	@Override
	public String getTypeNameOf(Object instance) {
		System.err.println("T: " + getTypeNameOf(instance));
		try {
			return port.typeName(port.type((MEOop) instance));
		} catch (RemoteException e) {
			return "Unknown";
		}
	}

	@Override
	public Object createInstance(String type)
			throws EolModelElementTypeNotFoundException,
			EolNotInstantiableModelElementTypeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getElementById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getElementId(Object instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setElementId(Object instance, String newId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteElement(Object instance) throws EolRuntimeException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean owns(Object instance) {
		return instance instanceof MEOop;
		//return objects.contains(instance) || relationships.contains(instance);
	}

	@Override
	public boolean isInstantiable(String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isModelElement(Object instance) {
		// TODO Auto-generated method stub
		return false;
	}

	protected ArrayList<String> types = null;
	
	@Override
	public boolean hasType(String type) {
		try {
			METype objectType = new METype("OPRRObj");
			METype returnedType = port.subTypeNamed(objectType, type);
			if (objectType.getName().equals(returnedType.getName())) {
				METype relationshipType = new METype("Relationship");
				returnedType = port.subTypeNamed(relationshipType, type);
				return !relationshipType.getName().equals(returnedType.getName());
			}
			else {
				return true;
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean store(String location) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean store() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isRelationship(MEOop b) {
		return relationshipIds.contains(b.getObjectID());
		/*
		for (MEOop candidate : relationships) {
			if (candidate.getAreaID() == b.getAreaID() && 
					candidate.getObjectID() == b.getObjectID()) {
				return true;
			}
		}
		return false;*/
	}

	@Override
	public IPropertyGetter getPropertyGetter() {
		MetaEditPropertyGetter pg = (MetaEditPropertyGetter) propertyGetter;
		pg.setModel(this);
		pg.setPort(port);
		return pg;
	}
	
	public String getGraphTypeName() {
		return graphTypeName;
	}
	
	public void setGraphTypeName(String graphTypeName) {
		this.graphTypeName = graphTypeName;
	}
	
	public String getGraphName() {
		return graphName;
	}
	
	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}
	
	public MEOop getGraph() {
		return graph;
	}
}
