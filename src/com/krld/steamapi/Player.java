package com.krld.steamapi;

/**
 * Created by Andrey on 9/2/2014.
 */
public class Player {
    private final int accountId;

    private final String personaname;

    public Player(int accountId, String personaname) {
        this.accountId = accountId;
        this.personaname = personaname;
    }

    public String getPersonaname() {
        return personaname;
    }

    public int getAccountId() {
        return accountId;
    }

}
