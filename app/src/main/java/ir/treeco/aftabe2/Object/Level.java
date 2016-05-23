package ir.treeco.aftabe2.Object;

public class Level {
    private int id;
    private String resources;
    private String javab;
    private boolean resolved;
    private String type;
    private String thumbnail;
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    public String getJavab() {
        return javab;
    }

    public void setJavab(String javab) {
        this.javab = javab;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
