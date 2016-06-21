package ir.treeco.aftabe2.API.Rest.Utils;

import com.google.gson.annotations.Expose;

/**
 * Created by root on 5/4/16.
 */
public class ContactsHolder {

    @Expose
    String name;

    @Expose
    String number;

    @Expose
    String email;


    public ContactsHolder(String name, String email, String number) {
        this.name = name;
        this.email = email;
        this.number = number;
    }

    public int getId() {
        return number.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return ((ContactsHolder) o).hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        String temp = number;
        temp = temp.replace("+", "");
        while (temp.length() > 1 && temp.charAt(0) == '0')
            temp = temp.substring(1);
        if (temp.length() > 2 && temp.substring(0, 2).equals("989"))
            temp = temp.substring(2);

        return temp.hashCode();
    }

    public String getNumber() {
        return number;
    }
}
