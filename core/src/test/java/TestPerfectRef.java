import PerfectRefE.FOFormula;
import PerfectRefE.PerfectRef;
import PerfectRefE.Unifier;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.junit.Test;
import org.semanticweb.clipper.hornshiq.rule.Atom;
import org.semanticweb.clipper.hornshiq.rule.CQ;
import org.semanticweb.clipper.hornshiq.rule.Variable;
import org.semanticweb.clipper.sparql.SparqlToCQConverter;
import org.semanticweb.owlapi.model.OWLOntology;
import vocab.UniverityOntology;

import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class TestPerfectRef {

    @Test
    public void entail(){

    }

    @Test
    public  void compileQueryExample1(){

        SparqlToCQConverter s = new  SparqlToCQConverter();

        Query areaQ= QueryFactory.create("PREFIX ex: <http://www.meteo-example.org#>\n"+
                "PREFIX dbo: <http://dbpedia.org/ontology/>\n"+
                "PREFIX dbp: <http://dbpedia.org/property/>\n"+
                "SELECT ?X WHERE {?X a dbo:Settlement. ?X ex:area ?area. ?X dbp:populationTotal ?population. FILTER(?area>3)}");

        CQ aCQ= s.compileQuery(areaQ);

        String expected = "ans(X) :- Settlement(X), area(X,Area), populationTotal(X,Population).";

        assertEquals(expected, aCQ.toString());

        for(Variable v:aCQ.getNonDistinguishedVars()){
            assertFalse(v.isDistinguished());
            assertFalse(v.isShared());// none of the variables is shared
        }
        for(Variable v:aCQ.getDistinguishedVariables()){
            assertTrue(v.isDistinguished());
        }
    }

    @Test
    public  void compileQueryExample2SharedVariable(){

        SparqlToCQConverter s = new  SparqlToCQConverter();

        // teaches(x, y), Course(y)
        Query areaQ= QueryFactory.create("PREFIX ex: <http://www.meteo-example.org#>\n"+
                "PREFIX dbo: <http://dbpedia.org/ontology/>\n"+
                "PREFIX dbp: <http://dbpedia.org/property/>\n"+
                "SELECT ?X WHERE {?X ex:teaches ?Y. ?Y a ex:Course.}");

        CQ aCQ= s.compileQuery(areaQ);

        String expected = "ans(X) :- teaches(X,Y), Course(Y).";

        assertEquals(expected, aCQ.toString());

        for(Variable v:aCQ.getNonDistinguishedVars()){
            assertFalse(v.isDistinguished());
            if(v.getName().equals("Y")){ //SHOULD BE SHARED
                assertTrue(v.isShared());
            } else{
                assertFalse(v.isShared());
            }
        }
        for(Variable v:aCQ.getDistinguishedVariables()){
            assertTrue(v.isDistinguished());
        }

    }

    // unify
    // q(x) :- teaches(x, y) nor teaches(z, y) --> teaches(x, y)
    @Test
    public  void unificableExample1(){

        String property = "teaches";
        String classCourse = "Course";
        Variable var1 = new Variable("X");
        var1.setDistinguished(true);
        Variable var2 = new Variable("Y");
        var2.setShared(true);
        Variable var3 = new Variable("Z");

        //teaches(x, y)
        Atom g1 = Atom.createRole(property, var1, var2);
        //teaches(z, y)
        Atom g2 = Atom.createRole(property, var3, var2);
        //Course(y)
        Atom g3 = Atom.createClass(classCourse, var2);
        //Course(z)
        Atom g4 = Atom.createClass(classCourse, var2);


        //teaches(x, y) nor teaches(z, y) --> teaches(x, y)
        assertTrue(Unifier.unifiable(g1, g2));
        assertEquals( g1, Unifier.unifyAtoms(g1, g2));

        //Course(y_bound) vs Course(z_unbound) --> Course(y)
        assertTrue(Unifier.unifiable(g3, g4));
        assertEquals( g3, Unifier.unifyAtoms(g3, g4));
        assertEquals( g3, Unifier.unifyAtoms(g4, g3));

    }

    @Test
    public void unifyVars()
    {
        Variable var1 = new Variable("X");
        var1.setDistinguished(true);
        Variable var2 = new Variable("Y");
        var2.setShared(true);
        Variable var3UnBound = new Variable("Z");
        Variable var4UnBound = new Variable("W");
        Variable var5 = new Variable("X");
        var5.setDistinguished(true);

        assertEquals(Unifier.unifyVars(var1, var5).getName(), "X");//equals
        assertEquals(Unifier.unifyVars(var1, var3UnBound).getName(), "X");//bound vs unbound
        assertEquals(Unifier.unifyVars(var4UnBound, var2).getName(), "Y");//unbound vs bound
        assertTrue(Unifier.unifyVars(var3UnBound, var4UnBound).getName().matches("^z_[a-zA-Z0-9]+$"));//unbound vs unbound -> new var

    }

    @Test
    public void rewriteTeachCourse() throws CloneNotSupportedException {

        String teaches = vocab.UniverityOntology.teaches.getIRIString();
        String Professor = vocab.UniverityOntology.Professor.getIRIString();

        Variable var1 = new Variable("X");
        var1.setDistinguished(true);
        Variable var2 = new Variable("Y");

        //teaches(x, y)
        Atom g1 = Atom.createRole(teaches, var1, var2);

        //Professor(x) validation
        Atom g2 = Atom.createClass(Professor, var1);

        PerfectRef pref = new PerfectRef();
        Atom [] atoms = {g1};

        // create ontology
        OWLOntology ontology = OntologyFactoryTest.ontologyExample2Teaches();
        FOFormula initialQuery = new FOFormula(Atom.createDefaultHeader(),Arrays.asList(atoms.clone()) );

        LinkedList<FOFormula> newUCQ =  pref.rewrite(initialQuery, ontology );


        FOFormula f1 = new FOFormula(initialQuery.getHead(), Arrays.asList(atoms.clone()));
        FOFormula f2 = new FOFormula(initialQuery.getHead(), Arrays.asList(new Atom[] {g2}));

        LinkedList<FOFormula> expectedUCQ = new LinkedList<>() ;
        expectedUCQ.addAll(Arrays.asList(f1, f2));

        assertEquals(newUCQ, expectedUCQ);


    }




    @Test
    public void rewriteTeachCourseShared() throws CloneNotSupportedException {
        // test g(x) :- teach(x,y)  and Course(y)

        String teaches = UniverityOntology.teaches.getIRIString();
        String Course = UniverityOntology.Course.getIRIString();

        Variable var1 = new Variable("X");
        var1.setDistinguished(true);
        Variable var2 = new Variable("Y");
        var2.setShared(true);
        Variable varInvisible = new Variable("_");

        //teaches(x, y)
        Atom g1 = Atom.createRole(teaches, var1, var2);
        //Course(y)
        Atom g2 = Atom.createClass(Course, var2);

        //validation
        //teaches(_, y)
        Atom g3 = Atom.createRole(teaches, varInvisible, var2);

        PerfectRef pref = new PerfectRef();
        Atom [] atoms = {g1, g2};

        // create ontology
        OWLOntology ontology = OntologyFactoryTest.ontologyExample2Teaches();

        FOFormula query = new FOFormula(Atom.createDefaultHeader(), Arrays.asList(g1, g2));
        LinkedList<FOFormula> newUCQ =  pref.rewrite(query, ontology );


        assertTrue(newUCQ.size()==4);
        if(newUCQ.size()==4){

            assertEquals(newUCQ.get(0).getAtoms(),Arrays.asList(g1, g2) ) ;
            //assertEquals(newUCQ.get(0).getAtoms().get(),Arrays.asList(g1, g3) ) ;

        }

        //PerfectRefE.FOFormula f1 = new PerfectRefE.FOFormula(aCQ.getHead(), Arrays.asList(atoms.clone()));
        int x = 0;
//        PerfectRefE.FOFormula f2 = new PerfectRefE.FOFormula(Arrays.asList(new Atom[] {g2}));
//
//        LinkedList<PerfectRefE.FOFormula> expectedUCQ = new LinkedList<>() ;
//        expectedUCQ.addAll(List.of(f1, f2));
//
//        assertEquals(newUCQ, expectedUCQ);

        // test g(x) :- teach(x,y)  and Course(y)
    }

    @Test
    public void deepCopyFOFormulaTest() throws CloneNotSupportedException {

//        LinkedList <PerfectRefE.FOFormula> l1 = new LinkedList<>();
//        Atom classProfessor= Atom.createClass(UniverityOntology.Professor.getIRIString(), new Variable("X"));
//        PerfectRefE.FOFormula f1 = new PerfectRefE.FOFormula(null, new LinkedList<>(Arrays.asList(classProfessor)));
//        PerfectRefE.FOFormula f2 = PerfectRefE.FOFormula.deepCopyFOFormula(f1);
//        f2.getAtoms().get(0).getVar1().asVariable().setShared(true);
//
//        assertNotEquals(
//                f1.getAtoms().get(0).getVar1().asVariable().isShared(),
//                f2.getAtoms().get(0).getVar1().asVariable().isShared()
//        );
    }
}

//
//    A(x) B -> A B(x)
//        ∃P -> A P (x, _)
//        ∃P- ->  A P (_, x)
//        ∃U -> A U (x, _)