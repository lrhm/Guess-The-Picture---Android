package ir.treeco.aftabe.API.Utils;

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

    @Expose (serialize = false, deserialize = false)
    String id;


    public ContactsHolder(String name, String email, String number, String id) {
        this.name = name;
        this.email = email;
        this.number = number;
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
