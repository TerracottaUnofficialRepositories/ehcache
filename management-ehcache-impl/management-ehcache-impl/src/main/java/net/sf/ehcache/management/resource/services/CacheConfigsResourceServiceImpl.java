package net.sf.ehcache.management.resource.services;

import net.sf.ehcache.management.resource.CacheConfigEntity;
import net.sf.ehcache.management.service.EntityResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.management.ServiceExecutionException;
import org.terracotta.management.ServiceLocator;
import org.terracotta.management.resource.exceptions.ResourceRuntimeException;
import org.terracotta.management.resource.services.validator.RequestValidator;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * @author brandony
 */
@Path("/agents/cacheManagers/caches/configs")
public final class CacheConfigsResourceServiceImpl implements CacheConfigsResourceService {
  private static final Logger LOG = LoggerFactory.getLogger(CacheConfigsResourceServiceImpl.class);

  private final EntityResourceFactory entityResourceFactory;

  private final RequestValidator validator;

  public CacheConfigsResourceServiceImpl() {
    this.entityResourceFactory = ServiceLocator.locate(EntityResourceFactory.class);
    this.validator = ServiceLocator.locate(RequestValidator.class);
  }

  @Override
  public Collection<CacheConfigEntity> getCacheConfigs(UriInfo info) {
    LOG.debug(String.format("Invoking CacheConfigsResourceServiceImpl.getCacheConfigs: %s", info.getRequestUri()));

    validator.validateSafe(info);

    String cacheManagerNames = info.getPathSegments().get(1).getMatrixParameters().getFirst("names");
    Set<String> cmNames = cacheManagerNames == null ? null : new HashSet<String>(
        Arrays.asList(cacheManagerNames.split(",")));

    String cacheNames = info.getPathSegments().get(2).getMatrixParameters().getFirst("names");
    Set<String> cNames = cacheNames == null ? null : new HashSet<String>(Arrays.asList(cacheNames.split(",")));

    try {
      return entityResourceFactory.createCacheConfigEntities(cmNames, cNames);
    } catch (ServiceExecutionException see) {
      throw new ResourceRuntimeException("Failed to get cache configs", see, Response.Status.BAD_REQUEST.getStatusCode());
    }
  }

  @Override
  public Collection<CacheConfigEntity> getXMLCacheConfigs(UriInfo info) {
    return getCacheConfigs(info);
  }
}
