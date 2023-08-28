import PerfectRefE.FOFormula;
import org.junit.Test;
import org.semanticweb.clipper.hornshiq.rule.Atom;
import org.semanticweb.clipper.hornshiq.rule.Constant;
import org.semanticweb.clipper.hornshiq.rule.Variable;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

public class FOFormulaTest {
    @Test
    public void toSQL(){


        String VAR_NAME = "z_1";
        Atom head = Atom.createDefaultHeader();
        Variable headVar = head.getTerm1().asVariable();
        Variable person =  new Variable(VAR_NAME);
        Constant value = new Constant("2.5");


        List<Atom> atoms = Arrays.asList(
                Atom.createClass("Person", person),
                Atom.createRole("height", person, value ),
                Atom.createBINDAtom("<height> * 2",headVar )
        );
        FOFormula f = new FOFormula(head,atoms );

        String actual = f.toSQL();

        String expected = "{\n" +
                person.toSQL()+" <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <Person>.\n" +
                person.toSQL()+" <height> 2.5.\n" +
                "BIND(<height> * 2 AS ?X)\n"+
                "}";

        assertEquals(expected, actual);
    }

    @Test
    public void containedInListIgnoreVars(){
        String VAR_NAME = "z_1";
        Atom head = Atom.createDefaultHeader();
        Variable headVar = head.getTerm1().asVariable();
        Variable person =  new Variable(VAR_NAME);
        Constant value = new Constant("2.5");

        List<Atom> atoms = Arrays.asList(
                Atom.createClass("Person", person),
                Atom.createRole("height", person, value ),
                Atom.createBINDAtom("?v1 * 2",headVar )
        );

        FOFormula f = new FOFormula(head,atoms );

        person =  new Variable("w_12");//other var name

        List<Atom> atomsF2 = Arrays.asList(
                Atom.createClass("Person", person),
                Atom.createRole("height", person, value ),
                Atom.createBINDAtom("?v2 * 2",headVar )
        );

        FOFormula f2 = new FOFormula(head,atomsF2 );

        //assertNotEquals(f,f2);

        assertTrue(f.containedInListIgnoreVars(Arrays.asList(f2)));

        int x = 0;


    }
}



