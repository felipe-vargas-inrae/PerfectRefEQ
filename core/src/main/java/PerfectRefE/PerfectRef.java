package PerfectRefE;

import PerfectRefE.FOFormula;
import equations.DefinedByEquationAxiom;
import equations.EquationExpression;
import org.semanticweb.clipper.hornshiq.rule.Atom;
import org.semanticweb.clipper.hornshiq.rule.DLPredicate;
import org.semanticweb.clipper.hornshiq.rule.Term;
import org.semanticweb.clipper.hornshiq.rule.Variable;
import org.semanticweb.owlapi.model.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
public class PerfectRef {

    public PerfectRef(){

    }
    public LinkedList<FOFormula> rewrite(FOFormula query, OWLOntology tbox) throws CloneNotSupportedException {

        // Making the initial query iterable
        //PerfectRefE.FOFormula f1 = new PerfectRefE.FOFormula(aCQ.getHead(), query);
        LinkedList<FOFormula> PR = new LinkedList<FOFormula> ( List.of(query)) ;


        // Working list PR_prime of the entailes queries in PR.
        LinkedList<FOFormula> PR_PRIME = new LinkedList<>();

        // Run until there are no changes
        while(!PR_PRIME.equals(PR)){
            //Make a deep copy for each iteration (is necessary in order not to mess up bounded variables for previous queries)
            PR_PRIME = deepCopyList(PR);
            //System.out.println("Rewritting "+ PR_PRIME);

            // For every formula in the list
            for(FOFormula q:PR_PRIME){
                // if the formula is not yet processed
                if(!q.isProcessed()){

                    int indexQ = PR.indexOf(q);

                    PR.get(indexQ).setProcessed(true);


                    //task A
                    for(Atom ato:q.getAtoms()){
                        Set<OWLAxiom> axioms = tbox.getAxioms();
                        // for every positive inclusion axiom
                        for(OWLAxiom ax:axioms){
                            //ax.accept();
                            if(isApplicable(ax, ato)){

                                if(ax instanceof DefinedByEquationAxiom){
                                    List<Atom> atomList = entailEquation(ax,ato);

                                    FOFormula qNew = replaceByList(q,ato, atomList);

                                    qNew.setProcessed(false);

                                    // if it is not already: compare ignoring variable terms
                                    if(!qNew.containedInListIgnoreVars(PR)){
                                        PR.add(qNew);
                                    }

                                }

                                else{
                                    //construct new query
                                    // entailment
                                    Atom ato2 = entail(ax,ato);
                                    // replace ato in q by ato/ax
                                    FOFormula qNew = replace(q, ato,ato2);
                                    // union
                                    // if it is not already: compare ignoring variable terms
                                    if(!qNew.containedInListIgnoreVars(PR)){
                                        PR.add(qNew);
                                    }

                                }
                            };
                        }
                    }
                    // task B: Unify atoms of the same kind
                    if(q.getAtoms().size()>1){// at least two items


                        // filter bind atoms
                        List<Atom> l = q.getAtoms().stream().filter(x->x.getPredicate().isDLPredicate()).collect(Collectors.toList());
                        for (Atom g1: l)
                            for (Atom g2: l){
                                if(!g1.equals(g2)){
                                    if(Unifier.unifiable(g1, g2)){
                                        FOFormula f2 = Unifier.reduce(q, g1, g2 );
                                        if(!f2.containedInList(PR)){
                                            PR.add(f2);
                                        }
                                    }
                                }
                            }
                    }
                }
            }

            //break;
        }
        return  PR;
    }

    public static FOFormula replace(FOFormula q, Atom ato, Atom ato2) {
        List<Atom> l =  q.getAtoms().stream().map((x)-> x.equals(ato) ? ato2: x).collect(Collectors.toList());
        return new FOFormula(q.getHead(), l);
    }

    public static FOFormula replaceByList(FOFormula q, Atom ato, List<Atom> atomsEntailed) {
        List<Atom> newAtoms = new LinkedList<>(q.getAtoms());

        int i = newAtoms.indexOf(ato);
        newAtoms.remove(ato);

        newAtoms.addAll(i, atomsEntailed);

        return new FOFormula(q.getHead(), newAtoms);
    }


    public static LinkedList<FOFormula> deepCopyList(LinkedList<FOFormula> l) throws CloneNotSupportedException {
        LinkedList<FOFormula> l2 = new LinkedList<>();
        for(FOFormula formula: l){
            FOFormula formulaCopy = FOFormula.deepCopyFOFormula(formula);
            l2.add(formulaCopy);
        }
        return l2;
    }



    public static boolean isApplicable(OWLAxiom ax, Atom g ){
        boolean isApplicable=false;
        if(g.isConcept()){
            if(ax.isOfType(AxiomType.SUBCLASS_OF)){
                OWLSubClassOfAxiom axSubClass = (OWLSubClassOfAxiom) ax;
                OWLClass gClass = ((DLPredicate) g.getPredicate()).getOwlEntity().asOWLClass();
                isApplicable = gClass.equals(axSubClass.getSuperClass());
            };
        }else if(g.isRole()){
            if(ax.isOfType(AxiomType.SUBCLASS_OF)){
                OWLSubClassOfAxiom axSubClass = (OWLSubClassOfAxiom) ax;
                axSubClass.getSuperClass();
                if(axSubClass.getSuperClass().getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)){
                    OWLObjectSomeValuesFrom right = (OWLObjectSomeValuesFrom) axSubClass.getSuperClass();
                    IRI axiomPredicate = right.getProperty().getNamedProperty().getIRI();
                    IRI queryPredicate = ((DLPredicate) g.getPredicate()).getOwlEntity().getIRI()   ;
                    if(queryPredicate.equals(axiomPredicate))
                    {
                        if(right.getProperty() instanceof  OWLObjectInverseOf) return g.getTerm1().asVariable().isUnbound();
                        else return  g.getTerm2().asVariable().isUnbound();
                    }else {
                        return  false;
                    }
                }
            }
            if(ax instanceof OWLSubObjectPropertyOfAxiom){
                OWLSubDataPropertyOfAxiom axSubDataPro = (OWLSubDataPropertyOfAxiom) ax;
                IRI iri = ((DLPredicate) g.getPredicate()).getOwlEntity().getIRI();
                isApplicable = iri.equals(axSubDataPro.getSuperProperty().asOWLDataProperty().getIRI());
                //Variable
            }

            // Bischof method
            if(ax instanceof DefinedByEquationAxiom){
                IRI atomPredicate = ((DLPredicate) g.getPredicate()).getOwlEntity().getIRI();

                DefinedByEquationAxiom eqAx = (DefinedByEquationAxiom) ax;

                IRI axiomIRI= eqAx.getProperty().asOWLDataProperty().getIRI();

                return axiomIRI.equals(atomPredicate);

            }
        }
        //System.out.println("Axiom:"+ax+" -- Atom:"+g+" applicable="+isApplicable);
        return  isApplicable;
    }

    public static Atom entail(OWLAxiom ax, Atom g ){
        if(g.isConcept()){
            if(ax instanceof  OWLSubClassOfAxiom){
                OWLSubClassOfAxiom axSubClass = (OWLSubClassOfAxiom) ax;
                //OWLClass gClass = ((DLPredicate) g.getPredicate()).getOwlEntity().asOWLClass();

                Variable v = g.getTerm1().asVariable();
                OWLClassExpression left = axSubClass.getSubClass();
                if(left instanceof OWLObjectSomeValuesFrom ){


                    OWLObjectPropertyExpression leftExpre = ((OWLObjectSomeValuesFrom) left).getProperty();


                    Variable newVar = Variable.createNewVariable();
                    String iriPredicate = leftExpre.getNamedProperty().getIRI().getIRIString();
                    if(leftExpre instanceof OWLObjectInverseOf){
                        return  Atom.createRole(iriPredicate, newVar, g.getTerm1().asVariable());
                    } else {
                        return  Atom.createRole(iriPredicate, g.getTerm1().asVariable(), newVar);
                    }
//                    val newVar = new Var("x_"+PerfReformulator.getVC)
//                    val leftPropertyExpr = left.asInstanceOf[OWLObjectSomeValuesFrom].getProperty
//                    val prop = leftPropertyExpr.getNamedProperty()
//                    return if (leftPropertyExpr.isInstanceOf[OWLObjectInverseOf]) new Binary(prop,newVar,e) else new Binary(prop,e,newVar)

                } else {
                    DLPredicate p = new DLPredicate(left.asOWLClass());
                    Atom g2 = new Atom(p, v);
                    return g2;
                }
            }
        }else if(g.isRole()){
            if(ax instanceof OWLSubObjectPropertyOfAxiom){
                OWLSubObjectPropertyOfAxiom axSubPro = (OWLSubObjectPropertyOfAxiom) ax;
                //isApplicable = g.getPredicate().getName() == axSubDataPro.getSuperProperty().getClass().getName();

                Variable v1 = g.getTerm(0).asVariable();
                Variable v2 = g.getTerm(1).asVariable();

                OWLObjectPropertyExpression rightProperty = axSubPro.getSuperProperty();
                OWLObjectPropertyExpression leftProperty = axSubPro.getSubProperty();

                String iriPredicate = leftProperty.getNamedProperty().getIRI().getIRIString();

                if(rightProperty instanceof  OWLObjectInverseOf == leftProperty instanceof  OWLObjectInverseOf){
                    return Atom.createRole(iriPredicate, g.getTerm1().asVariable(), g.getTerm2().asVariable());
                }
                else {
                    return Atom.createRole(iriPredicate, g.getTerm2().asVariable(), g.getTerm1().asVariable());
                }
            }
            else if(ax instanceof  OWLSubClassOfAxiom){
                OWLSubClassOfAxiom axSubClass = (OWLSubClassOfAxiom) ax;
                //isApplicable = g.getPredicate().getName() == axSubDataPro.getSuperProperty().getClass().getName();

                OWLClassExpression right = axSubClass.getSuperClass();
                OWLClassExpression left = axSubClass.getSubClass();

                if(left instanceof OWLClass){
                    OWLObjectPropertyExpression rightProperty = ((OWLObjectSomeValuesFrom) right).getProperty();
                    Term v1 = (rightProperty instanceof  OWLObjectInverseOf)? g.getTerm2() : g.getTerm1();

                    return Atom.createClass(((OWLClass) left).getIRI().getIRIString(), v1.asVariable());
                }

                if(right instanceof OWLObjectSomeValuesFrom && left instanceof OWLObjectSomeValuesFrom  ){

                    OWLObjectPropertyExpression rightProperty = ((OWLObjectSomeValuesFrom) right).getProperty();
                    OWLObjectPropertyExpression leftProperty = ((OWLObjectSomeValuesFrom) left).getProperty();

                    String iriPredicate = leftProperty.getNamedProperty().getIRI().getIRIString();

                    if(rightProperty instanceof  OWLObjectInverseOf == leftProperty instanceof  OWLObjectInverseOf){
                        return Atom.createRole(iriPredicate, g.getTerm1().asVariable(), g.getTerm2().asVariable());
                    }
                    else {
                        return Atom.createRole(iriPredicate, g.getTerm2().asVariable(), g.getTerm1().asVariable());
                    }
                }
            }
        }

        return null;
    }


    public static List<Atom> entailEquation(OWLAxiom ax, Atom g ){
        //we know that ax is an equation axioms
        List<Atom> atoms = new LinkedList<>();
        if(g.isRole()){
            // a new atom by variable
            DefinedByEquationAxiom eqAx = (DefinedByEquationAxiom) ax;

            EquationExpression eqExp = eqAx.getEquationExpr();
            List<OWLDataPropertyExpression> propInputs =eqExp.getPropertyList();
            HashMap<IRI, Variable> mapVarDataPro = new HashMap<>();



            for(OWLDataPropertyExpression p:propInputs){
                IRI pIRI = p.asOWLDataProperty().getIRI();

                Variable newVar= Variable.createNewVariable();
                Atom newVarAtom = Atom.createRole(pIRI.getIRIString(), g.getTerm1(), newVar);


                mapVarDataPro.put(pIRI, newVar);

                atoms.add(newVarAtom);
            }

            String eqReplaced = eqExp.replaceDataProByVars(mapVarDataPro);
            // a bind expression using the created variables
            Atom bindAtom = Atom.createBINDAtom(eqReplaced, g.getTerm2().asVariable());
            atoms.add(bindAtom);
        }

        return atoms;
    }

}
