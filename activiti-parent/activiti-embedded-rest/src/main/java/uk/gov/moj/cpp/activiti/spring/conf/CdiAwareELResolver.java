package uk.gov.moj.cpp.activiti.spring.conf;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import jakarta.enterprise.inject.spi.CDI;

import org.activiti.engine.impl.javax.el.ELContext;
import org.activiti.engine.impl.javax.el.ELResolver;

/**
 * EL resolver that looks up CDI beans by name using jakarta.enterprise.inject.spi.CDI.
 *
 * Activiti 5.x bundles its own javax.el implementation under org.activiti.engine.impl.javax.el,
 * so this resolver extends that bundled ELResolver rather than javax.el.ELResolver.
 * This avoids the javax.el dependency that is absent from WildFly 32 / Jakarta EE 10.
 *
 * When Activiti evaluates a delegateExpression like ${uploadFile}, it calls getValue with
 * base=null and property="uploadFile". This resolver looks that name up as a CDI bean.
 */
public class CdiAwareELResolver extends ELResolver {

    @Override
    public Object getValue(final ELContext context, final Object base, final Object property) {
        if (base != null || property == null) {
            return null;
        }
        final String beanName = property.toString();
        try {
            final Object bean = CDI.current().select(Object.class,
                    jakarta.enterprise.inject.literal.NamedLiteral.of(beanName)).get();
            context.setPropertyResolved(true);
            return bean;
        } catch (final Exception e) {
            return null;
        }
    }

    @Override
    public Class<?> getType(final ELContext context, final Object base, final Object property) {
        return null;
    }

    @Override
    public void setValue(final ELContext context, final Object base, final Object property, final Object value) {
        // read-only resolver
    }

    @Override
    public boolean isReadOnly(final ELContext context, final Object base, final Object property) {
        return true;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(final ELContext context, final Object base) {
        return null;
    }

    @Override
    public Class<?> getCommonPropertyType(final ELContext context, final Object base) {
        return Object.class;
    }
}
