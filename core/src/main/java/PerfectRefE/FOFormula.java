package PerfectRefE;

import org.apache.jena.vocabulary.RDF;
import org.semanticweb.clipper.hornshiq.rule.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import utils.OntologyUtils;
import vocab.SPARQL;

import java.util.*;
import java.util.stream.Collectors;

public class FOFormula{
    private final Atom head;
    private boolean processed = false;
    private List<Atom> atoms = null;

    public List<Atom> getAtoms(){
        return  atoms;
    }
    public Atom getHead(){return  head;}
    public void setProcessed(boolean p){
        processed=p;
    }

    public boolean isProcessed(){
        return processed;
    }

    public FOFormula(Atom head, List<Atom> atoms) {
        this.atoms = atoms;
        this.head = head;
    }

    @Override
    public String toString() {
        return "PerfectRefE.FOFormula{" +
                "isProcessed=" + processed +
                ", atoms=" + atoms +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FOFormula foFormula = (FOFormula) o;
        return Objects.equals(atoms, foFormula.atoms);
    }


    /**
     * convert all to the variables to the aux variable in order to ignore variables during comparison
     * @param atoms1
     * @return
     */
    public List<Atom> unifyVars(List<Atom> atoms1){
        String varName = "w_yui";
        Variable auxVar = new Variable("w_yui");
        return atoms1.stream().map(atom -> {

            try {
                Atom atom2 = atom.clone();
                List<Term> terms = atom2.getTerms().stream().map(term -> term.isVariable()? auxVar: term).collect(Collectors.toList());
                atom2.setTerms(terms);

                if(SPARQL.isBIND(atom2)){
                    String equation = atom2.getTerm1().asConstant().getName();
                    equation = equation.replaceAll("\\?\\w*", auxVar.toSQL() );


                    atom2.getTerm1().setName(equation);
                }
                return atom2;
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

            return null;

        }).collect(Collectors.toList());
    }

    public boolean equalsIgnoreVars(FOFormula o) {
        List<Atom> atomsLocal = unifyVars(this.atoms);
        List<Atom> atomsExternal = unifyVars(o.atoms);
        return Objects.equals(atomsLocal, atomsExternal);
    }

    public boolean containedInListIgnoreVars(List<FOFormula> l1){
        boolean contained = l1.stream().anyMatch(x-> x.equalsIgnoreVars(this));
        return  contained;
    }

    @Override
    public int hashCode() {
        return Objects.hash(processed, atoms);
    }

    public boolean containedInList(List<FOFormula> l1){
        return  l1.indexOf(this)>-1;
    }



    public static void main (String [] args){

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
        OWLClass c1A = manager.getOWLDataFactory().getOWLClass("c1");
        OWLClass c1B = manager2.getOWLDataFactory().getOWLClass("c1");

        OWLClass c2A = manager.getOWLDataFactory().getOWLClass("c2");
        OWLClass c2B = manager2.getOWLDataFactory().getOWLClass("c2");

        OWLEntity dp1 = manager.getOWLDataFactory().getOWLDataProperty("dp1");
        OWLEntity dp2 = manager.getOWLDataFactory().getOWLDataProperty("dp2");

        DLPredicate p1 = new DLPredicate(dp1);
        DLPredicate p2 = new DLPredicate(dp2);

        DLPredicate pc1A = new DLPredicate(c1A);
        DLPredicate pc1B = new DLPredicate(c1B);

        DLPredicate pc2A = new DLPredicate(c2A);
        DLPredicate pc2B = new DLPredicate(c2B);

        System.out.println("equal DLPredicate c1A vs c1b="+pc1A.equals(pc1B));
        System.out.println("equal DLPredicate dataP1 vs dataP2="+p1.equals(p2));

        Variable v1 = new Variable("x");

        Atom ato1 =new Atom(pc1A, v1);
        Atom ato2 =new Atom(pc1B, v1);

        Atom ato3 =new Atom(pc2A, v1);
        Atom ato4 =new Atom(pc2B, v1);

        System.out.println("equal DLPredicate ato1 vs ato2="+ato1.equals(ato2));

        LinkedList<Atom> l1 = new LinkedList(Arrays.asList(new Atom[]{ato1}));
        LinkedList<Atom> l2 = new LinkedList(Arrays.asList(new Atom[]{ato2}));

        System.out.println("equal List l1 vs l2="+l1.equals(l2));

        l1 = new LinkedList(Arrays.asList(new Atom[]{ato1, ato3}));
        l2 = new LinkedList(Arrays.asList(new Atom[]{ato2, ato4}));

        System.out.println("equal order List l1 vs l2="+l1.equals(l2));

//        l1 = new LinkedList(Arrays.asList(new Atom[]{ato1, ato3}));
//        l2 = new LinkedList(Arrays.asList(new Atom[]{ato4, ato2}));
//
//        System.out.println("equal distinct order List l1 vs l2="+l1.equals(l2));


//        PerfectRefE.FOFormula f1 = new PerfectRefE.FOFormula(aCQ.getHead(), l1);
//        PerfectRefE.FOFormula f2 = new PerfectRefE.FOFormula(aCQ.getHead(), l2);

//        f1.setProcessed(true);// equality is about the atoms
//
//        System.out.println("equal distinct order List f1 vs f2="+f1.equals(f2));

    }

    public static FOFormula deepCopyFOFormula(FOFormula f1) throws CloneNotSupportedException {

        List atoms = f1.getAtoms().stream().map(x-> {
            try {
                return x.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
        List<Atom> clonedAtoms = new LinkedList<>(atoms);


        FOFormula formulaCopy = new FOFormula(f1.getHead().clone(), clonedAtoms);
        formulaCopy.setProcessed(f1.isProcessed());
        return  formulaCopy;
    }

    public void computeVars(){

        HashMap<String, Variable> varsMap = new HashMap<>();
        // body
        for (Atom at: atoms ){
            List <Term> terms2 = at.getTerms().stream().map(x->{
                if(!x.isVariable()) return x;
                else if(varsMap.containsKey(x.getName())){
                    Variable xBase = varsMap.get(x.getName());
                    xBase.setShared(true);
                    return xBase;
                }
                else {
                    x.asVariable().setDistinguished(false);
                    x.asVariable().setShared(false);
                    varsMap.put(x.getName(), x.asVariable());
                }
                return x;
            }).collect(Collectors.toList());

            at.setTerms(terms2);// new variables ref
        }
        //header
        List <Term> terms2 = this.getHead().getTerms().stream().map(t->{
            if(varsMap.containsKey(t.getName())){
                Variable x = varsMap.get(t.getName());
                x.setDistinguished(true);
                return x;
            }
            return t;// not relevant
        }).collect(Collectors.toList());

        this.head.setTerms(terms2);

    }


    public String toSQL(){

        //templates
        String templateTriple = "%s %s %s.";
        String templateBGP = "%s\n";
        String BINDTemplate = "BIND(%s AS %s)";
        String start = "{\n";
        String end = "}";

        StringBuilder bgp = new StringBuilder();
        //bgp.append(selectClause);
        bgp.append(start);

        for(Atom at:this.atoms){
            // at is a concept
            String triple ="";
            if(at.isConcept()){
                String subject = at.getTerm1().toSQL();
                IRI iriPredicate = ((DLPredicate) at.getPredicate()).getOwlEntity().getIRI();
                triple = String.format(templateTriple, subject , OntologyUtils.quoted(RDF.type.toString()), OntologyUtils.quoted(iriPredicate.getIRIString()) );
            }
            else if(at.isRole()){
                String subject = at.getTerm1().toSQL();
                String object = at.getTerm2().toSQL();
                IRI iriPredicate = ((DLPredicate) at.getPredicate()).getOwlEntity().getIRI();
                triple = String.format(templateTriple, subject,OntologyUtils.quoted(iriPredicate.getIRIString()), object );
            }
            else { // is a SPARQL predicate
                if(at.getPredicate().getName().equals(SPARQL.BIND)){
                    String equation = at.getTerm1().getName();
                    String var = at.getTerm2().toSQL();
                    triple = String.format(BINDTemplate, equation, var  );
                }
            }
            bgp.append(String.format(templateBGP, triple ));
        }
        bgp.append(end);

        return bgp.toString();
    }
}