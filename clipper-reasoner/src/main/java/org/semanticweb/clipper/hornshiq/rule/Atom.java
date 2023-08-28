package org.semanticweb.clipper.hornshiq.rule;

import gnu.trove.list.linked.TLinkedList;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Atom implements Cloneable{

	Predicate predicate;

	List<Term> terms;

	public Atom(Predicate predicate, Term... terms) {
		this(predicate, Arrays.asList(terms));
	}

	public Atom(Predicate predicate, List<Term> terms) {
		this.predicate = predicate;
		this.terms = terms;
	}

	public Atom() {

		this.terms = Collections.emptyList();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(predicate);
		sb.append("(");
		boolean first = true;
		for (Term t : terms) {
			if (!first) {
				sb.append(",");
			}
			first = false;
			sb.append(t);
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public Atom clone() throws CloneNotSupportedException {

		List<Term> newTerms = terms.stream().map(x-> {
			try {
				if (x.isVariable()) {
					return x.asVariable().clone();
				} else if (x.isConstant()) return x.asConstant().clone();

			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			return x;
		}).collect(Collectors.toList());
//		List <Term> newTerms = terms.stream().map(x->x.clone());
		return new Atom(predicate, new LinkedList<>(newTerms));
	}

	public Predicate getPredicate() {
		return this.predicate;
	}

	public List<Term> getTerms() {
		return this.terms;
	}

	public void setPredicate(Predicate predicate) {
		this.predicate = predicate;
	}

	public void setTerms(List<Term> terms) {
		this.terms = terms;
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Atom)) return false;
		final Atom other = (Atom) o;
		if (!other.canEqual(this)) return false;
		final Object this$predicate = this.predicate;
		final Object other$predicate = other.predicate;
		if (this$predicate == null ? other$predicate != null : !this$predicate.equals(other$predicate)) return false;
		final Object this$terms = this.terms;
		final Object other$terms = other.terms;
		return !(this$terms == null ? other$terms != null : !this$terms.equals(other$terms));
	}

	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final Object $predicate = this.predicate;
		result = result * PRIME + ($predicate == null ? 0 : $predicate.hashCode());
		final Object $terms = this.terms;
		result = result * PRIME + ($terms == null ? 0 : $terms.hashCode());
		return result;
	}

	protected boolean canEqual(Object other) {
		return other instanceof Atom;
	}

    public int getArity() {
        return terms.size();
    }

    public Term getTerm(int i) {
        return terms.get(i);
    }

	public Term getTerm1() {
		return terms.get(0);
	}

	public Term getTerm2() {// only valid for roles
		return terms.get(1);
	}

    public boolean isConcept(){
		return getArity()==1 && predicate.isDLPredicate();
	}
	public boolean isRole(){
		return getArity()==2 && predicate.isDLPredicate();
	}




	public static Atom createClass(String classIRI, Term var){
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLClass owlClass = manager.getOWLDataFactory().getOWLClass(classIRI);
		DLPredicate p1 = new DLPredicate(owlClass);
		return new Atom(p1, var);
	}

	public static Atom createRole(String iri, Term var1, Term var2){
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataProperty owlProperty = manager.getOWLDataFactory().getOWLDataProperty(iri);
		DLPredicate p1 = new DLPredicate(owlProperty);
		return new Atom(p1, var1, var2);
	}

	public static Atom createDefaultHeader(){
		NonDLPredicate p1 = new NonDLPredicate("q");
		return  new Atom(p1, new Variable("X"));
	}

	public static Atom createBINDAtom(String expression, Variable asVar){
		NonDLPredicate p1 = new NonDLPredicate("BIND");
		return  new Atom(p1, new Constant(expression), asVar);
	}
}
