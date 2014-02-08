package org.opennms.android;

final class Modules {
    static Object[] list(App app) {
        return new Object[]{
                new AppModule(app)
        };
    }

    private Modules() {
        // No instances
    }
}
