package team16.filtershare;

/**
 * Created by chocho on 7/18/16.
 */
public enum FilterEffect {
    BRIGHTNESS ("brightness", 0),
    CONTRAST ("contrast", 0),
    SATURATION ("saturation", 50),
    SHARPEN ("sharpen", 0),
    TEMPERATURE ("temperature", 50),
    TINT ("tint", 0),
    VIGNETTE ("vignette", 0),
    GRAIN ("grain", 0);

    private String name;
    private int value;
    FilterEffect(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }
    public int getValue() {
        return value;
    }
    public void setName (String name) {
        this.name = name;
    }
    public void setValue (int value) {
        this.value = value;
    }
}
