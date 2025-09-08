package uk.gov.moj.cpp.accesscontrol.test.util;

public class Permission {

    private String object;
    private String action;
    private String source;
    private String target;

    public Permission(String object, String action, String source, String target) {
        this.object = object;
        this.action = action;
        this.source = source;
        this.target = target;
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

    public static Permission.Builder builder() {
        return new Permission.Builder();
    }

    public static class Builder {

        private String object;

        private String action;

        private String source;

        private String target;

        public Permission.Builder withObject(final String object) {
            this.object = object;
            return this;
        }

        public Permission.Builder withAction(final String action) {
            this.action = action;
            return this;
        }
        public Permission.Builder withSource(final String source) {
            this.source = source;
            return this;
        }
        public Permission.Builder withTarget(final String target) {
            this.target = target;
            return this;
        }

        public Permission build() {
            return new Permission(object, action, source, target);
        }
    }

}
