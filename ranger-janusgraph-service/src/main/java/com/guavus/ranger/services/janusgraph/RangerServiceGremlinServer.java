package com.guavus.ranger.services.janusgraph;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ranger.plugin.model.RangerService;
import org.apache.ranger.plugin.model.RangerServiceDef;
import org.apache.ranger.plugin.service.RangerBaseService;
import org.apache.ranger.plugin.service.ResourceLookupContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RangerServiceGremlinServer extends RangerBaseService {
  private static final Log LOG = LogFactory.getLog(RangerServiceGremlinServer.class);


  @Override
  public void init(RangerServiceDef serviceDef, RangerService service) {
    super.init(serviceDef, service);
  }

  @Override
  public Map<String, Object> validateConfig() throws Exception {
    Map<String, Object> ret = new HashMap<String, Object>();

    String serviceName = getServiceName();

    if(LOG.isDebugEnabled()) {
      LOG.debug("==> RangerServiceGremlinServer.validateConfig() Service: (" + serviceName + " )");
    }

    return ret;
  }

  @Override
  public List<String> lookupResource(ResourceLookupContext resourceLookupContext) throws Exception {

    List<String> ret = null;

    if (LOG.isDebugEnabled()) {
      LOG.debug("==> RangerServiceJanusgraph.lookupResource(" + serviceName + ")");
    }

    return ret;
  }
}
