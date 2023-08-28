package vocab;

import org.semanticweb.clipper.hornshiq.rule.Atom;
import org.semanticweb.clipper.hornshiq.rule.NonDLPredicate;
import org.semanticweb.clipper.hornshiq.rule.Predicate;

public class SPARQL {
    final public static String BIND = "BIND";


    public static boolean isBIND(Atom t){
        Predicate p = t.getPredicate();
        return t.getArity()==2 && !p.isDLPredicate() && ((NonDLPredicate) p).getName().equals(SPARQL.BIND);
    }
}
