package com.mapsengineering.base.util;

import java.util.List;

import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionFunction;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityConditionValue;
import org.ofbiz.entity.condition.EntityConditionVisitor;
import org.ofbiz.entity.condition.EntityDateFilterCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFieldMap;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityWhereString;

public class RecursiveEntityConditionVisitor implements EntityConditionVisitor {
	protected boolean recurse = true;

	@Override
	public <T> void visit(T obj) {
	}

	@Override
	public <T> void accept(T obj) {
	}

	@Override
	public void acceptObject(Object obj) {
	}

	@Override
	public void acceptEntityCondition(EntityCondition condition) {
		if (recurse) {
			condition.visit(this);
		}
	}

	@Override
	public <T extends EntityCondition> void acceptEntityJoinOperator(EntityJoinOperator op, List<T> conditions) {
		if (recurse && conditions != null) {
			for (T cond : conditions) {
				if (recurse && cond != null) {
					cond.visit(this);
				}
			}
		}
	}

	@Override
	public <L, R, T> void acceptEntityOperator(EntityOperator<L, R, T> op, L lhs, R rhs) {
	}

	@Override
	public <L, R> void acceptEntityComparisonOperator(EntityComparisonOperator<L, R> op, L lhs, R rhs) {
	}

	@Override
	public void acceptEntityConditionValue(EntityConditionValue value) {
	}

	@Override
	public void acceptEntityFieldValue(EntityFieldValue value) {
	}

	@Override
	public void acceptEntityExpr(EntityExpr expr) {
	}

	@Override
	public <T extends EntityCondition> void acceptEntityConditionList(EntityConditionList<T> list) {
		if (recurse && list != null) {
			list.visit(this);
		}
	}

	@Override
	public void acceptEntityFieldMap(EntityFieldMap fieldMap) {
	}

	@Override
	public void acceptEntityConditionFunction(EntityConditionFunction func, EntityCondition nested) {
		if (recurse && nested != null) {
			nested.visit(this);
		}
	}

	@Override
	public <T extends Comparable<?>> void acceptEntityFunction(EntityFunction<T> func) {
	}

	@Override
	public void acceptEntityWhereString(EntityWhereString condition) {
	}

	@Override
	public void acceptEntityDateFilterCondition(EntityDateFilterCondition condition) {
	}
}
