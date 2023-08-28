import PerfectRefE.FOFormula;
import PerfectRefE.PerfectRef;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.semanticweb.clipper.hornshiq.rule.Atom;
import org.semanticweb.clipper.hornshiq.rule.CQ;
import org.semanticweb.clipper.hornshiq.rule.Term;
import org.semanticweb.clipper.sparql.SparqlToCQConverter;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import performance.Performance;
import utils.FileUtil;
import utils.ModelUtil;
import utils.OntologyUtils;
import utils.QueryUtil;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Application {

    // open the ontology
    // process the SPARQL query
    // perform the rewrite
    public static void evaluateQueryBasedOnRewriteE(
            String ontologyPath,
            String dataPath,
            String queryPath,
            String rewritePath,
            String queryResultPath,
            Performance performance
            ){


        //read ontology

        OWLOntology ontology = OntologyUtils.loadOntology(ontologyPath);
        performance.addStageEndTime("Load ontology");
        // read query
        SparqlToCQConverter s = new  SparqlToCQConverter();
        Query query = QueryFactory.read(queryPath);
        CQ aCQ= s.compileQuery(query);
        performance.addStageEndTime("Transform SQL->CQ");


        //perform rewriting

        PerfectRef pref = new PerfectRef();
        FOFormula originalQuery = new FOFormula(aCQ.getHead(), aCQ.getBody());

        try {
            LinkedList<FOFormula> newUCQ =  pref.rewrite(originalQuery, ontology);
            performance.addStageEndTime("Rewrite Q");

            String SQL = CQSetToSQL(newUCQ, aCQ.getHead());
            FileUtil.writeFile(rewritePath, SQL);//save the rewriting
            performance.addStageEndTime("Transform UCQ->SQL");


            // load the data

            Model dataModel = ModelUtil.fromFile(dataPath);
            performance.addStageEndTime("Load G");

            ResultSet rs = QueryUtil.executeQueryStr(SQL, dataModel);
            performance.addStageEndTime("Evaluate Q'");

            QueryUtil.writeInFile(rs, queryResultPath);
            performance.addStageEndTime("Save Rs");



        } catch (CloneNotSupportedException | FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    private static String CQSetToSQL(LinkedList<FOFormula> UnCQ, Atom head){

        StringBuilder sb = new StringBuilder();

        String selectTemplate = "SELECT %s \n";

        String prefixes = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n";

        String whereStart = "WHERE { \n ";
        String whereEnd = "}";

        // SELECT CLAUSE
        List<Term> selectVars = head.getTerms();
        String vars = selectVars.stream().map(x->x.toSQL()).collect(Collectors.joining(" "));
        String selectClause = String.format(selectTemplate, vars);


        sb.append(prefixes);
        sb.append(selectClause);

        sb.append(whereStart);
        String templateUnion = "%s \n UNION \n";
        for(FOFormula f:UnCQ){
            String q = String.format(templateUnion, f.toSQL());
            sb.append(q);
        }

        int lastUnion = sb.lastIndexOf("UNION");
        sb.delete(lastUnion, sb.length());

        sb.append(whereEnd);
        return sb.toString();
    }
}
