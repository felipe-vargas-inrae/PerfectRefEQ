package utils;

import equations.EquationExpression;
import equations.OWLFunctionalSyntaxFactoryEq;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import vocab.GenericOntology;
import vocab.PerfectRefEquations;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.DataProperty;

public class OntologyUtils {

    public static String quoted (String s){
        return "<"+s+">";
    }


    public static OWLOntology loadOntology(String path){

        final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        try {
            final OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File(path));


            // this is required since our axiom is not read  as it because is not part of the owl/rdfs standards
            //OWLOntology ontology2  = manager.createOntology(ontology.getOntologyID().getOntologyIRI().get());

            Set<OWLAxiom> axiomsTemp = new HashSet<>(ontology.getAxioms());// copy to avoid clean

            ontology.removeAxioms(ontology.getAxioms());// remove all the axioms

            ontology.addAxioms(mapDefinedbyEqAxiom(axiomsTemp));


            return ontology;

        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Set<OWLAxiom> mapDefinedbyEqAxiom(Set<OWLAxiom> axioms){
//        Set<OWLAxiom> axioms = ontology.getAxioms();

        // clean type
        Set<OWLAxiom> newAxioms = axioms.stream().map(x->{
            if(x instanceof OWLAnnotationAssertionAxiom){
                OWLAnnotationAssertionAxiom anotaAx= (OWLAnnotationAssertionAxiom) x;

                OWLAxiom newAx = x;
                if(anotaAx.getProperty().getIRI().getIRIString().equals(PerfectRefEquations.definedByEquation)){

                    IRI iri = anotaAx.getSubject().asIRI().get();

                    newAx = OWLFunctionalSyntaxFactoryEq.DefinedByEquation(
                            DataProperty(iri),
                            new EquationExpression(anotaAx.getValue().asLiteral().get().getLiteral())
                    );
                }


                return  newAx;
            }
            else {
                return x;
            }
        }).collect(Collectors.toSet());

        return newAxioms;
    }

}
