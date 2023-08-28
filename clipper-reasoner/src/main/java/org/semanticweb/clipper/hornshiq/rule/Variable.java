package org.semanticweb.clipper.hornshiq.rule;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.jena.sparql.core.Var;

public class Variable implements Term {

	int index;
	String name;

	boolean distinguished = false;
	boolean shared = false;

	public void setDistinguished(boolean b){
		distinguished = b;
	}
	public void setShared(boolean b){
		shared = b;
	}

	public boolean isDistinguished(){
		return distinguished;
	}

	public boolean isShared(){
		return shared;
	}

	public boolean isBound(){
		return shared || distinguished;
	}

	public boolean isUnbound(){
		return !(isBound()); // not bound
	}

	public Variable(int index) {
		this.index = index;
	}

	public Variable(String text) {
		this.name = text;
	}

	@Override
	public boolean isVariable() {
		return true;
	}

	@Override
	public String toString() {

		if (name != null)
			return name;
        // TODO: check, this may cause name collision
        return "X" + index;
		//return "X" + index;
	}

	@Override
	public Variable asVariable() {
		return this;
	}

	@Override
	public Constant asConstant() {
		throw new IllegalArgumentException("not a constant!");
	}

	@Override
	public String toSQL() {
		String templateVar = "?%s";
		return String.format(templateVar, name) ;
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	public int getIndex() {
		return this.index;
	}

	public String getName() {
		return this.name;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Variable)) return false;
		final Variable other = (Variable) o;
		if (!other.canEqual((Object) this)) return false;
		if (this.index != other.index) return false;
		final Object this$name = this.name;
		final Object other$name = other.name;
        return !(this$name == null ? other$name != null : !this$name.equals(other$name));
    }

	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		result = result * PRIME + this.index;
		final Object $name = this.name;
		result = result * PRIME + ($name == null ? 0 : $name.hashCode());
		return result;
	}

	protected boolean canEqual(Object other) {
		return other instanceof Variable;
	}

	@Override
	protected Variable clone() throws CloneNotSupportedException {

		Variable v = new Variable(this.getName());
		v.setShared(this.shared);
		v.setDistinguished(this.distinguished);
		return v;
	}

	public static Variable createNewVariable(){
		String name = RandomStringUtils.random(3, true, true);
		return new Variable("z_"+name);
	}
}
