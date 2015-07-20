package org.opennms.android.data.api;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.RequestInterceptor;

@Singleton
public final class ApiHeaders implements RequestInterceptor {

    @Inject
    public ApiHeaders() {}

    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("Accept", "application/json");
    }
}
