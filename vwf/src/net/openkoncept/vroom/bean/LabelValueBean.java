package net.openkoncept.vroom.bean;

import java.io.Serializable;

/**
 * @author Farrukh Ijaz Muhammad Riaz (ijazfx@yahoo.com)
 */
public class LabelValueBean implements Serializable {

    private String label;
    private String value;

    public LabelValueBean() {
    }

    public LabelValueBean(String label, String value) {
        setLabel(label);
        setValue(value);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
