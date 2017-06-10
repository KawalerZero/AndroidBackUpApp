package wolvesfromuz.androidbackupapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dawid Kuźmiński on 27.05.2017.
 */

public class Contact
{
    public String displayName;
    public ArrayList<String> phoneNos;
    public ArrayList<String> emails;

    public Contact()
    {
        phoneNos = new ArrayList<>();
        emails = new ArrayList<>();
    }
}
