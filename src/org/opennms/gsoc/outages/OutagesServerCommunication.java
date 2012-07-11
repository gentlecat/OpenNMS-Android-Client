package org.opennms.gsoc.outages;

import java.util.ArrayList;

import org.opennms.gsoc.model.OnmsOutage;

public interface OutagesServerCommunication {

	ArrayList<OnmsOutage> getOutages(String url);

}
