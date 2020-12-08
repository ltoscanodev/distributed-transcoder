package dev.ltoscano.transcoder.lib.service.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author ltosc
 */
public class ServiceInfoTable
{
    private final Map<String, Set<String>> serviceInfoTable;
    
    public ServiceInfoTable()
    {
        this.serviceInfoTable = new HashMap<>();
    }
    
    public synchronized void addService(ServiceInfo serviceInfo)
    {
        String key = serviceInfo.getServiceType().name();
        
        if(serviceInfoTable.containsKey(key))
        {
            serviceInfoTable.get(key).add(serviceInfo.getServiceUrl());
        }
        else
        {
            Set<String> serviceList = new HashSet<>();
            serviceList.add(serviceInfo.getServiceUrl());
            serviceInfoTable.put(key, serviceList);
        }
    }
    
    public synchronized Set<String> getServices(ServiceType serviceType)
    {
        String key = serviceType.name();
        
        if(!serviceInfoTable.containsKey(key))
        {
            throw new RuntimeException("No services with type '" + serviceType.name() + "' was found");
        }
        
        return serviceInfoTable.get(key);
    }
    
    public synchronized void removeService(ServiceType serviceType, String serviceUrl)
    {
        getServices(serviceType).remove(serviceUrl);
    }
    
    public synchronized void removeServices(ServiceType serviceType)
    {
        serviceInfoTable.remove(serviceType.name());
    }
    
    public synchronized void clearTable()
    {
        serviceInfoTable.clear();
    }
}
