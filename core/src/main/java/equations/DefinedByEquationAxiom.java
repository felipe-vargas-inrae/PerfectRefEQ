package equations;

import org.semanticweb.owlapi.model.*;

import java.util.Collection;
import java.util.stream.Stream;

import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;

public class DefinedByEquationAxiom implements OWLPropertyAxiom {

    OWLDataPropertyExpression property;
    EquationExpression equationExpr;

    public DefinedByEquationAxiom(OWLDataPropertyExpression property, EquationExpression equationExpr) {

        this.property = checkNotNull(property, "property cannot be null");
        this.equationExpr = checkNotNull(equationExpr, "equation cannot be null");
    }

    public OWLDataPropertyExpression getProperty() {
        return property;
    }

    public void setProperty(OWLDataPropertyExpression property) {
        this.property = property;
    }

    public EquationExpression getEquationExpr() {
        return equationExpr;
    }

    public void setEquationExpr(EquationExpression equationExpr) {
        this.equationExpr = equationExpr;
    }



    @Override
    public void accept(OWLAxiomVisitor visitor) {

    }

    @Override
    public <O> O accept(OWLAxiomVisitorEx<O> visitor) {
        return null;
    }

    @Override
    public <T extends OWLAxiom> T getAxiomWithoutAnnotations() {
        return null;
    }

    @Override
    public <T extends OWLAxiom> T getAnnotatedAxiom(Stream<OWLAnnotation> annotations) {
        return null;
    }

    @Override
    public boolean isAnnotated() {
        return false;
    }

    @Override
    public AxiomType<?> getAxiomType() {
        //getInstance(OWLSubObjectPropertyOfAxiom.class, 13, "SubObjectPropertyOf", false, false, true);
        return AxiomType.getAxiomType("SubDataPropertyOf");
    }

    @Override
    public OWLAxiom getNNF() {
        return null;
    }

    @Override
    public void accept(OWLObjectVisitor visitor) {

    }

    @Override
    public <O> O accept(OWLObjectVisitorEx<O> visitor) {
        return null;
    }

    @Override
    public int initHashCode() {
        return 0;
    }

    @Override
    public int compareTo(OWLObject owlObject) {
        return 0;
    }

    @Override
    public Stream<?> components() {
        return null;
    }

    @Override
    public int hashIndex() {
        return 0;
    }
}
