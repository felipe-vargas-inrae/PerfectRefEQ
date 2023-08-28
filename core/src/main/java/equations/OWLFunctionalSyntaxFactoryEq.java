package equations;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

public class OWLFunctionalSyntaxFactoryEq {

    public static DefinedByEquationAxiom DefinedByEquation(
            OWLDataPropertyExpression property, EquationExpression eq) {
        return new DefinedByEquationAxiom(property, eq);
    }
}
