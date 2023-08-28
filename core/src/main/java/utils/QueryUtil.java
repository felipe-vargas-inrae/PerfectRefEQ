package utils;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.core.PathBlock;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class QueryUtil {


    public static ResultSet executeQueryStr(String queryStr, Dataset dataset) {
        Query query = QueryFactory.create(queryStr);
        QueryExecution exec = QueryExecutionFactory.create(query, dataset);
        ResultSet results = exec.execSelect();
        return results;
    }

    public static ResultSet executeQueryStr(String queryStr, Model graph) {
        Query query = QueryFactory.create(queryStr);
        return executeQuery(query, graph);
    }

    public static ResultSet executeQuery(String url, Model graph) {
        Query query = QueryFactory.read(url);
        return executeQuery(query, graph);
    }

    public static ResultSet executeQuery(Query query, Model graph) {
        QueryExecution exec = QueryExecutionFactory.create(query, graph);
        ResultSet results = exec.execSelect();
        return results;
    }

    public static void writeInFile(ResultSet rs, String path) throws FileNotFoundException {
        OutputStream outputStream = new FileOutputStream(path);
        ResultSetFormatter.outputAsCSV(outputStream, rs);
    }

    public static boolean isTypeProperty(Node n){
        Set<Node> typeProperties = new HashSet<>(Arrays.asList(RDF.type.asNode(), OWL.Class.asNode()));
        return  typeProperties.contains(n);
    }

    public static void printQuery(Query q){
        System.out.println(q);
    }



}

