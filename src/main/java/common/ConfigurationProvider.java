package common;

@FunctionalInterface
public interface ConfigurationProvider {
    public String getConf(String configName);
}
