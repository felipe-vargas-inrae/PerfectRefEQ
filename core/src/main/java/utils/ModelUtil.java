package utils;


import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.compose.MultiUnion;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileUtils;
import org.apache.jena.graph.Node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ModelUtil {

    public static void unifyModels(Model model, Model computed) {

        model.add(computed);
        computed.close();
    }

    public static Model unifyModelGraph(Model m1, Model m2){

        MultiUnion unionGraph = new MultiUnion(new Graph[] {
                m1.getGraph(),
                m2.getGraph()
        });

        return ModelFactory.createModelForGraph(unionGraph);
    }

    public static Model fromFile(String path){
        if(!path.contains("file:")){
            path= "file://"+ path;
        }
        Model dataModel = ModelFactory.createDefaultModel();
        dataModel.read(path, "urn:dummy", FileUtils.langTurtle);
        return dataModel;
    }

    public static Model fromResultSet(ResultSet rs){
        Model m = ModelFactory.createDefaultModel();
        while(rs.hasNext()){
            QuerySolution qs = rs.next();
            List<String> varNames = new ArrayList<String>();
            qs.varNames().forEachRemaining(varNames::add);
            Resource r = (Resource) qs.get(varNames.get(0));
            Property p = ResourceFactory.createProperty(((Resource) qs.get(varNames.get(1))).getURI());
            RDFNode object = qs.get(varNames.get(2));
            Statement st = ResourceFactory.createStatement(r, p, object);
            m.add(st);
        };
        return m;
    }


}

