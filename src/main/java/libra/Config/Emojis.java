package libra.Config;

public class Emojis {

    public String Error;
    public String Ping;
    public String Success;
    public String Time;
    public String Invite;
    public String Volume;

    /*Insignias de Discord*/
    public String STAFF;
    public String PARTNER;
    public String HYPESQUAD;
    public String BUG_HUNTER_LEVEL_1;
    public String HYPESQUAD_BRAVERY;
    public String HYPESQUAD_BRILLIANCE;
    public String HYPESQUAD_BALANCE;
    public String EARLY_SUPPORTER;
    public String BUG_HUNTER_LEVEL_2;
    public String VERIFIED_DEVELOPER;
    public String CERTIFIED_MODERATOR;

    /*Insignias del comando Info*/
    public String BotBadge;
    public String OwnerBadge;

    public Emojis getEmojis() {
        Error = ":no_entry_sign:" + "  ";
        Ping = ":ping_pong:" + "  ";
        Success = "<:externalcontent:830859377463656479>" + "  ";
        Time = "<:Time:913509404614230016>" + "  ";
        Invite = "<:Invite:913508352108150784>" + "  ";
        Volume = ":speaker:" + "  ";

        STAFF = "<:Staff:913504958912024626>";
        PARTNER = "<:Partner:913504958362583061>";
        HYPESQUAD = "<:Hypesquad:913504958727462953>";
        BUG_HUNTER_LEVEL_1 = "<:Bug1:913504958777811045>";
        HYPESQUAD_BRAVERY = "<:Bravery:913504958400303185>";
        HYPESQUAD_BRILLIANCE = "<:Brillance:913504958404522066>";
        HYPESQUAD_BALANCE = "<:Balance:913504958526148629>";
        EARLY_SUPPORTER = "<:Early:913504958324813846>";
        BUG_HUNTER_LEVEL_2 = "<:Bug2:913504958597443614>";
        VERIFIED_DEVELOPER = "<:Developer:913504958526148630>";
        CERTIFIED_MODERATOR = "<:Moderator:913504958643601499>";

        BotBadge = "  " + "<:Bot1:913500818345439283><:Bot2:913500818584518706>";
        OwnerBadge = "  " + "<:Owner:913501281400815686>";

        return this;
    }

}
