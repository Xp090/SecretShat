package me.xp090.secretshat.DataModels;

import java.io.Serializable;

/**
 * Created by Xp090 on 31/12/2017.
 */

public class SecurityOptions implements Serializable {

    public static final String SEC_NONE = "none";
    public static final String SEC_PATTERN = "pattern";
    public static final String SEC_PIN = "pin";

    public String SecurityMethod;
    public int PatternSize;
    public int PinLength;
    public int PassCode[][];

    public SecurityOptions() {
        SecurityMethod = SEC_NONE;
        PatternSize = 3;
        PinLength = 4;

    }


}
