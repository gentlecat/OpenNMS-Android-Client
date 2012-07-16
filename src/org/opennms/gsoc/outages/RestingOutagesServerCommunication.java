package org.opennms.gsoc.outages;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.opennms.gsoc.ServerConfiguration;

import android.util.Base64;

import com.google.resting.Resting;
import com.google.resting.component.EncodingTypes;
import com.google.resting.component.impl.ServiceResponse;

public class RestingOutagesServerCommunication implements Callable<ServiceResponse>{
	private ServerConfiguration serverConfiguration = ServerConfiguration.getInstance();
	
	@Override
	public ServiceResponse call() throws Exception {
		String auth = new String(Base64.encode(
				(serverConfiguration.getUsername() + ":" + serverConfiguration.getPassword()).getBytes(), Base64.URL_SAFE
						| Base64.NO_WRAP));
		Header httpHeader = new BasicHeader("Authorization", "Basic " + auth);
		List<Header> headers = new ArrayList<Header>();
		headers.add(httpHeader);
		ServiceResponse response=Resting.get(serverConfiguration.getBase() + "/outages", 80, null, EncodingTypes.UTF8, headers);
		return response;
	}
}
