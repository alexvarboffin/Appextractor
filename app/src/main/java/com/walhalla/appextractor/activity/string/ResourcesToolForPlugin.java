package com.walhalla.appextractor.activity.string;

import com.walhalla.appextractor.R;
import com.walhalla.appextractor.activity.resources.ResType;

public class ResourcesToolForPlugin {
    public static final String ANIM = "anim";
    public static final String ANIMATOR = "animator";
    public static final String ARRAY = "array";
    public static final String ATTR = "attr";
    public static final String BOOL = "bool";
    public static final String COLOR = "color";
    public static final String DIMEN = "dimen";
    public static final String DRAWABLE = "drawable";
    public static final String ID = "id";
    public static final String INTEGER = "integer";
    public static final String INTERPOLATOR = "interpolator";
    public static final String LAYOUT = "layout";
    public static final String MENU = "menu";
    public static final String RAW = "raw";
    public static final String STRING = "string";
    public static final String STYLE = "style";
    public static final String STYLEABLE = "styleable";
    public static final String TRANSITION = "transition";
    public static final String XML = "xml";

    public static ResType getIconBySourceType(String resourceType) {

        if (XML.equals(resourceType)) {
            return ResType.XML;
        }
        if (STRING.equals(resourceType)) {
            return ResType.STRING;
        }
        return null;
    }
}
