package org.opennms.android.communication.alarms;

import org.opennms.android.dao.alarms.Alarm;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface AlarmsServerCommunication {

    List<Alarm> getAlarms(String url) throws InterruptedException, ExecutionException, IOException;

}
