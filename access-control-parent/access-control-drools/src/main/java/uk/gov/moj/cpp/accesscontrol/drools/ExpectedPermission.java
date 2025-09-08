package uk.gov.moj.cpp.accesscontrol.drools;


import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;


public class ExpectedPermission {
    private String object;
    private String action;
    private String  source;
    private String  target;

    public ExpectedPermission() {

    }

    public ExpectedPermission(final String object , final String action, final String source, final String target) {
        this.object = object;
        this.action = action;
        this.source = source;
        this.target = target;
    }

    public ExpectedPermission(final String object , final String action) {
        this.object = object;
        this.action = action;

    }

   public String getKey() {
        final StringBuffer stringBuffer = new StringBuffer("");
        final String strObject = getObject();
        final String strAction = getAction();
        final String strSource = getSource();
        final String strTarget = getTarget();
        if(isNotEmpty(strObject)) {
            stringBuffer.append(strObject).append("_");
        }
        if(isNotEmpty(strAction)) {
            stringBuffer.append(strAction).append("_");
        }
       if(isNotEmpty(strSource)) {
           stringBuffer.append(strSource).append("_");
       }
       if(isNotEmpty(strTarget)) {
           stringBuffer.append(strTarget);
       }
       return StringUtils.stripEnd(stringBuffer.toString(), "_");

   }

    public String getKeyWithOutSource() {
        final StringBuffer stringBuffer = new StringBuffer("");
        final String strObject = getObject();
        final String strAction = getAction();
        final String strTarget = getTarget();
        if(isNotEmpty(strObject)) {
            stringBuffer.append(strObject).append("_");
        }
        if(isNotEmpty(strAction)) {
            stringBuffer.append(strAction).append("_");
        }
        if(isNotEmpty(strTarget)) {
            stringBuffer.append(strTarget);
        }
        return StringUtils.stripEnd(stringBuffer.toString(), "_");

    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpectedPermission that = (ExpectedPermission) o;
        return Objects.equals(object, that.object) &&
                Objects.equals(action, that.action) &&
                Objects.equals(source, that.source) &&
                Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(object, action, source, target);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String object;

        private String action;

        private String source;

        private String target;

        public Builder withObject(final String object) {
            this.object = object;
            return this;
        }

        public Builder withAction(final String action) {
            this.action = action;
            return this;
        }
        public Builder withSource(final String source) {
            this.source = source;
            return this;
        }
        public Builder withTarget(final String target) {
            this.target = target;
            return this;
        }

        public ExpectedPermission build() {
            return new ExpectedPermission(object, action, source, target);
        }
    }
}
