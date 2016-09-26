# Epsilon - MetaEdit+ Integration

Eclipse plugins that extend Epsilon's Model Connectivity (EMC) layer with support for managing [MetaEdit+](http://www.metacase.com/) models using languages of the [Epsilon platform](http://www.eclipse.org/epsilon) to perform activities such as code generation, model validation and model-to-model transformation. The MetaEdit+ EMC driver only supports querying models at this stage.

Example
-----------
The following EOL snippet demonstrates iterating through all the classes in a MetaEdit+ UML model and printing their names and the names and datatypes of their attributes.
```Javascript
for (c in `Class [UML]`.all) {
	c.`Class name`.println();
	for (a in c.`Attributes [UML]`) {
		("- " + a.`Attribute name` + ":" + a.`Data type`).println();
	}
}
```

Screencast (YouTube)
-----------
[![Epsilon - MetaEdit+ Integration](http://img.youtube.com/vi/yvgustUllQE/0.jpg)](https://www.youtube.com/watch?v=yvgustUllQE)


Tips
-----------
* The driver works with MetaEdit's SOAP API so to work you need to click the "API Tool" toolbar button in the main MetaEdit+ window, and then click the "Start Server" button in the popup dialog.
* **Important:** You will need to update to the latest interim version of Epsilon to use the MetaEdit+ driver.
