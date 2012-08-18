package org.opennms.gsoc.alarms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.opennms.gsoc.model.OnmsAlarm;


public interface AlarmsServerCommunication {
	ArrayList<OnmsAlarm> getAlarms(String url) throws InterruptedException, ExecutionException, IOException;
}
