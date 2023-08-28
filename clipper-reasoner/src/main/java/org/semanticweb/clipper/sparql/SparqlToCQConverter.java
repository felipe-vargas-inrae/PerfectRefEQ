package org.semanticweb.clipper.sparql;

import com.google.common.collect.Lists;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.semanticweb.clipper.hornshiq.rule.Atom;
import org.semanticweb.clipper.hornshiq.rule.CQ;
import org.semanticweb.clipper.hornshiq.rule.Constant;
import org.semanticweb.clipper.hornshiq.rule.DLPredicate;
import org.semanticweb.clipper.hornshiq.rule.NonDLPredicate;
import org.semanticweb.clipper.hornshiq.rule.Term;
import org.semanticweb.clipper.hornshiq.rule.Variable;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.util.*;

public class SparqlToCQConverter {
	HashMap <String, Variable> queryVarsMap = new HashMap<>();

	/**
	 * @param query
	 */
	public CQ compileQuery(Query query) {

		List<Atom> body = new ArrayList<>();
		List<Term> ansVars = new ArrayList<>();
		List<Var> projectVars = query.getProjectVars();



		for (Var var : projectVars) {
			ansVars.add(compileVarHead(var));
		}

		NonDLPredicate ans = new NonDLPredicate("ans", ansVars.size());

		// NormalPredicate ans = CacheManager.getInstance().getPredicate("ans",
		// ansVars.size());

		Atom head = new Atom(ans, ansVars);

		Element queryPattern = query.getQueryPattern();

		if (queryPattern instanceof ElementGroup) {
			ElementGroup group = (ElementGroup) queryPattern;
			List<Element> elements = group.getElements();
			for (Element ele : elements) {
				// System.out.println(ele.getClass());
				if (ele instanceof ElementPathBlock) {
					ElementPathBlock block = (ElementPathBlock) ele;
					Iterator<TriplePath> patternElts = block.patternElts();
					while (patternElts.hasNext()) {
						TriplePath triplePath = patternElts.next();
						Triple triple = triplePath.asTriple();
						final Atom lit = complileTriple(triple);
						// System.out.println(triple + " ==> " + lit);
						body.add(lit);
					}
					return new CQ(head, body);
				} else {
					throw new UnsupportedOperationException();
				}

			}

		} else {
			throw new UnsupportedOperationException();
		}
		return null;
	}

	private Atom complileTriple(Triple triple) {

		if (triple.getPredicate().getURI()
				.equals(OWLRDFVocabulary.RDF_TYPE.toString())) {
			return new Atom(compileUnaryPredicate(triple.getObject()), //
					compileTerm(triple.getSubject()));
		} else {
			return new Atom(compileBinaryPredicate(triple.getPredicate()), //
					compileTerm(triple.getSubject()), //
					compileTerm(triple.getObject()));
		}

	}

	private DLPredicate compileUnaryPredicate(Node predicate) {

		if (predicate.isURI()) {
			String uri = predicate.getURI();
			
			return new DLPredicate(OWLManager.getOWLDataFactory()
					.getOWLClass(IRI.create(uri)));
		} else {
			throw new IllegalArgumentException();
		}
	}


	private DLPredicate compileBinaryPredicate(Node predicate) {

		if (predicate.isURI()) {
			String uri = predicate.getURI();
			return new DLPredicate(OWLManager.getOWLDataFactory()
					.getOWLObjectProperty(IRI.create(uri)));
		} else {
			throw new IllegalArgumentException();
		}
	}

	private Term compileTerm(Node node) {
		if (node.isURI()) {
			String uri = node.getURI();
			return new Constant(quote(uri));
		} else if (node.isLiteral()) {
			String uri = node.getLiteralDatatypeURI();
			if (uri != null)
				return new Constant(quote(uri));
			else
				return new Constant(node.toString());
		} else if (node.isVariable()) {
			String name = node.getName();

            // in Datalog/DLV, variable names are capitalized
            String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1);

			//Variable varCQ = new Variable(capitalizedName);

			if(this.queryVarsMap.containsKey(capitalizedName)){

				Variable varCQ= this.queryVarsMap.get(capitalizedName);
				if(!varCQ.isDistinguished()){
					varCQ.setShared(true); // is at least twice in the body
				}

				return varCQ;
			}
			else{
				Variable varCQ= new Variable(capitalizedName);
				this.queryVarsMap.put(capitalizedName, varCQ);
				return varCQ;
			}

		} else {
			throw new IllegalArgumentException(node.toString());
		}
	}

	private Variable compileVarHead(Var node) {
		String name = node.getName();
		// in Datalog/DLV, variable names are capitalized
		String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1);

		Variable varCQ = new Variable(capitalizedName);
		varCQ.setDistinguished(true);// is in the header
		this.queryVarsMap.put(capitalizedName, varCQ);
		return varCQ;
	}


	private String quote(String uri) {
		final String quotedIRI = "<" + uri + ">";
		return quotedIRI;
	}

}
