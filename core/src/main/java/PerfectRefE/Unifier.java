package PerfectRefE;

import PerfectRefE.FOFormula;
import org.semanticweb.clipper.hornshiq.rule.Atom;
import org.semanticweb.clipper.hornshiq.rule.Term;
import org.semanticweb.clipper.hornshiq.rule.Variable;

import java.util.LinkedList;
import java.util.List;

public class Unifier {

    public static boolean unifiable(Atom g1, Atom g2) {

        if (g1.isConcept() && g2.isRole()) return false;
        if (g1.isRole() && g2.isConcept()) return false;

        //different concepts
        if (g1.isConcept() && g2.isConcept())
            if (!g1.getPredicate().equals(g2.getPredicate())) return false;

        //different roles
        if (g1.isRole() && g2.isRole())
            if (!g1.getPredicate().equals(g2.getPredicate())) return false;


        //same concepts - different bound variables
        if (g1.isConcept() && g2.isConcept())
            if (
                //g1 bound g2 bound
                    g1.getTerm1().asVariable().isBound() && g2.getTerm1().asVariable().isBound() &&
                    !g1.equals(g2)
            ) return false;


        //same roles - different bound variables
        if (g1.isRole() && g2.isRole()){
            if (
                //g1 bound g2 bound
                    g1.getTerm1().asVariable().isBound() && g1.getTerm2().asVariable().isBound() &&
                            g2.getTerm1().asVariable().isBound() && g2.getTerm2().asVariable().isBound() &&

                            !(g1.getTerm1().equals(g2.getTerm1()) && g1.getTerm2().equals(g2.getTerm2())))

                return false;

            if(!varUnifiable(g1.getTerm1(), g2.getTerm1()) || !varUnifiable(g1.getTerm2(), g2.getTerm2())){
                return  false;
            }
        }


        return true;
    }

    public static boolean varUnifiable(Term v1, Term v2){

        if(v1.asVariable().isBound() && v2.asVariable().isBound() && !(v1.equals(v2))){
            return false;
        }
        return true;
    }

    public static Atom unifyAtoms(Atom g1, Atom g2){
        if(g1.isRole()&& g2.isRole()){
            return UnifyRoles( g1, g2);
        }
        if(g1.isConcept()&& g2.isConcept()){
            return UnifyConcepts( g1, g2);
        }
        return null;
    }

    public static Variable unifyVars(Variable v1, Variable v2){

        if(v1.isBound() && v2.isBound() && v1.equals(v2)) return  v1;
        if(v1.isBound() && !v2.isBound() ) return  v1;
        if(!v1.isBound() && v2.isBound() ) return  v2;
        if(!v1.isBound() && !v2.isBound()) {
            Variable v3 = Variable.createNewVariable();
            return v3;
        }

        return null;
    }

    public static FOFormula reduce(FOFormula f, Atom g1, Atom g2) throws CloneNotSupportedException {
        Atom g3 = unifyAtoms(g1, g2);

        List<Atom> formulaAtoms = new LinkedList<>();

        FOFormula copyFormula = FOFormula.deepCopyFOFormula(f);

        copyFormula.getAtoms().remove(g1);
        copyFormula.getAtoms().remove(g2);
        //formulaAtoms.remove(g2);

        copyFormula.getAtoms().add(g3);

        copyFormula.computeVars();
        //formulaAtoms.add(g3);




        return copyFormula;
    }

    private static Atom UnifyConcepts(Atom g1, Atom g2) {
        return  new Atom(g1.getPredicate(),
                unifyVars(g1.getTerm1().asVariable(), g2.getTerm1().asVariable()));

    }

    private static Atom UnifyRoles(Atom g1, Atom g2) {
        return  new Atom(g1.getPredicate(),
                unifyVars(g1.getTerm1().asVariable(), g2.getTerm1().asVariable()),
                unifyVars(g1.getTerm2().asVariable(), g2.getTerm2().asVariable()) );
    }


}
