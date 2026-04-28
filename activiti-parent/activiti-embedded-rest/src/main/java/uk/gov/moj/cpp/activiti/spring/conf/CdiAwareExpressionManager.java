package uk.gov.moj.cpp.activiti.spring.conf;

import org.activiti.engine.delegate.VariableScope;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.el.VariableScopeElResolver;
import org.activiti.engine.impl.javax.el.ArrayELResolver;
import org.activiti.engine.impl.javax.el.BeanELResolver;
import org.activiti.engine.impl.javax.el.CompositeELResolver;
import org.activiti.engine.impl.javax.el.DynamicBeanPropertyELResolver;
import org.activiti.engine.impl.javax.el.ELResolver;
import org.activiti.engine.impl.javax.el.JsonNodeELResolver;
import org.activiti.engine.impl.javax.el.ListELResolver;
import org.activiti.engine.impl.javax.el.MapELResolver;
import org.activiti.engine.impl.bpmn.data.ItemInstance;

/**
 * ExpressionManager that resolves CDI beans in Activiti BPMN expressions.
 *
 * Overrides createElResolver to insert a CdiAwareELResolver into the chain, allowing
 * delegateExpression values like ${uploadFile} to resolve CDI beans by name.
 * Uses only bundled org.activiti.engine.impl.javax.el classes to avoid the
 * javax.el dependency absent from WildFly 32 / Jakarta EE 10.
 */
public class CdiAwareExpressionManager extends ExpressionManager {

    @Override
    protected ELResolver createElResolver(final VariableScope variableScope) {
        final CompositeELResolver composite = new CompositeELResolver();
        composite.add(new VariableScopeElResolver(variableScope));
        composite.add(new CdiAwareELResolver());
        if (getBeans() != null) {
            composite.add(new org.activiti.engine.impl.el.ReadOnlyMapELResolver(getBeans()));
        }
        composite.add(new ArrayELResolver());
        composite.add(new ListELResolver());
        composite.add(new MapELResolver());
        composite.add(new JsonNodeELResolver());
        composite.add(new DynamicBeanPropertyELResolver(ItemInstance.class, "getFieldValue", "setFieldValue"));
        composite.add(new BeanELResolver());
        return composite;
    }
}
