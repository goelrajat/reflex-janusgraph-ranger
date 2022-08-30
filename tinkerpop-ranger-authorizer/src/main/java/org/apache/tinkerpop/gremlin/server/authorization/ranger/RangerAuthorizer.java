package org.apache.tinkerpop.gremlin.server.authorization.ranger;

import org.apache.ranger.authorization.hadoop.config.RangerConfiguration;
import org.apache.ranger.plugin.audit.RangerDefaultAuditHandler;
import org.apache.ranger.plugin.policyengine.RangerAccessRequest;
import org.apache.ranger.plugin.policyengine.RangerAccessRequestImpl;
import org.apache.ranger.plugin.policyengine.RangerAccessResourceImpl;
import org.apache.ranger.plugin.policyengine.RangerAccessResult;
import org.apache.ranger.plugin.service.RangerBasePlugin;

import org.apache.tinkerpop.gremlin.server.authorization.AuthorizationException;
import org.apache.tinkerpop.gremlin.server.authorization.AuthorizationRequest;
import org.apache.tinkerpop.gremlin.server.authorization.Authorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

public class RangerAuthorizer implements Authorizer {
  private static final Logger logger = LoggerFactory.getLogger(RangerAuthorizer.class);
  public static final String SVC_TYPE_KEY = "svcType";
  public static final String APP_ID_KEY = "appId";

  private Map<String, Object> config;
  private static volatile RangerBasePlugin rangerPlugin = null;
  private String rangerUrl = null;

  @Override
  public boolean requireAuthorization() {
    return false;
  }

  @Override
  public void setup(Map<String, Object> config) {
    this.config = config;
    configureRangerPlugin();
  }

  @Override
  public boolean isAccessAllowed(AuthorizationRequest authorizationRequest) throws AuthorizationException {

    logger.debug("authorizationRequest:: {}", authorizationRequest);

    RangerAccessRequestImpl rangerRequest = new RangerAccessRequestImpl();
    rangerRequest.setUser(authorizationRequest.getUser());
    if(authorizationRequest.getUserGroup() !=null) {
      rangerRequest.setUserGroups(new HashSet<>(Arrays.asList(authorizationRequest.getUserGroup().split(","))));
    }
    rangerRequest.setClientIPAddress(authorizationRequest.getClientIPAddress());
    rangerRequest.setResourceMatchingScope(RangerAccessRequest.ResourceMatchingScope.SELF_OR_DESCENDANTS);
    rangerRequest.setAccessTime(authorizationRequest.getAccessTime());
    rangerRequest.setAccessType(authorizationRequest.getAccessType().name());
    rangerRequest.setAction(authorizationRequest.getAction());

    RangerAccessResourceImpl rangerResource = new RangerAccessResourceImpl();
    rangerResource.setValue("graph-name", authorizationRequest.getResource());
    rangerRequest.setResource(rangerResource);

    logger.debug("rangerRequest:: {}", rangerRequest);

    RangerAccessResult result = rangerPlugin.isAccessAllowed(rangerRequest);
    if (result == null || !(result.getIsAllowed())) {
      logger.warn("permission denied:: {}, request:: {}", result, rangerRequest);
      return false;
    }
    logger.debug("permission granted :: {}", result);

    return true;
  }

  private void configureRangerPlugin() {
    String svcType = "janusgraphs";
    String appId = "janusgraphs";

    if (config!=null && config.get(SVC_TYPE_KEY) !=null)
      svcType = (String) config.get(SVC_TYPE_KEY);

    if (config!=null && config.get(APP_ID_KEY) !=null)
      appId = (String) config.get(APP_ID_KEY);

    RangerBasePlugin me = rangerPlugin;
    if (me == null) {
      synchronized(RangerAuthorizer.class) {
        me = rangerPlugin;
        if (me == null) {
          me = rangerPlugin = new RangerBasePlugin(svcType, appId);
        }
      }
    }
    logger.debug("Calling ranger plugin init");
    rangerPlugin.init();

    this.rangerUrl = (String) RangerConfiguration.getInstance().get("ranger.plugin.janusgraph.policy.rest.url");
    logger.debug("Ranger uri : {}" , rangerUrl);
    RangerDefaultAuditHandler auditHandler = new RangerDefaultAuditHandler();
    rangerPlugin.setResultProcessor(auditHandler);
  }

}
