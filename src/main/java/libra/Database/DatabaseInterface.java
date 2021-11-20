package libra.Database;

public interface DatabaseInterface {

    String getPrefix(long guildID);
    void setPrefix(long guildID, String newPrefix);

}
