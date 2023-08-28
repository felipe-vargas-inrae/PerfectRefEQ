import equations.EquationExpression;
import equations.OWLFunctionalSyntaxFactoryEq;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import vocab.GenericOntology;
import vocab.UniverityOntology;

import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.*;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.DataProperty;

public class OntologyFactoryTest {

    public static OWLOntology ontologyExample1 (){
        OWLOntologyManager owlOntologyManager = OWLManager.createOWLOntologyManager();

        IRI o1 = IRI.create("http://ghxiao.org/inst/o1");
        IRI o2 = IRI.create("http://ghxiao.org/inst/o2");
        IRI o3 = IRI.create("http://ghxiao.org/inst/o3");

        IRI a = IRI.create("http://ghxiao.org/onto/A");
        IRI a1 = IRI.create("http://ghxiao.org/onto/A1");
        IRI a2 = IRI.create("http://ghxiao.org/onto/A2");
        IRI a3 = IRI.create("http://ghxiao.org/onto/A3");
        IRI a4 = IRI.create("http://ghxiao.org/onto/A4");
        IRI b = IRI.create("http://ghxiao.org/onto/B");
        IRI c = IRI.create("http://ghxiao.org/onto/C");
        IRI d = IRI.create("http://ghxiao.org/onto/D");
        IRI r = IRI.create("http://ghxiao.org/onto/r");
        IRI r1 = IRI.create("http://ghxiao.org/onto/r1");
        IRI r2 = IRI.create("http://ghxiao.org/onto/r2");
        IRI r3 = IRI.create("http://ghxiao.org/onto/r3");
        IRI r4 = IRI.create("http://ghxiao.org/onto/r4");

        OWL2Datatype intType = OWL2Datatype.getDatatype(OWL2Datatype.XSD_INTEGER);

        //OWLAxiom range = new OWLDataPropertyRangeAxiomImpl(DataProperty(r1), intType, null);

        OWLOntology ontology = Ontology(owlOntologyManager,
                // TBox
                SubObjectPropertyOf(ObjectProperty(r), ObjectInverseOf(ObjectProperty(r))),
                TransitiveObjectProperty(ObjectProperty(r)),

                //new OWLDataPropertyRangeAxiomImpl(DataProperty(r1),intType ),
                SubClassOf(Class(a), ObjectSomeValuesFrom(ObjectProperty(r), Class(b))),
                SubClassOf(Class(b), ObjectSomeValuesFrom(ObjectProperty(r), Class(c))),
                SubClassOf(Class(c), Class(d)),
                // ABox
                ClassAssertion(Class(a), NamedIndividual(o1))
        );

        return ontology;
    }


    public static OWLOntology ontologyExample2Teaches (){
        OWLOntologyManager owlOntologyManager = OWLManager.createOWLOntologyManager();
        // ABOX
//        IRI o1 = IRI.create("http://ghxiao.org/inst/o1");
//        IRI o2 = IRI.create("http://ghxiao.org/inst/o2");
//        IRI o3 = IRI.create("http://ghxiao.org/inst/o3");

        // TBOX
//        IRI Professor = IRI.create(BASE+"onto/Professor");
//        IRI Course = IRI.create(BASE+"onto/Course");
//        IRI teaches = IRI.create(BASE+"onto/teaches");

        //OWLAxiom range = new OWLDataPropertyRangeAxiomImpl(DataProperty(r1), intType, null);

        OWLOntology ontology = Ontology(owlOntologyManager,
                // TBox
                // Professor subclassOf teaches (x, _)
                SubClassOf(Class(UniverityOntology.Professor), ObjectSomeValuesFrom(ObjectProperty(UniverityOntology.teaches), Class(OWLRDFVocabulary.OWL_THING.getIRI()))),
                //∃teaches− v Course
                SubClassOf(ObjectSomeValuesFrom(ObjectInverseOf(ObjectProperty(UniverityOntology.teaches)), Class(OWLRDFVocabulary.OWL_THING.getIRI())), Class(UniverityOntology.Course))
        );
        return ontology;
    }


    public static OWLOntology ontologyBasicSummation (){
        OWLOntologyManager owlOntologyManager = OWLManager.createOWLOntologyManager();


        //OWLAxiom range = new OWLDataPropertyRangeAxiomImpl(DataProperty(r1), intType, null);

        OWLOntology ontology = Ontology(owlOntologyManager,
                //EQBox
                OWLFunctionalSyntaxFactoryEq.DefinedByEquation(DataProperty(GenericOntology.u1), new EquationExpression("<"+GenericOntology.u2.getIRIString()+"> + <"+GenericOntology.u3.getIRIString()+">"))
                // TBox

                , SubDataPropertyOf(DataProperty(GenericOntology.u1), DataProperty(GenericOntology.u2) )

                // ex:u1 defined by equation "ex:u2 + ex:u3"
                // Professor subclassOf teaches (x, _)
                //SubClassOf(Class(UniverityOntology.Professor), ObjectSomeValuesFrom(ObjectProperty(UniverityOntology.teaches), Class(OWLRDFVocabulary.OWL_THING.getIRI()))),
                //∃teaches− v Course
                //SubClassOf(ObjectSomeValuesFrom(ObjectInverseOf(ObjectProperty(UniverityOntology.teaches)), Class(OWLRDFVocabulary.OWL_THING.getIRI())), Class(UniverityOntology.Course))
        );
        return ontology;
    }
}
