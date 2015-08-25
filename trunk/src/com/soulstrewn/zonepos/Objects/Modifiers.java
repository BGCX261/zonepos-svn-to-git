package com.soulstrewn.zonepos.Objects;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

/**
 * User: jeff
 * Date: Mar 2, 2009
 * Time: 6:22:34 PM
 * No warranties, expressed or implied, including the warranty that it'll actually work, etc...
 */
public class Modifiers  implements Serializable {
    public static enum types { one, many }
    public String Name;
    public types Type;
    public List<Option> Options;
    public Modifiers()
    {
       Options = new ArrayList<Option>();
    }
}
