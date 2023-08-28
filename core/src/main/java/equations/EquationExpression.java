package equations;

import org.semanticweb.clipper.hornshiq.rule.Variable;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.*;

public class EquationExpression {

    final static String REGEX_SYMBOLS = "<(\\S*)>";

    String equationStr = "";

    public List<OWLDataPropertyExpression> getPropertyList() {
        return propertyList;
    }

    public String getEquationStr() {
        return equationStr;
    }

    List<OWLDataPropertyExpression> propertyList = null;

    public EquationExpression(String equation){
        this.equationStr= equation;
        compileEquation();
    }
    private void compileEquation(){
        // " <qdqsd> + <sfsfsgfd>
        List<String> allMatches = new ArrayList<String>();
        Matcher m = Pattern.compile(REGEX_SYMBOLS)
                .matcher(this.equationStr);
        while (m.find()) {
            allMatches.add(m.group(1));
        }

        this.propertyList = allMatches.stream()
                .map(x-> DataProperty(IRI.create(x))).collect(Collectors.toList());
    }

    public String replaceDataProByVars(HashMap<IRI, Variable> map) {

        String templateDoubleVar = "xsd:double(?%s)";
        String equation = this.equationStr;//not a reference
        for(IRI key: map.keySet()){
            Variable v = map.get(key);
            equation=equation.replace("<"+key.getIRIString()+">", String.format(templateDoubleVar, v.getName()));
        }

        return equation;
    }

}
