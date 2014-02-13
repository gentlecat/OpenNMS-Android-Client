package org.opennms.android.data.api;

import org.opennms.android.data.api.model.Alarm;
import org.opennms.android.data.api.model.Alarms;
import org.opennms.android.data.api.model.Event;
import org.opennms.android.data.api.model.Events;
import org.opennms.android.data.api.model.Node;
import org.opennms.android.data.api.model.Nodes;
import org.opennms.android.data.api.model.Outage;
import org.opennms.android.data.api.model.Outages;
import org.opennms.android.data.api.model.User;

import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

public interface ServerInterface {

  @GET("/nodes?orderBy=id")
  Nodes nodes(@Query("limit") int limit, @Query("offset") int offset);

  @GET("/nodes?comparator=ilike")
  Nodes nodesSearch(@Query("limit") int limit, @Query("offset") int offset,
                    @Query("label") String searchQuery);

  @GET("/nodes/{id}")
  Node node(@Path("id") long id);

  @GET("/events?orderBy=id&order=desc")
  Events events(@Query("limit") int limit, @Query("offset") int offset);

  @GET("/events/{id}")
  Event event(@Path("id") long id);

  @GET("/alarms")
  Alarms alarms(@Query("limit") int limit, @Query("offset") int offset);

  @PUT("/alarms/{id}")
  Alarm alarmSetAck(@Path("id") long id, @Query("ack") boolean isAcked);

  @GET("/alarms?orderBy=id&order=desc&limit=0")
  Alarms alarmsAll();

  @GET("/alarms/{id}")
  Alarm alarm(@Path("id") long id);

  // TODO: Fix
  @GET("/alarms/?query=nodeLabel='{label}'")
  Alarms alarmsRelatedToNode(@Path("label") String nodeLabel);

  @GET("/outages?orderBy=id&order=desc")
  Outages outages(@Query("limit") int limit, @Query("offset") int offset);

  @GET("/outages/{id}")
  Outage outage(@Path("id") long id);

  @GET("/outages/forNode/{nodeId}")
  Outages outagesRelatedToNode(@Path("nodeId") long nodeId);

  @GET("/users")
  User user(@Query("name") String name);
}
