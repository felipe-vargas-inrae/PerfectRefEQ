import PerfectRefE.FOFormula;
import PerfectRefE.PerfectRef;
import equations.EquationExpression;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.junit.Test;
import org.semanticweb.clipper.hornshiq.rule.Atom;
import org.semanticweb.clipper.hornshiq.rule.CQ;
import org.semanticweb.clipper.hornshiq.rule.Constant;
import org.semanticweb.clipper.hornshiq.rule.Variable;
import org.semanticweb.clipper.sparql.SparqlToCQConverter;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import vocab.GenericOntology;
import utils.OntologyUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.*;

public class DLEquationAxiomsTest {


    @Test
    public void summationEx1() throws CloneNotSupportedException {

        SparqlToCQConverter s = new  SparqlToCQConverter();

        Query areaQ= QueryFactory.create("PREFIX ex: <"+GenericOntology.BASE+">\n"+
                "SELECT ?X WHERE {ex:o1 ex:u1 ?X. FILTER(?X>1)}");

        CQ aCQ= s.compileQuery(areaQ);

        String expected = "ans(X) :- Settlement(X), area(X,Area), populationTotal(X,Population).";

        OWLOntology ontology = OntologyFactoryTest.ontologyBasicSummation();

        PerfectRef pref = new PerfectRef();
        FOFormula initialQuery = new FOFormula(aCQ.getHead(), aCQ.getBody());
        LinkedList<FOFormula> newUCQ =  pref.rewrite(initialQuery, ontology );

        //OWLOntologyImpl

        //assertEquals(expected, aCQ.toString());
        assertTrue(newUCQ.size()==2);

        FOFormula expectedFo = new FOFormula(aCQ.getHead(),
                                Arrays.asList(Atom.createRole(
                                        GenericOntology.u1.getIRIString(),
                                        new Constant(OntologyUtils.quoted(GenericOntology.o1.getIRIString())),
                                        new Variable("X"))));
        expectedFo.setProcessed(true);

        assertEquals(expectedFo.getAtoms().get(0).getTerms(), newUCQ.get(0).getAtoms().get(0).getTerms());

        //bind expression
        Atom bind = newUCQ.get(1).getAtoms().get(2);
        String equation = bind.getTerm1().asConstant().getName();

        assertTrue(equation.matches("^xsd:double\\(\\?z_[a-zA-Z0-9]+\\)\\s\\+\\sxsd:double\\(\\?z_[a-zA-Z0-9]+\\)$"));//validate the equation structure

    }

    @Test
    public void equationCompileTest(){

        String u2 = GenericOntology.u2.getIRIString();
        String u3 =  GenericOntology.u3.getIRIString();
        String u4 =  GenericOntology.u4.getIRIString();
        String u5 =  GenericOntology.u5.getIRIString();
        EquationExpression eq = new EquationExpression("<"+u2+"> + <"+u3+"> - <"+u4+"> / <"+u5+">");

        List<OWLDataProperty> lExpected =  Arrays.asList(
                DataProperty(IRI.create(u2)),
                DataProperty(IRI.create(u3)),
                DataProperty(IRI.create(u4)),
                DataProperty(IRI.create(u5))
        );

        assertEquals(lExpected, eq.getPropertyList());

    }


}
