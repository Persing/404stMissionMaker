package datasource;

public interface ConfigRepo extends Repo {
    String getStartFlag(String guild);

    void setStartFlag(String guild, String flag);

    Long getModMailChannel(String guild);

    void setModMailChannel(String guild, Long channelId);
}
