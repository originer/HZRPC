package hzr.spring.provider.model;


public class ServiceModel {
	private String serviceName;
	private String startTime;
	private ServiceProvider serviceProvider;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProviders) {
		this.serviceProvider = serviceProviders;
	}
}
