import PerfectRefE.FOFormula;
import PerfectRefE.PerfectRef;
import com.beust.jcommander.JCommander;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;
import org.json.JSONObject;
import org.semanticweb.clipper.hornshiq.rule.Atom;
import org.semanticweb.clipper.hornshiq.rule.CQ;
import org.semanticweb.clipper.sparql.SparqlToCQConverter;
import org.semanticweb.clipper.util.Cq2SparqlConverter;
import org.semanticweb.clipper.util.Ontology2TBoxABox;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import performance.Performance;
import utils.FileUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class Main {

    static String PATH = "/home/mistea/PhD/study/thesis/tutorials/OWL examples/SHACL/perfectRef/src/main/resources/ontology_test/";

    static String ONTOLOGY_URL = "file:///home/mistea/PhD/study/thesis/tutorials/OWL%20examples/SHACL/perfectRef/src/main/resources/ontology_test/dbpedia_cities.ttl";
    static String ONTOLOGY_PATH = PATH +"dbpedia_cities.ttl";
    static String ONTOLOGY_OWL_XML = PATH+ "dbpedia_cities.owl";
    static String ONTOLOGY_TURTLE_TEST1 = PATH + "test1.ttl";


    public static void toOWLXML(){
        Model m = ModelFactory.createDefaultModel();
        m.read(ONTOLOGY_PATH, FileUtils.langTurtle);
        try {
            FileWriter file = new FileWriter(ONTOLOGY_OWL_XML);
            m.write(file, FileUtils.langXML);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createTurtleModelFromOWLAPIObjects (){
//        OWLOntologyManager owlOntologyManager = OWLManager.createOWLOntologyManager();
//
//
//
//        OntologyFactoryTest.ontologyExample1
//        try {
//            //owlOntologyManager.saveOntology(ontology,System.out);
//
//            //TurtleDocumentFormat turtleFormat = new TurtleDocumentFormat();
//            //turtleFormat.setDefaultPrefix(ontology.getOntologyID().getOntologyIRI().get().getIRIString() + "/");
//            File fileout = new File(ONTOLOGY_TURTLE_TEST1);
//            owlOntologyManager.saveOntology(ontology, new TurtleDocumentFormat(), new FileOutputStream(fileout));
//        } catch (OWLOntologyStorageException | IOException e) {
//            e.printStackTrace();
//        }
    }
    public static boolean isAbsolute(String path){
        return path.startsWith("/");
    }

    public static String fullURIOrPath(JSONObject config, String key ,boolean isURI){
        String start = "";
        if(isURI){
            start = "file://";
        }
        String currentValue = config.getString(key);
        if (isAbsolute(currentValue)){
            return start+config.getString(key);
        }

        String root = config.getString("rootPath");
        if (root.isEmpty()){
            root = new File("").getAbsolutePath()+"/";
        }
        return start+root+config.getString(key);
    }

    public static void run(JSONObject config){

        //performance info
        // performance info
        Performance performance = new Performance();
        performance.printMemoryUsage();
        performance.addMetadata("experimentName",config.getString("experimentName"));
//        performance.addMetadata("method",config.getString("method"));
        performance.start();

        // outputs
        //String outInferencePath = fullURIOrPath(config, "outInferencePath", false);
        String outResultSet =  fullURIOrPath(config, "outResultSet", false);
        String outRewrite =  fullURIOrPath(config, "outRewrite", false);
        // inputs
        String graphPath = fullURIOrPath(config, "graphPath", true);
        String ontologyPath = fullURIOrPath(config, "ontologyPath", false);
        String queryPath =  fullURIOrPath(config, "queryPath", true);

        String outReportPath = fullURIOrPath(config, "outReportPath", false);

        Application.evaluateQueryBasedOnRewriteE(
                ontologyPath,
                graphPath,
                queryPath,
                outRewrite,
                outResultSet,
                performance
        );

        performance.writeStages(outReportPath);
    }


    public static void main(String args []) throws  IOException {

        MainArgs jArgs = new MainArgs();
        JCommander cmd = JCommander.newBuilder()
                .addObject(jArgs)
                .build();
        cmd.parse(args);

        String pathJsonConf = jArgs.getConfig();


        JSONObject config = FileUtil.readJsonFile(pathJsonConf);

        run(config);


//        SparqlToCQConverter s = new  SparqlToCQConverter();
//
//        Query areaQ= QueryFactory.create("PREFIX ex: <http://www.meteo-example.org#>\n"+
//
//                "PREFIX dbo: <http://dbpedia.org/ontology/>\n"+
//                "PREFIX dbp: <http://dbpedia.org/property/>\n"+
//                "SELECT ?X WHERE {?X a dbo:Settlement. ?X ex:area ?area. ?X dbp:populationTotal ?population. FILTER(?area>3)}");
//
//        CQ aCQ= s.compileQuery(areaQ);
//        System.out.println(aCQ.toString());
////        System.out.println(aCQ.getBody().get(0));
////        System.out.println(aCQ.getBody().get(0).isConcept());
////        System.out.println(aCQ.getBody().get(0).isRole());
//
//        Ontology2TBoxABox tBoxABox = new Ontology2TBoxABox();
//        tBoxABox.extractFromDoc(ONTOLOGY_PATH);

//        System.out.println(tBoxABox.getTbox().getAxioms());
//
//        System.out.println("ABOX -------------------");
//        System.out.println(tBoxABox.getAbox().getAxioms());


        //System.out.println(tBoxABox.getTbox().getAxioms().stream().findFirst().get());


//        PerfectRef pref = new PerfectRef();
//
//        FOFormula originalQuery = new FOFormula(aCQ.getHead(), aCQ.getBody());
//
//        LinkedList<FOFormula> newUCQ =  pref.rewrite(originalQuery, tBoxABox.getTbox());
//
//        List<CQ> newUCQAsCQ = newUCQ.stream().map(x -> new CQ(new Atom(), x.getAtoms() ) ).collect(Collectors.toList());
//
//        Cq2SparqlConverter revert = new Cq2SparqlConverter();
//        String resultQ = revert.convert(newUCQAsCQ, "any");
//
//        System.out.println(resultQ);
//        Model m = ModelFactory.createDefaultModel();
//        m.read(ONTOLOGY_PATH, FileUtils.langTurtle);


//        final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//        try {
//            final OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File(ONTOLOGY_PATH));
//            System.out.println("axioms count "+ontology.getAxiomCount());
//            System.out.println("TBox axioms "+ontology.getAxioms());
//
//            Set<OWLAxiom> axioms = ontology.getAxioms();
//
//            for (OWLAxiom ax : axioms){
//                System.out.println(ax);
//            }
//
//        } catch (OWLOntologyCreationException e) {
//            e.printStackTrace();
//        }
    }
}
