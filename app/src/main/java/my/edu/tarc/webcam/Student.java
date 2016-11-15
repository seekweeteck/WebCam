package my.edu.tarc.webcam;

import android.net.Uri;

/**
 * Created by TAR UC on 11/15/2016.
 */

public class Student {
    private String id;
    private String name;
    private Uri photo;

    public Student(String id, String name, Uri photo) {
        this.id = id;
        this.name = name;
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getPhoto() {
        return photo;
    }

    public void setPhoto(Uri photo) {
        this.photo = photo;
    }
}
