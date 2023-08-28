package org.semanticweb.clipper.hornshiq.rule;

public interface Term extends Cloneable {
	public boolean isVariable();
	
	public boolean isConstant();

	public int getIndex();

	public String getName();

	public void setName(String name);

	public Variable asVariable();

	public Constant asConstant();

	public String toSQL();
}
